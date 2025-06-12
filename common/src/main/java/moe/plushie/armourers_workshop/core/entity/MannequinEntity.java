package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.api.common.IEntityDataBuilder;
import moe.plushie.armourers_workshop.api.common.IEntityHandler;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.compatibility.core.AbstractLivingEntity;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.item.option.MannequinToolOptions;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModEntitySerializers;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutorIO;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
public class MannequinEntity extends AbstractLivingEntity.ArmorStand implements IEntityHandler, IDataSerializable.Mutable {

    public static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0f, 0.0f, 0.0f);
    public static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0f, 0.0f, 0.0f);
    public static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0f, 0.0f, -10.0f);
    public static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0f, 0.0f, 10.0f);
    public static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0f, 0.0f, -1.0f);
    public static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0f, 0.0f, 1.0f);

    public static final EntityDimensions MARKER_DIMENSIONS = EntityDimensions.fixed(0.0f, 0.0f);
    public static final EntityDimensions BABY_DIMENSIONS = EntityDimensions.scalable(0.5f, 1.0f).withEyeHeight(0.88f);
    public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.6f, 1.88f).withEyeHeight(1.62f);

    public static final EntityDataAccessor<Boolean> DATA_IS_CHILD = SynchedEntityData.defineId(MannequinEntity.class, ModEntitySerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_IS_FLYING = SynchedEntityData.defineId(MannequinEntity.class, ModEntitySerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_IS_GHOST = SynchedEntityData.defineId(MannequinEntity.class, ModEntitySerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_IS_VISIBLE = SynchedEntityData.defineId(MannequinEntity.class, ModEntitySerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(MannequinEntity.class, ModEntitySerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> DATA_EXTRA_RENDERER = SynchedEntityData.defineId(MannequinEntity.class, ModEntitySerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_NO_GRAVITY = SynchedEntityData.defineId(MannequinEntity.class, ModEntitySerializers.BOOLEAN);
    public static final EntityDataAccessor<EntityTextureDescriptor> DATA_TEXTURE = SynchedEntityData.defineId(MannequinEntity.class, ModEntitySerializers.PLAYER_TEXTURE);
    public static final EntityDataAccessor<EntityTextureDescriptor.Model> DATA_TEXTURE_MODEL = SynchedEntityData.defineId(MannequinEntity.class, ModEntitySerializers.PLAYER_TEXTURE_MODEL);

    private boolean isDropEquipment = false;
    private AABB boundingBoxForCulling;

    public MannequinEntity(EntityType<? extends MannequinEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(IEntityDataBuilder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_CHILD, false);
        builder.define(DATA_IS_FLYING, false);
        builder.define(DATA_IS_GHOST, false);
        builder.define(DATA_IS_VISIBLE, true);
        builder.define(DATA_EXTRA_RENDERER, true);
        builder.define(DATA_NO_GRAVITY, true); // default is no gravity
        builder.define(DATA_SCALE, 1.0f);
        builder.define(DATA_TEXTURE, EntityTextureDescriptor.EMPTY);
        builder.define(DATA_TEXTURE_MODEL, EntityTextureDescriptor.Model.STEVE);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.deserialize(new TagSerializer(tag));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.serialize(new TagSerializer(tag));
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        serializer.write(CodingKeys.IS_SMALL, entityData.get(DATA_IS_CHILD));
        serializer.write(CodingKeys.IS_FLYING, entityData.get(DATA_IS_FLYING));
        serializer.write(CodingKeys.IS_GHOST, entityData.get(DATA_IS_GHOST));
        serializer.write(CodingKeys.IS_VISIBLE, entityData.get(DATA_IS_VISIBLE));
        serializer.write(CodingKeys.EXTRA_RENDER, entityData.get(DATA_EXTRA_RENDERER));
        serializer.write(CodingKeys.NO_GRAVITY, entityData.get(DATA_NO_GRAVITY));

        serializer.write(CodingKeys.SCALE, entityData.get(DATA_SCALE));
        serializer.write(CodingKeys.TEXTURE, entityData.get(DATA_TEXTURE));
        serializer.write(CodingKeys.TEXTURE_MODEL, entityData.get(DATA_TEXTURE_MODEL));

        serializer.write(CodingKeys.POSE, saveCustomPose());
    }

    @Override
    public void deserialize(IDataSerializer serializer) {
        entityData.set(DATA_IS_CHILD, serializer.read(CodingKeys.IS_SMALL));
        entityData.set(DATA_IS_FLYING, serializer.read(CodingKeys.IS_FLYING));
        entityData.set(DATA_IS_GHOST, serializer.read(CodingKeys.IS_GHOST));
        entityData.set(DATA_IS_VISIBLE, serializer.read(CodingKeys.IS_VISIBLE));
        entityData.set(DATA_EXTRA_RENDERER, serializer.read(CodingKeys.EXTRA_RENDER));
        entityData.set(DATA_NO_GRAVITY, serializer.read(CodingKeys.NO_GRAVITY));

        entityData.set(DATA_SCALE, serializer.read(CodingKeys.SCALE));
        entityData.set(DATA_TEXTURE, serializer.read(CodingKeys.TEXTURE));
        entityData.set(DATA_TEXTURE_MODEL, serializer.read(CodingKeys.TEXTURE_MODEL));

        readCustomPose(serializer.read(CodingKeys.POSE));

        refreshDimensions();
        refreshPhysics();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataParameter) {
        if (DATA_IS_CHILD.equals(dataParameter)) {
            refreshDimensions();
        }
        if (DATA_SCALE.equals(dataParameter)) {
            refreshDimensions();
        }
        if (DATA_NO_GRAVITY.equals(dataParameter)) {
            refreshPhysics();
        }
        super.onSyncedDataUpdated(dataParameter);
    }

    public boolean isModelVisible() {
        return entityData.get(DATA_IS_VISIBLE);
    }

    public void setModelVisible(boolean value) {
        entityData.set(DATA_IS_VISIBLE, value);
    }

    @Override
    public float getScale() {
        return entityData.get(DATA_SCALE);
    }

    @Override
    public boolean isSmall() {
        return entityData.get(DATA_IS_CHILD);
    }

    @Override
    public boolean isNoGravity() {
        return entityData.get(DATA_NO_GRAVITY);
    }

    @Override
    public void setNoGravity(boolean bl) {
        entityData.set(DATA_NO_GRAVITY, bl);
    }

    public boolean isFakeFlying() {
        return entityData.get(DATA_IS_FLYING);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive() && !entityData.get(DATA_IS_GHOST);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        if (isMarker()) {
            return MARKER_DIMENSIONS;
        }
        var entitySize = STANDING_DIMENSIONS;
        if (isBaby()) {
            entitySize = BABY_DIMENSIONS;
        }
        return entitySize;
    }

    @Override
    public ItemStack getCustomPickResult(HitResult target) {
        var itemStack = new ItemStack(ModItems.MANNEQUIN.get());
        // yep, we need copy the fully model info when ctrl down.
        if (EnvironmentExecutorIO.hasControlDown()) {
            var entityTag = new CompoundTag();
            entityTag.putString(Constants.Key.ID, ModEntityTypes.MANNEQUIN.registryName().toString());
            addAdditionalSaveData(entityTag);
            itemStack.set(ModDataComponents.ENTITY_DATA.get(), entityTag);
        }
        return itemStack;
    }

    @Override
    public void setYBodyRot(float f) {
        super.setYBodyRot(f);
        this.setYRot(f);
        this.yBodyRot = f;
    }

    @Override
    public void setPos(double d, double e, double f) {
        super.setPos(d, e, f);
        this.boundingBoxForCulling = null;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        isDropEquipment = false;
        boolean flag = this.isAlive();
        boolean flag1 = super.hurt(source, amount);
        var level = getLevel();
        if (!isDropEquipment && flag != this.isAlive() && level instanceof ServerLevel) {
            this.brokenByAnything((ServerLevel) level, source);
        }
        return flag1;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 pos, InteractionHand hand) {
        if (isMarker()) {
            return InteractionResult.PASS;
        }
        var itemStack = player.getItemInHand(hand);
        if (itemStack.is(ModItems.MANNEQUIN_TOOL.get())) {
            return InteractionResult.PASS;
        }
        if (itemStack.is(Items.NAME_TAG)) {
            // forward to vanilla `NameTagItem` implementations.
            return itemStack.interactLivingEntity(player, this, hand);
        }
        if (player.isSecondaryUseActive()) {
            // forward to vanilla armour stand interact implementations.
            if (EnvironmentExecutorIO.hasControlDown()) {
                return super.interactAt(player, pos, hand);
            }
            var ry = OpenMath.getAngleDegrees(player.getX(), player.getZ(), getX(), getZ()) + 90.0;
            var rotations = getBodyPose();
            var yRot = this.getYRot();
            setBodyPose(new Rotations(rotations.getX(), (float) ry - yRot, rotations.getZ()));
            return InteractionResult.sidedSuccess(getLevel().isClientSide());
        }
        var wardrobe = SkinWardrobe.of(this);
        if (wardrobe != null && wardrobe.isEditable(player)) {
            ModMenuTypes.WARDROBE.get().openMenu(player, wardrobe);
            return InteractionResult.sidedSuccess(getLevel().isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void brokenByPlayer(ServerLevel serverLevel, DamageSource source) {
        // drop a mannequin item stack?
        if (source.getEntity() instanceof Player player && !player.getAbilities().instabuild) {
            var entityData = new EntityData();
            entityData.setScale(getScale());
            entityData.setTexture(getTextureDescriptor());
            Block.popResource(getLevel(), blockPosition(), entityData.itemStack());
        }
        this.brokenByAnything(serverLevel, source);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        this.isDropEquipment = true;
        // drop all wardrobe items.
        var wardrobe = SkinWardrobe.of(this);
        if (wardrobe != null) {
            wardrobe.dropAll(this::spawnAtLocation);
        }
    }

    protected void refreshPhysics() {
        this.noPhysics = !hasPhysics();
    }

    protected boolean hasPhysics() {
        return !isMarker() && !isNoGravity();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AABB getBoundingBoxForCulling() {
        if (boundingBoxForCulling != null) {
            return boundingBoxForCulling;
        }
        float f = getScale();
        boundingBoxForCulling = this.getBoundingBox().inflate(f * 3f, f * 2f, f * 2.5f);
        return boundingBoxForCulling;
    }

    public EntityTextureDescriptor getTextureDescriptor() {
        return entityData.get(DATA_TEXTURE);
    }

    public void setTextureDescriptor(EntityTextureDescriptor newValue) {
        entityData.set(DATA_TEXTURE, newValue);
    }

    public EntityTextureDescriptor.Model getTextureModel() {
        return entityData.get(DATA_TEXTURE_MODEL);
    }

    public void setTextureModel(EntityTextureDescriptor.Model newValue) {
        entityData.set(DATA_TEXTURE_MODEL, newValue);
    }

    public boolean isExtraRenderer() {
        return this.entityData.get(DATA_EXTRA_RENDERER);
    }

    public void setExtraRenderer(boolean value) {
        this.entityData.set(DATA_EXTRA_RENDERER, value);
    }

    public Container getInventory() {
        return new SimpleContainer(getMainHandItem(), getOffhandItem()) {
            @Override
            public void setItem(int index, ItemStack itemStack) {
                super.setItem(index, itemStack);
                setItemSlot(EquipmentSlot.values()[index], itemStack);
            }
        };
    }

    public CompoundTag saveCustomPose() {
        var serializer = new TagSerializer();
        serializer.write(CodingKeys.POSE_HEAD, entityData.get(DATA_HEAD_POSE));
        serializer.write(CodingKeys.POSE_BODY, entityData.get(DATA_BODY_POSE));
        serializer.write(CodingKeys.POSE_LEFT_ARM, entityData.get(DATA_LEFT_ARM_POSE));
        serializer.write(CodingKeys.POSE_RIGHT_ARM, entityData.get(DATA_RIGHT_ARM_POSE));
        serializer.write(CodingKeys.POSE_LEFT_LEG, entityData.get(DATA_LEFT_LEG_POSE));
        serializer.write(CodingKeys.POSE_RIGHT_LEG, entityData.get(DATA_RIGHT_LEG_POSE));
        return serializer.tag();
    }

    public void readCustomPose(CompoundTag tag) {
        var serializer = new TagSerializer(tag);
        setHeadPose(serializer.read(CodingKeys.POSE_HEAD));
        setBodyPose(serializer.read(CodingKeys.POSE_BODY));
        setLeftArmPose(serializer.read(CodingKeys.POSE_LEFT_ARM));
        setRightArmPose(serializer.read(CodingKeys.POSE_RIGHT_ARM));
        setLeftLegPose(serializer.read(CodingKeys.POSE_LEFT_LEG));
        setRightLegPose(serializer.read(CodingKeys.POSE_RIGHT_LEG));
    }

    public void saveMannequinToolData(CompoundTag entityTag) {
        serialize(new TagSerializer(entityTag));
    }

    public void readMannequinToolData(CompoundTag entityTag, ItemStack itemStack) {
        CompoundTag newEntityTag = new CompoundTag();
        if (itemStack.get(MannequinToolOptions.CHANGE_OPTION)) {
            newEntityTag.merge(entityTag);
            newEntityTag.remove(CodingKeys.SCALE.name());
            newEntityTag.remove(CodingKeys.POSE.name());
            newEntityTag.remove(CodingKeys.TEXTURE.name());
        }
        if (itemStack.get(MannequinToolOptions.CHANGE_SCALE)) {
            var oldValue = entityTag.get(CodingKeys.SCALE.name());
            if (oldValue != null) {
                newEntityTag.put(CodingKeys.SCALE.name(), oldValue);
            }
        }
        if (itemStack.get(MannequinToolOptions.CHANGE_ROTATION)) {
            var oldValue = entityTag.getOptionalCompound(CodingKeys.POSE.name()).orElseGet(CompoundTag::new);
            if (itemStack.get(MannequinToolOptions.MIRROR_MODE) && !oldValue.isEmpty()) {
                var poseSerializer = new TagSerializer(oldValue.copy());
                poseSerializer.write(CodingKeys.POSE_HEAD, EntityData.mirror(poseSerializer.read(CodingKeys.POSE_HEAD)));
                poseSerializer.write(CodingKeys.POSE_BODY, EntityData.mirror(poseSerializer.read(CodingKeys.POSE_BODY)));
                poseSerializer.write(CodingKeys.POSE_LEFT_ARM, EntityData.mirror(poseSerializer.read(CodingKeys.POSE_LEFT_ARM)));
                poseSerializer.write(CodingKeys.POSE_RIGHT_ARM, EntityData.mirror(poseSerializer.read(CodingKeys.POSE_RIGHT_ARM)));
                poseSerializer.write(CodingKeys.POSE_LEFT_LEG, EntityData.mirror(poseSerializer.read(CodingKeys.POSE_LEFT_LEG)));
                poseSerializer.write(CodingKeys.POSE_RIGHT_LEG, EntityData.mirror(poseSerializer.read(CodingKeys.POSE_RIGHT_LEG)));
                oldValue = poseSerializer.tag();
            }
            newEntityTag.put(CodingKeys.POSE.name(), oldValue);
        }
        if (itemStack.get(MannequinToolOptions.CHANGE_TEXTURE)) {
            var oldValue = entityTag.get(CodingKeys.TEXTURE.name());
            if (oldValue != null) {
                newEntityTag.put(CodingKeys.TEXTURE.name(), oldValue);
            }
        }
        // load into entity
        deserialize(new TagSerializer(newEntityTag));
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<Boolean> IS_SMALL = IDataSerializerKey.create("Small", IDataCodec.BOOL, false);
        public static final IDataSerializerKey<Boolean> IS_FLYING = IDataSerializerKey.create("Flying", IDataCodec.BOOL, false);
        public static final IDataSerializerKey<Boolean> IS_GHOST = IDataSerializerKey.create("Ghost", IDataCodec.BOOL, false);
        public static final IDataSerializerKey<Boolean> IS_VISIBLE = IDataSerializerKey.create("ModelVisible", IDataCodec.BOOL, true);
        public static final IDataSerializerKey<Boolean> EXTRA_RENDER = IDataSerializerKey.create("ExtraRender", IDataCodec.BOOL, true);
        public static final IDataSerializerKey<Boolean> NO_GRAVITY = IDataSerializerKey.create("NoGravity", IDataCodec.BOOL, true);
        public static final IDataSerializerKey<Float> SCALE = IDataSerializerKey.create("Scale", IDataCodec.FLOAT, 1.0f);
        public static final IDataSerializerKey<EntityTextureDescriptor> TEXTURE = IDataSerializerKey.create("Texture", EntityTextureDescriptor.CODEC, EntityTextureDescriptor.EMPTY);
        public static final IDataSerializerKey<EntityTextureDescriptor.Model> TEXTURE_MODEL = IDataSerializerKey.create("TextureModel", DataSerializers.ENTITY_TEXTURE_MODEL, EntityTextureDescriptor.Model.STEVE);
        public static final IDataSerializerKey<CompoundTag> POSE = IDataSerializerKey.create("Pose", IDataCodec.COMPOUND_TAG, new CompoundTag());

        public static final IDataSerializerKey<Rotations> POSE_HEAD = IDataSerializerKey.create("Head", EntityData.ROTATIONS_CODEC, DEFAULT_HEAD_POSE);
        public static final IDataSerializerKey<Rotations> POSE_BODY = IDataSerializerKey.create("Body", EntityData.ROTATIONS_CODEC, DEFAULT_BODY_POSE);
        public static final IDataSerializerKey<Rotations> POSE_LEFT_ARM = IDataSerializerKey.create("LeftArm", EntityData.ROTATIONS_CODEC, DEFAULT_LEFT_ARM_POSE);
        public static final IDataSerializerKey<Rotations> POSE_RIGHT_ARM = IDataSerializerKey.create("RightArm", EntityData.ROTATIONS_CODEC, DEFAULT_RIGHT_ARM_POSE);
        public static final IDataSerializerKey<Rotations> POSE_LEFT_LEG = IDataSerializerKey.create("LeftLeg", EntityData.ROTATIONS_CODEC, DEFAULT_LEFT_LEG_POSE);
        public static final IDataSerializerKey<Rotations> POSE_RIGHT_LEG = IDataSerializerKey.create("RightLeg", EntityData.ROTATIONS_CODEC, DEFAULT_RIGHT_LEG_POSE);
    }

    public static class EntityData {

        private static final IDataCodec<Rotations> ROTATIONS_CODEC = IDataCodec.FLOAT.listOf().xmap(it -> new Rotations(it.get(0), it.get(1), it.get(2)), it -> Collections.newList(it.getX(), it.getY(), it.getZ()));

        private final TagSerializer serializer;

        public EntityData() {
            this.serializer = new TagSerializer();
        }

        public EntityData(CompoundTag tag) {
            this.serializer = new TagSerializer(tag);
        }

        private static Rotations mirror(Rotations rot) {
            return new Rotations(rot.getX(), -rot.getY(), -rot.getZ());
        }

        public void setScale(float scale) {
            serializer.write(CodingKeys.SCALE, scale);
        }

        public float scale() {
            return serializer.read(CodingKeys.SCALE);
        }

        public void setTexture(EntityTextureDescriptor texture) {
            serializer.write(CodingKeys.TEXTURE, texture);
        }

        public EntityTextureDescriptor texture() {
            return serializer.read(CodingKeys.TEXTURE);
        }

        public boolean isSmall() {
            return serializer.read(CodingKeys.IS_SMALL);
        }

        public CompoundTag entityTag() {
            var entityTag = serializer.tag().copy();
            if (!entityTag.isEmpty()) {
                entityTag.putString(Constants.Key.ID, ModEntityTypes.MANNEQUIN.registryName().toString());
                //itemStack.set(ModDataComponents.ENTITY_DATA.get(), entityTag);
            }
            return entityTag;
        }

        public ItemStack itemStack() {
            var itemStack = new ItemStack(ModItems.MANNEQUIN.get());
            var entityTag = entityTag();
            if (!entityTag.isEmpty()) {
                itemStack.set(ModDataComponents.ENTITY_DATA.get(), entityTag);
            }
            return itemStack;
        }
    }
}
