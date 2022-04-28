package moe.plushie.armourers_workshop.core.render.other;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.*;
import moe.plushie.armourers_workshop.core.capability.SkinDataStorage;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.core.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.init.common.ModCompatible;
import moe.plushie.armourers_workshop.init.common.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;


@OnlyIn(Dist.CLIENT)
public class SkinRenderData implements SkinBakery.IBakeListener {

    private static final ImmutableMap<ISkinPartType, EquipmentSlotType> PART_TO_EQUIPMENT_SLOTS = new ImmutableMap.Builder<ISkinPartType, EquipmentSlotType>()
            .put(SkinPartTypes.BIPED_HEAD, EquipmentSlotType.HEAD)
            .put(SkinPartTypes.BIPED_CHEST, EquipmentSlotType.CHEST)
            .put(SkinPartTypes.BIPED_LEFT_ARM, EquipmentSlotType.CHEST)
            .put(SkinPartTypes.BIPED_RIGHT_ARM, EquipmentSlotType.CHEST)
            .put(SkinPartTypes.BIPED_LEFT_FOOT, EquipmentSlotType.FEET)
            .put(SkinPartTypes.BIPED_RIGHT_FOOT, EquipmentSlotType.FEET)
            .put(SkinPartTypes.BIPED_LEFT_LEG, EquipmentSlotType.LEGS)
            .put(SkinPartTypes.BIPED_RIGHT_LEG, EquipmentSlotType.LEGS)
            .build();

    private final ArrayList<String> missingSkins = new ArrayList<>();
    private final ArrayList<Entry> armorSkins = new ArrayList<>();
    private final ArrayList<Entry> itemSkins = new ArrayList<>();

    private final HashMap<ISkinPaintType, IPaintColor> dyeColors = new HashMap<>();
    private final HashMap<ISkinPaintType, IPaintColor> lastDyeColors = new HashMap<>();

    private final HashSet<ISkinPartType> hasOverriddenModelParts = new HashSet<>();
    private final HashSet<ISkinPartType> hasOverriddenOverlayParts = new HashSet<>();
    private final HashSet<ISkinPartType> hasParts = new HashSet<>();

    private final HashSet<EquipmentSlotType> hasOverriddenEquipmentSlots = new HashSet<>();

    private final NonNullList<ItemStack> lastWardrobeSlots = NonNullList.withSize(SkinSlotType.getTotalSize(), ItemStack.EMPTY);
    private final NonNullList<ItemStack> lastEquipmentSlots = NonNullList.withSize(EquipmentSlotType.values().length, ItemStack.EMPTY);

    private ColorScheme colorScheme = ColorScheme.EMPTY;

    private boolean isActiveWardrobe = false;
    private boolean isLimitLimbs = false;
    private boolean isListening = false;

    private int version = 0;
    private int lastVersion = Integer.MAX_VALUE;

    public SkinRenderData() {
    }

    @Nullable
    public static SkinRenderData of(@Nullable Entity entity) {
        if (entity != null) {
            return SkinDataStorage.getRenderData(entity);
        }
        return null;
    }

