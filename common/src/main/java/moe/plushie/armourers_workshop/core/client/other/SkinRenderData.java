package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.common.IItemStackProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.*;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightContext;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.ItemStackProvider;
import moe.plushie.armourers_workshop.core.data.SkinDataStorage;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.BiConsumer;

@Environment(value = EnvType.CLIENT)
public class SkinRenderData implements SkinBakery.IBakeListener {

    private final ArrayList<String> missingSkins = new ArrayList<>();
    private final ArrayList<Entry> armorSkins = new ArrayList<>();
    private final ArrayList<Entry> itemSkins = new ArrayList<>();

    private final HashMap<ISkinPaintType, IPaintColor> dyeColors = new HashMap<>();
    private final HashMap<ISkinPaintType, IPaintColor> lastDyeColors = new HashMap<>();

    private final NonNullList<ItemStack> lastWardrobeSlots = NonNullList.withSize(SkinSlotType.getTotalSize(), ItemStack.EMPTY);
    private final ArrayList<ItemStack> lastEquipmentSlots = new ArrayList<>();

    private final BitSet lastWardrobeFlags = new BitSet();

    private final IItemStackProvider itemProvider = ItemStackProvider.getInstance();
    private final SkinOverriddenManager overriddenManager = new SkinOverriddenManager();

    private ColorScheme colorScheme = ColorScheme.EMPTY;

    private boolean isRenderExtra = false;

    private boolean isActiveWardrobe = false;
    private boolean isLimitLimbs = false;
    private boolean isListening = false;

    private int version = 0;
    private int lastVersion = Integer.MAX_VALUE;

    public EpicFlightContext epicFlightContext;

    public SkinRenderData(EntityType<?> entityType) {
    }

    @Nullable
    public static SkinRenderData of(@Nullable Entity entity) {
        if (entity != null) {
            return SkinDataStorage.getRenderData(entity).orElse(null);
        }
        return null;
    }

    @Override
    public void didBake(String identifier, BakedSkin bakedSkin) {
        if (missingSkins.contains(identifier)) {
            RenderSystem.call(this::invalidateAll);
        }
    }

    public void tick(Entity entity) {
        this.loadEquipmentSlots(entity);
        this.loadWardrobeSlots(entity);
        if (this.lastVersion != this.version) {
            this.reload(entity);
            this.lastVersion = this.version;
        }
    }

    protected void reload(Entity entity) {
        invalidateAll();

        loadDyeSlots(entity, this::updateDye);
        loadHandSlots(entity, this::updateSkin);
        loadArmorSlots(entity, this::updateSkin);

        loadWardrobeFlags(entity);

        if (missingSkins.isEmpty()) {
            if (isListening) {
                SkinBakery.getInstance().removeListener(this);
                isListening = false;
            }
        } else {
            if (!isListening) {
                SkinBakery.getInstance().addListener(this);
                isListening = true;
            }
        }
    }

    protected void loadEquipmentSlots(Entity entity) {
        int index = 0;
        for (ItemStack itemStack : itemProvider.getAllSlots(entity)) {
            if (index >= lastEquipmentSlots.size()) {
                lastEquipmentSlots.add(itemStack);
                version += 1;
            } else if (lastVersion != version || lastEquipmentSlots.get(index) != itemStack) {
                lastEquipmentSlots.set(index, itemStack);
                version += 1;
            }
            index += 1;
        }
        // clear expired item stack
        while (index < lastEquipmentSlots.size()) {
            lastEquipmentSlots.remove(index);
        }
    }

