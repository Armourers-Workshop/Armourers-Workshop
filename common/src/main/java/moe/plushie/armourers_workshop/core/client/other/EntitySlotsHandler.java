package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.data.ticket.TicketHolder;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentManager;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EntitySlotsHandler<T> implements IAssociatedContainer, SkinBakery.IBakeListener {

    private final SlotProvider<T> entityProvider;
    private final WardrobeProvider wardrobeProvider;

    private final ArrayList<String> missingSkins = new ArrayList<>();

    private final HashSet<SkinType> lastSkinTypes = new HashSet<>();
    private final HashSet<SkinPartType> lastSkinPartTypes = new HashSet<>();

    private final ArrayList<EntitySlot> allSkins = new ArrayList<>();
    private final ArrayList<EntitySlot> armorSkins = new ArrayList<>();
    private final ArrayList<EntitySlot> itemSkins = new ArrayList<>();
    private final ArrayList<EntitySlot> containerSkins = new ArrayList<>();
    private final ArrayList<EntitySlot> heldSkins = new ArrayList<>();

    private final HashMap<SkinDescriptor, BakedSkin> activeSkins = new HashMap<>();
    private final HashMap<SkinDescriptor, BakedSkin> animatedSkins = new HashMap<>();

    private final AnimationManager animationManager;
    private final SkinOverriddenManager overriddenManager;

    private final TicketHolder tickets = new TicketHolder("EntitySlotsHandler");
    private final SkinLightSource lightSource = new SkinLightSource();

    private final DataContainer dataStorage = new DataContainer();
    private final SkinAttachmentManager attachmentManager = new SkinAttachmentManager();

    private int version = 0;
    private int lastVersion = Integer.MAX_VALUE;

    private boolean isLimitLimbs = false;
    private boolean isMannequinEntity = false;
    private boolean isOverrideAttachemntByDefault = false;
    private boolean isListening = false;

    protected EntitySlotsHandler(T entity, SlotProvider<T> entityProvider, WardrobeProvider wardrobeProvider) {
        this.entityProvider = entityProvider;
        this.wardrobeProvider = wardrobeProvider;
        // initialize the animation manager and overridden manager.
        this.animationManager = new AnimationManager(entity);
        this.overriddenManager = new SkinOverriddenManager();
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
        loadEntityInfo(source);

        wardrobeProvider.loadDye(wardrobe);
        wardrobeProvider.loadWardrobeFlags(wardrobe, overriddenManager);

        entityProvider.load(source, this::loadSkinFromItemStack);
        wardrobeProvider.load(wardrobe, this::loadSkinFromItemStack);
        entityProvider.load(source, this::loadSkinFromItemModel);

        loadSkinInfos();
        loadHandEquipments(source);
        loadArmourEquipments(source);
        loadSkinLightSources(source);
        loadSkinAnimations(source);
        loadMissingSkinIfNeeded();
    }

    private void invalidateAll() {
        lastVersion = Integer.MAX_VALUE;

        isLimitLimbs = false;
        isMannequinEntity = false;

        lastSkinTypes.clear();
        lastSkinPartTypes.clear();

        attachmentManager.clear();
        overriddenManager.clear();

        lightSource.clear();

        missingSkins.clear();
        armorSkins.clear();
        itemSkins.clear();
        containerSkins.clear();
        heldSkins.clear();
        allSkins.clear();
        activeSkins.clear();
        animatedSkins.clear();


        tickets.invalidate();
    }

    private void loadSkinFromItemStack(Object entity, ItemStack itemStack, float renderPriority, EntitySlot.Source source) {
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return; // not found skin in the item stack.
        }
        loadSkin(descriptor, itemStack, renderPriority, source);
    }

    private void loadSkinFromItemModel(Object entity, ItemStack itemStack, float renderPriority, EntitySlot.Source source) {
        // the item model has very low priority, when found a skin from the wardrobe or item stack will cause it discarded.
        var owner = Objects.safeCast(entity, LivingEntity.class);
        var itemModel = itemStack.getItemModel(getLevel(entity), owner, 0);
        var descriptor = DiscoveerableSkinManager.getInstance().get(itemModel);
        if (descriptor.isEmpty()) {
            return; // not found a skin in the item model.
        }
        loadSkin(descriptor, itemStack, renderPriority, source);
    }

    private void loadSkin(SkinDescriptor descriptor, ItemStack itemStack, float renderPriority, EntitySlot.Source source) {
        var bakedSkin = SkinBakery.getInstance().loadSkin(tickets.get(descriptor));
        if (bakedSkin == null) {
            missingSkins.add(descriptor.identifier());
            return;
        }
        var slot = new EntitySlot(bakedSkin, wardrobeProvider.colorScheme, itemStack, descriptor, renderPriority, source);
        switch (source) {
            case IN_HELD -> {
                // If held a skin of armor type, nothing happen
                if (bakedSkin.type().isTool() || bakedSkin.type() == SkinTypes.ITEM) {
                    allSkins.add(slot);
                    itemSkins.add(slot);
                }
            }
            case IN_EQUIPMENT, IN_WARDROBE -> {
                if (bakedSkin.type().isTool() || bakedSkin.type() == SkinTypes.ITEM) {
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
            var skin = entry.skin();
            var properties = skin.skin().properties();
            overriddenManager.merge(properties);
            if (!isLimitLimbs) {
                isLimitLimbs = properties.get(SkinProperty.LIMIT_LEGS_LIMBS);
            }
            // collect the skin and skin part type info.
            lastSkinTypes.add(skin.type());
            for (var skinPart : skin.parts()) {
                lastSkinPartTypes.add(skinPart.type());
            }
            activeSkins.put(entry.descriptor(), skin);
        }
    }

    private void loadSkinLightSources(T source) {
        // the armour/container skin will calculate the light source unconditionally.
        armorSkins.forEach(lightSource::add);
        containerSkins.forEach(lightSource::add);
        // will calculate the light source when item is active.
        heldSkins.forEach(lightSource::add);
    }

    private void loadHandEquipments(T source) {
        // matching the held items by the source into hand skins.
        if (entityProvider instanceof EntityProvider provider) {
            for (var itemStack : provider.handSlots) {
                heldSkins.addAll(getHeldSkins(itemStack));
            }
        }
    }

    private void loadArmourEquipments(T source) {
        // only support the entity.
        if (!(entityProvider instanceof EntityProvider entityProvider1)) {
            return;
        }
        for (var itemStack : entityProvider1.armourSlots) {
            for (var slot : getHeldSkins(itemStack)) {
                if (slot.type() == SkinTypes.ITEM_BACKPACK) {
                    armorSkins.add(slot);
                    overriddenManager.addProperty(SkinProperty.OVERRIDE_MODEL_BACKPACK);
                }
            }
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
        heldSkins.forEach(this::loadSkinAnimation);

        // submit data into animation manager.
        animationManager.load(activeSkins);
        animationManager.active(animatedSkins);
    }

    private void loadSkinAnimation(EntitySlot slot) {
        animatedSkins.put(slot.descriptor(), slot.skin());
    }

    private void loadEntityInfo(T source) {
        // we need use special item behavior on the mannequin entity.
        isMannequinEntity = source instanceof MannequinEntity;
    }

    @Override
    public void didBake(String identifier, BakedSkin bakedSkin) {
        if (missingSkins.contains(identifier)) {
            RenderSystem.safeCall(this::invalidateAll);
        }
    }

    public List<EntitySlot> getHeldSkins(ItemStack itemStack) {
        var target = getEmbeddedSkin(itemStack);
        if (target.isEmpty()) {
            // the item stack is not embedded skin, using matching pattern,
            // only need to find the first matching skin by item.
            for (var entry : itemSkins) {
                if (entry.source != EntitySlot.Source.IN_HELD && entry.shouldRenderInHeld(itemStack)) {
                    return Collections.singletonList(entry);
                }
            }
        } else {
            // the item stack is embedded skin, find the baked skin for matched descriptor.
            for (var entry : itemSkins) {
                if (entry.descriptor().equals(target)) {
                    return Collections.singletonList(entry);
                }
            }
        }
        return Collections.emptyList();
    }

    public List<EntitySlot> itemSkins() {
        return itemSkins;
    }

    public List<EntitySlot> armorSkins() {
        return armorSkins;
    }

    public List<EntitySlot> allSkins() {
        return allSkins;
    }

    public SkinPaintScheme colorScheme() {
        return wardrobeProvider.colorScheme;
    }

    public boolean isLimitLimbs() {
        return isLimitLimbs;
    }

    public SkinOverriddenManager overriddenManager() {
        return overriddenManager;
    }

    public AnimationManager animationManager() {
        return animationManager;
    }

    public SkinLightSource lightSource() {
        return lightSource;
    }

    public boolean shouldRenderExtra() {
        return wardrobeProvider.enableExtraRenderer;
    }

    public Collection<SkinType> usingTypes() {
        return lastSkinTypes;
    }

    public Collection<SkinPartType> usingPartTypes() {
        return lastSkinPartTypes;
    }

    public SkinAttachmentPose getAttachmentPose(SkinAttachmentType attachmentType, int index) {
        var attachmentPose = attachmentManager.get(attachmentType, index);
        if (attachmentPose != null) {
            return attachmentPose;
        }
        if (isOverrideAttachemntByDefault) {
            return SkinAttachmentPose.EMPTY;
        }
        return null;
    }

    public SkinAttachmentManager attachmentManager() {
        return attachmentManager;
    }

    @Override
    public <V> V getAssociatedObject(IAssociatedContainer.Key<V> key) {
        return dataStorage.getAssociatedObject(key);
    }

    @Override
    public <V> void setAssociatedObject(IAssociatedContainer.Key<V> key, V value) {
        dataStorage.setAssociatedObject(key, value);
    }

    private SkinDescriptor getEmbeddedSkin(ItemStack itemStack) {
        // for skin item, we don't consider it an embedded skin.
        if (!isMannequinEntity && itemStack.is(ModItems.SKIN.get())) {
            return SkinDescriptor.EMPTY;
        }
        var target = SkinDescriptor.of(itemStack);
        if (target.type() == SkinTypes.BOAT || target.type() == SkinTypes.ITEM_FISHING || target.type() == SkinTypes.HORSE) {
            return SkinDescriptor.EMPTY;
        }
        return target;
    }

    @Nullable
    private Level getLevel(Object entity) {
        if (entity instanceof Entity entity1) {
            return entity1.level();
        }
        if (entity instanceof BlockEntity entity1) {
            return entity1.getLevel();
        }
        return null;
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

        void accept(Object entity, ItemStack itemStack, float priority, EntitySlot.Source source);
    }

    protected static class WardrobeProvider extends SlotProvider<SkinWardrobe> {

        protected final HashMap<SkinPaintType, SkinPaintColor> dyeColors = new HashMap<>();
        protected final HashMap<SkinPaintType, SkinPaintColor> lastDyeColors = new HashMap<>();

        protected BitSet wardrobeFlags = new BitSet();
        protected SkinPaintScheme colorScheme = SkinPaintScheme.EMPTY;
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
            var flags = wardrobe.flags();
            if (!wardrobeFlags.equals(flags)) {
                wardrobeFlags.clear();
                wardrobeFlags.or(flags);
                result = true;
            }
            profile = wardrobe.profile();
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
                var paintColor = itemStack.get(ModDataComponents.TOOL_COLOR.get());
                if (paintColor != null) {
                    dyeColors.put(paintType, paintColor);
                }
            }
            if (!lastDyeColors.equals(dyeColors)) {
                colorScheme = new SkinPaintScheme();
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
            for (var slotType : OpenEquipmentSlot.values()) {
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
            for (var slotType : profile.slots()) {
                if (slotType == SkinSlotType.DYE) {
                    return;
                }
                for (int i = 0; i < slotType.maxSize(); ++i) {
                    var itemStack = lastSlots.get(slotType.index() + i);
                    var descriptor = SkinDescriptor.of(itemStack);
                    if (!descriptor.isEmpty()) {
                        usedSlots.add(slotType); // mark the slot is used.
                    }
                    consumer.accept(wardrobe, itemStack, i * 10, EntitySlot.Source.IN_WARDROBE);
                }
            }
            // load default skin when not user provided.
            var slotType = SkinSlotType.DEFAULT;
            for (int i = 0; i < slotType.maxSize(); ++i) {
                var itemStack = lastSlots.get(slotType.index() + i);
                var descriptor = SkinDescriptor.of(itemStack);
                if (descriptor.isEmpty()) {
                    continue;
                }
                // when slot is used, ignore it.
                var realSlotType = SkinSlotType.byType(descriptor.type());
                if (usedSlots.contains(realSlotType)) {
                    continue;
                }
                consumer.accept(wardrobe, itemStack, i * 10, EntitySlot.Source.IN_WARDROBE);
            }
        }

        @Override
        protected void collect(SkinWardrobe wardrobe, List<ItemStack> collector) {
            var inventory = wardrobe.inventory();
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
        protected void load(Entity entity, SlotConsumer consumer) {
            var i = 400;
            for (var itemStack : armourSlots) {
                consumer.accept(entity, itemStack, i++, EntitySlot.Source.IN_EQUIPMENT);
            }
            for (var itemStack : handSlots) {
                consumer.accept(entity, itemStack, i++, EntitySlot.Source.IN_HELD);
            }
        }

        @Override
        protected void collect(Entity entity, List<ItemStack> collector) {
            handSlots.clear();
            entity.getHandSlots(itemStack -> {
                handSlots.add(itemStack);
                collector.add(itemStack);
            });
            armourSlots.clear();
            entity.getArmorSlots(itemStack -> {
                armourSlots.add(itemStack);
                collector.add(itemStack);
            });
        }
    }

    protected static class BlockEntityProvider extends SlotProvider<BlockEntity> {

        @Override
        protected void load(BlockEntity entity, SlotConsumer consumer) {
            var i = 400;
            for (var itemStack : lastSlots) {
                consumer.accept(entity, itemStack, i++, EntitySlot.Source.IN_CONTAINER);
            }
        }

        @Override
        protected void collect(BlockEntity blockEntity, List<ItemStack> collector) {
            if (blockEntity instanceof SkinnableBlockEntity blockEntity1) {
                collector.add(blockEntity1.getSkin().sharedItemStack());
            }
            if (blockEntity instanceof HologramProjectorBlockEntity blockEntity1) {
                collector.add(blockEntity1.getItem(0));
            }
        }
    }
}