    @Override
    public void didBake(String identifier, BakedSkin bakedSkin) {
        if (missingSkins.contains(identifier)) {
            RenderUtils.call(this::invalidateAll);
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
        for (ItemStack itemStack : Iterables.concat(ModCompatible.getHandSlots(entity), ModCompatible.getArmorSlots(entity))) {
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
            this.isActiveWardrobe = false;
            return;
        }
        IInventory inventory = wardrobe.getInventory();
        int size = inventory.getContainerSize();
        for (int index = 0; index < size; ++index) {
            ItemStack itemStack = inventory.getItem(index);
            if (lastVersion != version || lastWardrobeSlots.get(index) != itemStack) {
                lastWardrobeSlots.set(index, itemStack);
                version += 1;
            }
        }
        for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
            boolean shows = wardrobe.shouldRenderEquipment(slotType);
            if (shows == hasOverriddenEquipmentSlots.contains(slotType)) {
                if (shows) {
                    hasOverriddenEquipmentSlots.remove(slotType);
                } else {
                    hasOverriddenEquipmentSlots.add(slotType);
                }
            }
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
        hasParts.clear();
        hasOverriddenModelParts.clear();
        hasOverriddenOverlayParts.clear();
    }

    private void loadDyeSlots(Entity entity, BiConsumer<ISkinPaintType, ItemStack> consumer) {
        if (!isActiveWardrobe) {
            return;
        }
        ISkinPaintType[] dyeSlots = SkinSlotType.getDyeSlots();
        for (int i = 0; i < dyeSlots.length; ++i) {
            ItemStack itemStack = lastWardrobeSlots.get(SkinSlotType.DYE.getIndex() + i);
            consumer.accept(dyeSlots[i], itemStack);
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

    private void loadArmorSlots(Entity entity, BiConsumer<ItemStack, Boolean> consumer) {
        ModCompatible.getArmorSlots(entity).forEach(itemStack -> consumer.accept(itemStack, false));
        if (!isActiveWardrobe) {
            return;
        }
        int size = SkinSlotType.DYE.getIndex();
        for (int i = 0; i < size; ++i) {
            consumer.accept(lastWardrobeSlots.get(i), false);
        }
    }

    private void loadHandSlots(Entity entity, BiConsumer<ItemStack, Boolean> consumer) {
        ModCompatible.getHandSlots(entity).forEach(itemStack -> consumer.accept(itemStack, true));
    }

    private void updateDye(ISkinPaintType paintType, ItemStack itemStack) {
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            dyeColors.put(paintType, paintColor);
        }
    }

    private void updateSkin(ItemStack itemStack, boolean isHeld) {
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
            armorSkins.add(new Entry(descriptor, bakedSkin, colorScheme, false));
            loadSkinPart(bakedSkin);
        }
        if (type instanceof ISkinToolType || type == SkinTypes.ITEM) {
            itemSkins.add(new Entry(descriptor, bakedSkin, colorScheme, isHeld));
            loadSkinPart(bakedSkin);
        }
    }

    private void loadSkinPart(BakedSkin skin) {
        // check exists part
        for (BakedSkinPart part : skin.getSkinParts()) {
            hasParts.add(part.getType());
        }
        // check all part status, some skin only one part, but overridden all the models/overlays
        SkinProperties properties = skin.getSkin().getProperties();
        for (ISkinPartType partType : skin.getType().getParts()) {
            if (partType.isModelOverridden(properties)) {
                hasOverriddenModelParts.add(partType);
            }
            if (partType.isOverlayOverridden(properties)) {
                hasOverriddenOverlayParts.add(partType);
            }
            if (partType == SkinPartTypes.BIPED_SKIRT && !isLimitLimbs) {
                isLimitLimbs = properties.get(SkinProperty.MODEL_LEGS_LIMIT_LIMBS);
            }
        }
    }

    private SkinDescriptor getEmbeddedSkin(ItemStack itemStack, boolean replaceSkinItem) {
        // for skin item, we don't consider it an embedded skin.
        if (!replaceSkinItem && itemStack.getItem() == ModItems.SKIN) {
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
        return isLimitLimbs;
    }

    public boolean hasPart(ISkinPartType partType) {
        return hasParts.contains(partType);
    }

    public boolean hasOverriddenPart(ISkinPartType partType) {
        return hasOverriddenModelParts.contains(partType);
    }

    public boolean hasOverriddenEquipmentPart(ISkinPartType partType) {
        if (hasOverriddenOverlayParts.contains(partType)) {
            return true;
        }
        EquipmentSlotType slotType = PART_TO_EQUIPMENT_SLOTS.get(partType);
        if (slotType != null) {
            return hasOverriddenEquipmentSlots.contains(slotType);
        }
        return false;
    }

    public static class Entry {

        protected final SkinDescriptor descriptor;
        protected final BakedSkin bakedSkin;
        protected final ColorScheme bakedScheme;
        protected final boolean isHeld;

        public Entry(SkinDescriptor descriptor, BakedSkin bakedSkin, ColorScheme entityScheme, boolean isHeld) {
            this.descriptor = descriptor;
            this.bakedSkin = bakedSkin;
            this.bakedScheme = baking(descriptor.getColorScheme(), entityScheme);
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

        public BakedSkin getBakedSkin() {
            return bakedSkin;
        }

        public ColorScheme getBakedScheme() {
            return bakedScheme;
        }

        public SkinDescriptor getDescriptor() {
            return descriptor;
        }
    }
}