    protected void loadWardrobeSlots(Entity entity) {
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe == null) {
            this.isRenderExtra = false;
            this.isActiveWardrobe = false;
            return;
        }
        Container inventory = wardrobe.getInventory();
        int size = inventory.getContainerSize();
        for (int index = 0; index < size; ++index) {
            ItemStack itemStack = inventory.getItem(index);
            if (lastVersion != version || lastWardrobeSlots.get(index) != itemStack) {
                lastWardrobeSlots.set(index, itemStack);
                version += 1;
            }
        }
        BitSet flags = wardrobe.getFlags();
        if (!lastWardrobeFlags.equals(flags)) {
            lastWardrobeFlags.clear();
            lastWardrobeFlags.or(flags);
            version += 1;
        }
        this.isActiveWardrobe = true;
    }

    protected void invalidateAll() {
        lastVersion = Integer.MAX_VALUE;
        isLimitLimbs = false;

        dyeColors.clear();
        missingSkins.clear();
        armorSkins.clear();
        itemSkins.clear();
        overriddenManager.clear();
    }

    private void loadDyeSlots(Entity entity, BiConsumer<ISkinPaintType, ItemStack> consumer) {
        if (!isActiveWardrobe) {
            return;
        }
        for (ISkinPaintType paintType : SkinSlotType.getSupportedPaintTypes()) {
            ItemStack itemStack = lastWardrobeSlots.get(SkinSlotType.getDyeSlotIndex(paintType));
            consumer.accept(paintType, itemStack);
        }
        if (!lastDyeColors.equals(dyeColors)) {
            colorScheme = new ColorScheme();
            lastDyeColors.clear();
            dyeColors.forEach((paintType, paintColor) -> {
                lastDyeColors.put(paintType, paintColor);
                colorScheme.setColor(paintType, paintColor);
            });
        }
    }

    private void loadArmorSlots(Entity entity, ItemConsumer consumer) {
        int i = 0;
        for (ItemStack itemStack : itemProvider.getArmorSlots(entity)) {
            consumer.accept(itemStack, i, false);
        }
        if (!isActiveWardrobe) {
            return;
        }
        for (SkinSlotType slotType : SkinSlotType.values()) {
            if (slotType == SkinSlotType.DYE) {
                continue;
            }
            int index = slotType.getIndex();
            int size = slotType.getMaxSize();
            for (i = 0; i < size; ++i) {
                consumer.accept(lastWardrobeSlots.get(index + i), i, false);
            }
        }
    }

    private void loadHandSlots(Entity entity, ItemConsumer consumer) {
        int i = 0;
        for (ItemStack itemStack : itemProvider.getHandSlots(entity)) {
            consumer.accept(itemStack, i, true);
        }
    }

    private void loadWardrobeFlags(Entity entity) {
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe == null) {
            return;
        }
        for (EquipmentSlot slotType : EquipmentSlot.values()) {
            if (wardrobe.shouldRenderEquipment(slotType)) {
                overriddenManager.removeEquipment(slotType);
            } else {
                overriddenManager.addEquipment(slotType);
            }
        }
        this.isRenderExtra = wardrobe.shouldRenderExtra();
    }

    private void updateDye(ISkinPaintType paintType, ItemStack itemStack) {
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            dyeColors.put(paintType, paintColor);
        }
    }

    private void updateSkin(ItemStack itemStack, int slotIndex, boolean isHeld) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return;
        }
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin == null) {
            missingSkins.add(descriptor.getIdentifier());
            return;
        }
        ISkinType type = bakedSkin.getType();
        // If held a skin of armor type, nothing happen
        if (type instanceof ISkinArmorType && !isHeld) {
            armorSkins.add(new Entry(itemStack, descriptor, bakedSkin, colorScheme, slotIndex, false));
            loadSkinPart(bakedSkin);
        }
        if (type instanceof ISkinToolType || type == SkinTypes.ITEM) {
            itemSkins.add(new Entry(itemStack, descriptor, bakedSkin, colorScheme, slotIndex, isHeld));
            loadSkinPart(bakedSkin);
        }
    }

    private void loadSkinPart(BakedSkin skin) {
        // check all part status, some skin only one part, but overridden all the models/overlays
        SkinProperties properties = skin.getSkin().getProperties();
        for (ISkinPartType partType : skin.getType().getParts()) {
            if (partType.isModelOverridden(properties)) {
                overriddenManager.addModel(partType);
            }
            if (partType.isOverlayOverridden(properties)) {
                overriddenManager.addOverlay(partType);
            }
            if (partType == SkinPartTypes.BIPPED_SKIRT && !isLimitLimbs) {
                isLimitLimbs = properties.get(SkinProperty.MODEL_LEGS_LIMIT_LIMBS);
            }
        }
    }

    private SkinDescriptor getEmbeddedSkin(ItemStack itemStack, boolean replaceSkinItem) {
        // for skin item, we don't consider it an embedded skin.
        if (!replaceSkinItem && itemStack.getItem() == ModItems.SKIN.get()) {
            return SkinDescriptor.EMPTY;
        }
        return SkinDescriptor.of(itemStack);
    }

    public Iterable<Entry> getItemSkins(ItemStack itemStack, boolean replaceSkinItem) {
        SkinDescriptor target = getEmbeddedSkin(itemStack, replaceSkinItem);
        if (target.isEmpty()) {
            // the item stack is not embedded skin, using matching pattern,
            // only need to find the first matching skin by item.
            for (Entry entry : itemSkins) {
                if (!entry.isHeld && entry.getDescriptor().accept(itemStack)) {
                    return Collections.singletonList(entry);
                }
            }
        } else {
            // the item stack is embedded skin, find the baked skin for matched descriptor.
            for (SkinRenderData.Entry entry : itemSkins) {
                if (entry.getDescriptor().equals(target)) {
                    return Collections.singletonList(entry);
                }
            }
        }
        return Collections.emptyList();
    }

    public Iterable<Entry> getArmorSkins() {
        return armorSkins;
    }

    public Iterable<Entry> getItemSkins() {
        return itemSkins;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public boolean isLimitLimbs() {
        // in EF doesn't need to limit limbs.
        if (epicFlightContext != null) {
            return false;
        }
        return isLimitLimbs;
    }

    public SkinOverriddenManager getOverriddenManager() {
        return overriddenManager;
    }

    public boolean shouldRenderExtra() {
        return isRenderExtra;
    }

    protected interface ItemConsumer {
        void accept(ItemStack itemStack, int slode, boolean flag);
    }

    public static class Entry {

        protected final ItemStack itemStack;
        protected final SkinDescriptor descriptor;
        protected final BakedSkin bakedSkin;
        protected final ColorScheme bakedScheme;
        protected final boolean isHeld;
        protected final int slotIndex;

        public Entry(ItemStack itemStack, SkinDescriptor descriptor, BakedSkin bakedSkin, ColorScheme entityScheme, int slotIndex, boolean isHeld) {
            this.itemStack = itemStack;
            this.descriptor = descriptor;
            this.bakedSkin = bakedSkin;
            this.bakedScheme = baking(descriptor.getColorScheme(), entityScheme);
            this.slotIndex = slotIndex;
            this.isHeld = isHeld;
        }

        public static ColorScheme baking(ColorScheme skinScheme, ColorScheme entityScheme) {
            if (skinScheme.isEmpty()) {
                return entityScheme;
            }
            if (entityScheme.isEmpty()) {
                return skinScheme;
            }
            ColorScheme bakedScheme = skinScheme.copy();
            bakedScheme.setReference(entityScheme);
            return bakedScheme;
        }

        public int getSlotIndex() {
            return slotIndex;
        }

        public BakedSkin getBakedSkin() {
            return bakedSkin;
        }

        public ColorScheme getBakedScheme() {
            return bakedScheme;
        }

        public SkinDescriptor getDescriptor() {
            return descriptor;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }
    }
}

