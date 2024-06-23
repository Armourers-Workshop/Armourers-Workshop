package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.EntityRenderPatch;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.EpicFightEntityRendererPatch;
import moe.plushie.armourers_workshop.core.data.EntityDataStorage;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.DataStorage;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class EntityRenderData implements IAssociatedContainer, SkinBakery.IBakeListener {

    private final ArrayList<String> missingSkins = new ArrayList<>();
    private final ArrayList<Entry> armorSkins = new ArrayList<>();
    private final ArrayList<Entry> itemSkins = new ArrayList<>();

    private final HashMap<ISkinPaintType, IPaintColor> dyeColors = new HashMap<>();
    private final HashMap<ISkinPaintType, IPaintColor> lastDyeColors = new HashMap<>();
    private final HashMap<SkinDescriptor, BakedSkin> lastActiveSkins = new HashMap<>();

    private final NonNullList<ItemStack> lastWardrobeSlots = NonNullList.withSize(SkinSlotType.getTotalSize(), ItemStack.EMPTY);
    private final ArrayList<ItemStack> lastEquipmentSlots = new ArrayList<>();

    private final BitSet lastWardrobeFlags = new BitSet();
    private final HashSet<ISkinType> lastSkinTypes = new HashSet<>();
    private final HashSet<ISkinPartType> lastSkinPartTypes = new HashSet<>();

    private final Ticket loadTicket = Ticket.wardrobe();
    private final AnimationManager animationManager = new AnimationManager();
    private final SkinOverriddenManager overriddenManager = new SkinOverriddenManager();
    private final DataStorage dataStorage = new DataStorage();

    private EntityProfile wardrobeProfile = null;
    private ColorScheme colorScheme = ColorScheme.EMPTY;

    private boolean isRenderExtra = false;

    private boolean isLimitLimbs = false;
    private boolean isListening = false;

    private int version = 0;
    private int lastVersion = Integer.MAX_VALUE;

    private EntityRenderPatch<? super Entity> renderPatch;

    public EntityRenderData(Entity entity) {
    }

    @Nullable
    public static EntityRenderData of(@Nullable Entity entity) {
        if (entity != null) {
            return EntityDataStorage.of(entity).getRenderData().orElse(null);
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
        // check animation play status.
        this.animationManager.tick();
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

        animationManager.load(lastActiveSkins);
    }

    protected void loadEquipmentSlots(Entity entity) {
        int index = 0;
        for (var itemStack : entity.getOverrideSlots()) {
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
        var wardrobe = SkinWardrobe.of(entity);
        if (wardrobe == null) {
            this.isRenderExtra = false;
            this.wardrobeProfile = null;
            return;
        }
        var inventory = wardrobe.getInventory();
        var size = inventory.getContainerSize();
        for (var index = 0; index < size; ++index) {
            var itemStack = inventory.getItem(index);
            if (lastVersion != version || lastWardrobeSlots.get(index) != itemStack) {
                lastWardrobeSlots.set(index, itemStack);
                version += 1;
            }
        }
        var flags = wardrobe.getFlags();
        if (!lastWardrobeFlags.equals(flags)) {
            lastWardrobeFlags.clear();
            lastWardrobeFlags.or(flags);
            version += 1;
        }
        this.wardrobeProfile = wardrobe.getProfile();
    }

    protected void invalidateAll() {
        lastVersion = Integer.MAX_VALUE;
        isLimitLimbs = false;

        lastSkinTypes.clear();
        lastSkinPartTypes.clear();
        lastActiveSkins.clear();

        dyeColors.clear();
        missingSkins.clear();
        armorSkins.clear();
        itemSkins.clear();
        overriddenManager.clear();

        loadTicket.invalidate();
    }

    private void loadDyeSlots(Entity entity, BiConsumer<ISkinPaintType, ItemStack> consumer) {
        // ignore when wardrobe profile load fails.
        if (wardrobeProfile == null) {
            return;
        }
        for (var paintType : SkinSlotType.getSupportedPaintTypes()) {
            var itemStack = lastWardrobeSlots.get(SkinSlotType.getDyeSlotIndex(paintType));
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
        for (var itemStack : entity.getOverrideArmorSlots()) {
            consumer.accept(itemStack, 400 + i++, false);
        }
        // ignore when wardrobe profile load fails.
        if (wardrobeProfile == null) {
            return;
        }
        for (var slotType : wardrobeProfile.getSlots()) {
            if (slotType == SkinSlotType.DYE) {
                continue;
            }
            int index = slotType.getIndex();
            int size = slotType.getMaxSize();
            for (i = 0; i < size; ++i) {
                consumer.accept(lastWardrobeSlots.get(index + i), i * 10, false);
            }
        }
    }

    private void loadHandSlots(Entity entity, ItemConsumer consumer) {
        int i = 0;
        for (var itemStack : entity.getOverrideHandSlots()) {
            consumer.accept(itemStack, 400 + i++, true);
        }
    }

    private void loadWardrobeFlags(Entity entity) {
        var wardrobe = SkinWardrobe.of(entity);
        if (wardrobe == null) {
            return;
        }
        for (var slotType : EquipmentSlot.values()) {
            if (wardrobe.shouldRenderEquipment(slotType)) {
                overriddenManager.removeEquipment(slotType);
            } else {
                overriddenManager.addEquipment(slotType);
            }
        }
        this.isRenderExtra = wardrobe.shouldRenderExtra();
    }

    private void updateDye(ISkinPaintType paintType, ItemStack itemStack) {
        var paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            dyeColors.put(paintType, paintColor);
        }
    }

    private void updateSkin(ItemStack itemStack, float renderPriority, boolean isHeld) {
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return;
        }
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, loadTicket);
        lastActiveSkins.put(descriptor, bakedSkin);
        if (bakedSkin == null) {
            missingSkins.add(descriptor.getIdentifier());
            return;
        }
        ISkinType type = bakedSkin.getType();
        // If held a skin of armor type, nothing happen
        if (type instanceof ISkinArmorType && !isHeld) {
            armorSkins.add(new Entry(itemStack, descriptor, bakedSkin, colorScheme, renderPriority, false));
            loadSkinInfo(bakedSkin);
        }
        if (type instanceof ISkinToolType || type == SkinTypes.ITEM) {
            itemSkins.add(new Entry(itemStack, descriptor, bakedSkin, colorScheme, renderPriority, isHeld));
            loadSkinInfo(bakedSkin);
        }
    }

    private void loadSkinInfo(BakedSkin skin) {
        // check all part status, some skin only one part, but overridden all the models/overlays
        var properties = skin.getSkin().getProperties();
        overriddenManager.merge(properties);
        if (!isLimitLimbs) {
            isLimitLimbs = properties.get(SkinProperty.LIMIT_LEGS_LIMBS);
        }
        // collect the skin and skin part type info.
        lastSkinTypes.add(skin.getType());
        for (var skinPart : skin.getParts()) {
            lastSkinPartTypes.add(skinPart.getType());
        }
    }

    private SkinDescriptor getEmbeddedSkin(ItemStack itemStack, boolean replaceSkinItem) {
        // for skin item, we don't consider it an embedded skin.
        if (!replaceSkinItem && itemStack.is(ModItems.SKIN.get())) {
            return SkinDescriptor.EMPTY;
        }
        var target = SkinDescriptor.of(itemStack);
        if (target.getType() == SkinTypes.ITEM_BOAT || target.getType() == SkinTypes.ITEM_FISHING || target.getType() == SkinTypes.HORSE) {
            return SkinDescriptor.EMPTY;
        }
        return target;
    }

    public Iterable<Entry> getItemSkins(ItemStack itemStack, boolean replaceSkinItem) {
        var target = getEmbeddedSkin(itemStack, replaceSkinItem);
        if (target.isEmpty()) {
            // the item stack is not embedded skin, using matching pattern,
            // only need to find the first matching skin by item.
            for (var entry : itemSkins) {
                if (!entry.isHeld && entry.getDescriptor().accept(itemStack)) {
                    return Collections.singletonList(entry);
                }
            }
        } else {
            // the item stack is embedded skin, find the baked skin for matched descriptor.
            for (EntityRenderData.Entry entry : itemSkins) {
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
        // use disable is the options.
        if (!ModConfig.Client.enableSkinLimitLimbs) {
            return false;
        }
        // in EF doesn't need to limit limbs.
        if (renderPatch instanceof EpicFightEntityRendererPatch) {
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

    public void setRenderPatch(EntityRenderPatch<? super Entity> renderPatch) {
        this.renderPatch = renderPatch;
    }

    public EntityRenderPatch<? super Entity> getRenderPatch() {
        return renderPatch;
    }

    public Collection<ISkinType> getUsingTypes() {
        return lastSkinTypes;
    }

    public Collection<ISkinPartType> getUsingPartTypes() {
        return lastSkinPartTypes;
    }

    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        return dataStorage.getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(T value, IAssociatedContainerKey<T> key) {
        dataStorage.setAssociatedObject(value, key);
    }

    protected interface ItemConsumer {
        void accept(ItemStack itemStack, float priority, boolean flag);
    }

    public static class Entry {

        protected final ItemStack itemStack;
        protected final SkinDescriptor descriptor;
        protected final BakedSkin bakedSkin;
        protected final ColorScheme bakedScheme;
        protected final boolean isHeld;
        protected final float renderPriority;

        public Entry(ItemStack itemStack, SkinDescriptor descriptor, BakedSkin bakedSkin, ColorScheme entityScheme, float renderPriority, boolean isHeld) {
            this.itemStack = itemStack;
            this.descriptor = descriptor;
            this.bakedSkin = bakedSkin;
            this.bakedScheme = baking(descriptor.getColorScheme(), entityScheme, isHeld);
            this.renderPriority = renderPriority;
            this.isHeld = isHeld;
        }

        public static ColorScheme baking(ColorScheme skinScheme, ColorScheme entityScheme, boolean isHeld) {
            // when player held item we can't use the entity scheme.
            if (isHeld) {
                return skinScheme;
            }
            if (skinScheme.isEmpty()) {
                return entityScheme;
            }
            if (entityScheme.isEmpty()) {
                return skinScheme;
            }
            var bakedScheme = skinScheme.copy();
            bakedScheme.setReference(entityScheme);
            return bakedScheme;
        }

        public float getRenderPriority() {
            return renderPriority;
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

