package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.DataStorage;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class EntitySlotsHandler<T> implements IAssociatedContainer, SkinBakery.IBakeListener {

    private final SlotProvider<T> entityProvider;
    private final WardrobeProvider wardrobeProvider;

    private final ArrayList<String> missingSkins = new ArrayList<>();

    private final HashSet<ISkinType> lastSkinTypes = new HashSet<>();
    private final HashSet<ISkinPartType> lastSkinPartTypes = new HashSet<>();

    private final ArrayList<EntitySlot> allSkins = new ArrayList<>();
    private final ArrayList<EntitySlot> armorSkins = new ArrayList<>();
    private final ArrayList<EntitySlot> itemSkins = new ArrayList<>();
    private final ArrayList<EntitySlot> containerSkins = new ArrayList<>();

    private final HashMap<SkinDescriptor, BakedSkin> activeSkins = new HashMap<>();
    private final HashMap<SkinDescriptor, BakedSkin> animatedSkins = new HashMap<>();

    private final Ticket loadTicket = Ticket.wardrobe();
    private final AnimationManager animationManager = new AnimationManager();
    private final SkinOverriddenManager overriddenManager = new SkinOverriddenManager();

    private final DataStorage dataStorage = new DataStorage();

    private int version = 0;
    private int lastVersion = Integer.MAX_VALUE;

    private boolean isLimitLimbs = false;
    private boolean isListening = false;

    protected EntitySlotsHandler(SlotProvider<T> entityProvider, WardrobeProvider wardrobeProvider) {
        this.entityProvider = entityProvider;
        this.wardrobeProvider = wardrobeProvider;
    }

    protected void tick(T source, @Nullable SkinWardrobe wardrobe) {
        tickSlots(source, wardrobe);
        animationManager.tick(source, TickUtils.animationTicks());
    }

    private void tickSlots(T source, @Nullable SkinWardrobe wardrobe) {
        // ..
        if (wardrobeProvider.tick(wardrobe)) {
            version += 1;
        }
        if (entityProvider.tick(source)) {
            version += 1;
        }
        if (lastVersion != version) {
            reloadSlots(source, wardrobe);
            lastVersion = version;
        }
    }

    private void reloadSlots(T source, @Nullable SkinWardrobe wardrobe) {
        invalidateAll();

        wardrobeProvider.loadDye(wardrobe);
        wardrobeProvider.loadWardrobeFlags(wardrobe, overriddenManager);

        entityProvider.load(source, this::loadSkin);
        wardrobeProvider.load(wardrobe, this::loadSkin);

        loadSkinInfos();
        loadSkinAnimations(source);
        loadMissingSkinIfNeeded();
    }

    private void invalidateAll() {
        lastVersion = Integer.MAX_VALUE;

        isLimitLimbs = false;

        lastSkinTypes.clear();
        lastSkinPartTypes.clear();

        missingSkins.clear();
        armorSkins.clear();
        itemSkins.clear();
        containerSkins.clear();
        allSkins.clear();
        activeSkins.clear();
        animatedSkins.clear();
        overriddenManager.clear();

        loadTicket.invalidate();
    }

    private void loadSkin(ItemStack itemStack, float renderPriority, EntitySlot.Type slotType) {
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return;
        }
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, loadTicket);
        if (bakedSkin == null) {
            missingSkins.add(descriptor.getIdentifier());
            return;
        }
        var slot = new EntitySlot(itemStack, descriptor, bakedSkin, wardrobeProvider.colorScheme, renderPriority, slotType);
        switch (slotType) {
            case IN_HELD -> {
                // If held a skin of armor type, nothing happen
                if (bakedSkin.getType() instanceof ISkinToolType || bakedSkin.getType() == SkinTypes.ITEM) {
                    allSkins.add(slot);
                    itemSkins.add(slot);
                }
            }
            case IN_EQUIPMENT, IN_WARDROBE -> {
                if (bakedSkin.getType() instanceof ISkinToolType || bakedSkin.getType() == SkinTypes.ITEM) {
                    allSkins.add(slot);
                    itemSkins.add(slot);
                } else {
                    allSkins.add(slot);
                    armorSkins.add(slot);
                }
            }
            case IN_CONTAINER -> {
                allSkins.add(slot);
                containerSkins.add(slot);
            }
        }
    }

    private void loadSkinInfos() {
        for (var entry : allSkins) {
            // check all part status, some skin only one part, but overridden all the models/overlays
            var skin = entry.getBakedSkin();
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
            activeSkins.put(entry.getDescriptor(), skin);
        }
    }

    private void loadMissingSkinIfNeeded() {
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

    private void loadSkinAnimations(T source) {
        // the armour/container skin will play parallel animation unconditionally.
        armorSkins.forEach(this::loadSkinAnimation);
        containerSkins.forEach(this::loadSkinAnimation);

        // the item skin will play parallel animation when item is active.
        if (entityProvider instanceof EntityProvider entityProvider1) {
            boolean isMannequinHand = source instanceof MannequinEntity;
            for (var itemStack : entityProvider1.handSlots) {
                getItemSkins(itemStack, isMannequinHand).forEach(this::loadSkinAnimation);
            }
        }

        // submit data into animation manager.
        animationManager.load(activeSkins);
        animationManager.active(animatedSkins);
    }

    private void loadSkinAnimation(EntitySlot slot) {
        animatedSkins.put(slot.getDescriptor(), slot.getBakedSkin());
    }

    @Override
    public void didBake(String identifier, BakedSkin bakedSkin) {
        if (missingSkins.contains(identifier)) {
            RenderSystem.call(this::invalidateAll);
        }
    }

    private SkinDescriptor getEmbeddedSkin(ItemStack itemStack, boolean inMannequinHand) {
        // for skin item, we don't consider it an embedded skin.
        if (!inMannequinHand && itemStack.is(ModItems.SKIN.get())) {
            return SkinDescriptor.EMPTY;
        }
        var target = SkinDescriptor.of(itemStack);
        if (target.getType() == SkinTypes.BOAT || target.getType() == SkinTypes.ITEM_FISHING || target.getType() == SkinTypes.HORSE) {
            return SkinDescriptor.EMPTY;
        }
        return target;
    }


    public List<EntitySlot> getItemSkins(ItemStack itemStack, boolean inMannequinHand) {
        var target = getEmbeddedSkin(itemStack, inMannequinHand);
        if (target.isEmpty()) {
            // the item stack is not embedded skin, using matching pattern,
            // only need to find the first matching skin by item.
            for (var entry : itemSkins) {
                if (entry.slotType != EntitySlot.Type.IN_HELD && entry.getDescriptor().accept(itemStack)) {
                    return Collections.singletonList(entry);
                }
            }
        } else {
            // the item stack is embedded skin, find the baked skin for matched descriptor.
            for (var entry : itemSkins) {
                if (entry.getDescriptor().equals(target)) {
                    return Collections.singletonList(entry);
                }
            }
        }
        return Collections.emptyList();
    }

    public List<EntitySlot> getItemSkins() {
        return itemSkins;
    }

    public List<EntitySlot> getArmorSkins() {
        return armorSkins;
    }

    public List<EntitySlot> getAllSkins() {
        return allSkins;
    }

    public ColorScheme getColorScheme() {
        return wardrobeProvider.colorScheme;
    }

    public boolean isLimitLimbs() {
        // use disable is the options.
        if (!ModConfig.Client.enableSkinLimitLimbs) {
            return false;
        }
        return isLimitLimbs;
    }

    public SkinOverriddenManager getOverriddenManager() {
        return overriddenManager;
    }

    public boolean shouldRenderExtra() {
        return wardrobeProvider.enableExtraRenderer;
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
    public <V> V getAssociatedObject(IAssociatedContainerKey<V> key) {
        return dataStorage.getAssociatedObject(key);
    }

    @Override
    public <V> void setAssociatedObject(V value, IAssociatedContainerKey<V> key) {
        dataStorage.setAssociatedObject(value, key);
    }

    protected static abstract class SlotProvider<T> {

        protected List<ItemStack> slots = new ArrayList<>();
        protected List<ItemStack> lastSlots = new ArrayList<>();

        public boolean tick(T source) {
            var oldSlots = lastSlots;
            var newSlots = slots;
            newSlots.clear();
            collect(source, newSlots);
            slots = oldSlots;
            lastSlots = newSlots;
            return !newSlots.equals(oldSlots);
        }

        protected abstract void load(T source, SlotConsumer consumer);

        protected abstract void collect(T source, List<ItemStack> collector);
    }

    protected interface SlotConsumer {
        void accept(ItemStack itemStack, float priority, EntitySlot.Type slotType);

    }

    protected static class WardrobeProvider extends SlotProvider<SkinWardrobe> {

        protected final HashMap<ISkinPaintType, IPaintColor> dyeColors = new HashMap<>();
        protected final HashMap<ISkinPaintType, IPaintColor> lastDyeColors = new HashMap<>();

        protected BitSet wardrobeFlags = new BitSet();
        protected ColorScheme colorScheme = ColorScheme.EMPTY;
        protected EntityProfile profile = null;

        protected boolean enableExtraRenderer = false;

        @Override
        public boolean tick(SkinWardrobe wardrobe) {
            if (wardrobe == null) {
                profile = null;
                enableExtraRenderer = false;
                return false;
            }
            var result = super.tick(wardrobe);
            var flags = wardrobe.getFlags();
            if (!wardrobeFlags.equals(flags)) {
                wardrobeFlags.clear();
                wardrobeFlags.or(flags);
                result = true;
            }
            profile = wardrobe.getProfile();
            return result;
        }

        protected void loadDye(SkinWardrobe wardrobe) {
            // ignore when wardrobe profile load fails.
            if (wardrobe == null || profile == null) {
                return;
            }
            dyeColors.clear();
            for (var paintType : SkinSlotType.getSupportedPaintTypes()) {
                var itemStack = lastSlots.get(SkinSlotType.getDyeSlotIndex(paintType));
                var paintColor = ColorUtils.getColor(itemStack);
                if (paintColor != null) {
                    dyeColors.put(paintType, paintColor);
                }
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

        protected void loadWardrobeFlags(SkinWardrobe wardrobe, SkinOverriddenManager overriddenManager) {
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
            enableExtraRenderer = wardrobe.shouldRenderExtra();
        }

        @Override
        protected void load(SkinWardrobe wardrobe, SlotConsumer consumer) {
            // ignore when wardrobe profile load fails.
            if (wardrobe == null || profile == null) {
                return;
            }
            // load normal skin, and the record used skin slots.
            var usedSlots = new HashSet<SkinSlotType>();
            for (var slotType : profile.getSlots()) {
                if (slotType == SkinSlotType.DYE) {
                    return;
                }
                for (int i = 0; i < slotType.getMaxSize(); ++i) {
                    var itemStack = lastSlots.get(slotType.getIndex() + i);
                    var descriptor = SkinDescriptor.of(itemStack);
                    if (!descriptor.isEmpty()) {
                        usedSlots.add(slotType); // mark the slot is used.
                    }
                    consumer.accept(itemStack, i * 10, EntitySlot.Type.IN_WARDROBE);
                }
            }
            // load default skin when not user provided.
            var slotType = SkinSlotType.DEFAULT;
            for (int i = 0; i < slotType.getMaxSize(); ++i) {
                var itemStack = lastSlots.get(slotType.getIndex() + i);
                var descriptor = SkinDescriptor.of(itemStack);
                if (descriptor.isEmpty()) {
                    continue;
                }
                // when slot is used, ignore it.
                var realSlotType = SkinSlotType.byType(descriptor.getType());
                if (usedSlots.contains(realSlotType)) {
                    continue;
                }
                consumer.accept(itemStack, i * 10, EntitySlot.Type.IN_WARDROBE);
            }
        }

        @Override
        protected void collect(SkinWardrobe wardrobe, List<ItemStack> collector) {
            var inventory = wardrobe.getInventory();
            var size = inventory.getContainerSize();
            for (var index = 0; index < size; ++index) {
                collector.add(inventory.getItem(index));
            }
        }
    }

    protected static class EntityProvider extends SlotProvider<Entity> {

        protected List<ItemStack> handSlots = new ArrayList<>();
        protected List<ItemStack> armourSlots = new ArrayList<>();

        @Override
        protected void load(Entity source, SlotConsumer consumer) {
            int i = 0;
            for (var itemStack : armourSlots) {
                consumer.accept(itemStack, 400 + i++, EntitySlot.Type.IN_EQUIPMENT);
            }
            for (var itemStack : handSlots) {
                consumer.accept(itemStack, 400 + i++, EntitySlot.Type.IN_HELD);
            }
        }

        @Override
        protected void collect(Entity entity, List<ItemStack> collector) {
            handSlots.clear();
            entity.getOverrideHandSlots().forEach(itemStack -> {
                handSlots.add(itemStack);
                collector.add(itemStack);
            });
            armourSlots.clear();
            entity.getOverrideArmorSlots().forEach(itemStack -> {
                armourSlots.add(itemStack);
                collector.add(itemStack);
            });
        }
    }

    protected static class BlockEntityProvider extends SlotProvider<BlockEntity> {

        @Override
        protected void load(BlockEntity source, SlotConsumer consumer) {
            int i = 0;
            for (var itemStack : lastSlots) {
                consumer.accept(itemStack, 400 + i++, EntitySlot.Type.IN_CONTAINER);
            }
        }

        @Override
        protected void collect(BlockEntity blockEntity, List<ItemStack> collector) {
            if (blockEntity instanceof SkinnableBlockEntity blockEntity1) {
                collector.add(blockEntity1.getDescriptor().sharedItemStack());
            }
            if (blockEntity instanceof HologramProjectorBlockEntity blockEntity1) {
                collector.add(blockEntity1.getItem(0));
            }
        }
    }
}
