package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.api.common.IEntityDataBuilder;
import moe.plushie.armourers_workshop.api.common.IEntityHandler;
import moe.plushie.armourers_workshop.compatibility.core.AbstractLivingEntity;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.item.option.MannequinToolOptions;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModEntitySerializers;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutorIO;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.TrigUtils;
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
public class MannequinEntity extends AbstractLivingEntity.ArmorStand implements IEntityHandler {

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
    public static final EntityDataAccessor<PlayerTextureDescriptor> DATA_TEXTURE = SynchedEntityData.defineId(MannequinEntity.class, ModEntitySerializers.PLAYER_TEXTURE);

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
        builder.define(DATA_SCALE, 1.0f);
        builder.define(DATA_TEXTURE, PlayerTextureDescriptor.EMPTY);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readExtendedData(tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addExtendedData(tag);
    }

    public void readExtendedData(CompoundTag tag) {
        entityData.set(DATA_IS_CHILD, tag.getOptionalBoolean(Constants.Key.ENTITY_IS_SMALL, false));
        entityData.set(DATA_IS_FLYING, tag.getOptionalBoolean(Constants.Key.ENTITY_IS_FLYING, false));
        entityData.set(DATA_IS_GHOST, tag.getOptionalBoolean(Constants.Key.ENTITY_IS_GHOST, false));
        entityData.set(DATA_IS_VISIBLE, tag.getOptionalBoolean(Constants.Key.ENTITY_IS_VISIBLE, true));
        entityData.set(DATA_EXTRA_RENDERER, tag.getOptionalBoolean(Constants.Key.ENTITY_EXTRA_RENDER, true));

        entityData.set(DATA_SCALE, tag.getOptionalFloat(Constants.Key.ENTITY_SCALE, 1.0f));
        entityData.set(DATA_TEXTURE, tag.getOptionalTextureDescriptor(Constants.Key.ENTITY_TEXTURE, PlayerTextureDescriptor.EMPTY));

        readCustomPose(tag.getCompound(Constants.Key.ENTITY_POSE));
    }

    public void addExtendedData(CompoundTag tag) {
        tag.putOptionalBoolean(Constants.Key.ENTITY_IS_SMALL, entityData.get(DATA_IS_CHILD), false);
        tag.putOptionalBoolean(Constants.Key.ENTITY_IS_FLYING, entityData.get(DATA_IS_FLYING), false);
        tag.putOptionalBoolean(Constants.Key.ENTITY_IS_GHOST, entityData.get(DATA_IS_GHOST), false);
        tag.putOptionalBoolean(Constants.Key.ENTITY_IS_VISIBLE, entityData.get(DATA_IS_VISIBLE), true);
        tag.putOptionalBoolean(Constants.Key.ENTITY_EXTRA_RENDER, entityData.get(DATA_EXTRA_RENDERER), true);

        tag.putOptionalFloat(Constants.Key.ENTITY_SCALE, getScale(), 1.0f);
        tag.putOptionalTextureDescriptor(Constants.Key.ENTITY_TEXTURE, getTextureDescriptor(), PlayerTextureDescriptor.EMPTY);

        tag.put(Constants.Key.ENTITY_POSE, saveCustomPose());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataParameter) {
        if (DATA_IS_CHILD.equals(dataParameter)) {
            refreshDimensions();
        }
        if (DATA_SCALE.equals(dataParameter)) {
            refreshDimensions();
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
        return true; // never gravity
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
        if (EnvironmentExecutorIO.hasSprintDown()) {
            var entityTag = new CompoundTag();
            entityTag.putString(Constants.Key.ID, ModEntityTypes.MANNEQUIN.getRegistryName().toString());
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
            double ry = TrigUtils.getAngleDegrees(player.getX(), player.getZ(), getX(), getZ()) + 90.0;
            Rotations rotations = getBodyPose();
            float yRot = this.getYRot();
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
        Player player = null;
        if (source.getEntity() instanceof Player) {
            player = (Player) source.getEntity();
        }
        if (player != null && !player.getAbilities().instabuild) {
            Block.popResource(this.getLevel(), this.blockPosition(), createMannequinStack());
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

    protected ItemStack createMannequinStack() {
        var itemStack = new ItemStack(ModItems.MANNEQUIN.get());
        var entityTag = new CompoundTag();
        entityTag.putString(Constants.Key.ID, ModEntityTypes.MANNEQUIN.getRegistryName().toString());
        entityTag.putOptionalFloat(Constants.Key.ENTITY_SCALE, getScale(), 1.0f);
        entityTag.putOptionalTextureDescriptor(Constants.Key.ENTITY_TEXTURE, getTextureDescriptor(), PlayerTextureDescriptor.EMPTY);
        itemStack.set(ModDataComponents.ENTITY_DATA.get(), entityTag);
        return itemStack;
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

    public PlayerTextureDescriptor getTextureDescriptor() {
        return this.entityData.get(DATA_TEXTURE);
    }

    public void setTextureDescriptor(PlayerTextureDescriptor descriptor) {
        this.entityData.set(DATA_TEXTURE, descriptor);
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
        CompoundTag tag = new CompoundTag();
        tag.putOptionalRotations(Constants.Key.ENTITY_POSE_HEAD, entityData.get(DATA_HEAD_POSE), DEFAULT_HEAD_POSE);
        tag.putOptionalRotations(Constants.Key.ENTITY_POSE_BODY, entityData.get(DATA_BODY_POSE), DEFAULT_BODY_POSE);
        tag.putOptionalRotations(Constants.Key.ENTITY_POSE_LEFT_ARM, entityData.get(DATA_LEFT_ARM_POSE), DEFAULT_LEFT_ARM_POSE);
        tag.putOptionalRotations(Constants.Key.ENTITY_POSE_RIGHT_ARM, entityData.get(DATA_RIGHT_ARM_POSE), DEFAULT_RIGHT_ARM_POSE);
        tag.putOptionalRotations(Constants.Key.ENTITY_POSE_LEFT_LEG, entityData.get(DATA_LEFT_LEG_POSE), DEFAULT_LEFT_LEG_POSE);
        tag.putOptionalRotations(Constants.Key.ENTITY_POSE_RIGHT_LEG, entityData.get(DATA_RIGHT_LEG_POSE), DEFAULT_RIGHT_LEG_POSE);
        return tag;
    }

    public void readCustomPose(CompoundTag tag) {
        this.setHeadPose(tag.getOptionalRotations(Constants.Key.ENTITY_POSE_HEAD, DEFAULT_HEAD_POSE));
        this.setBodyPose(tag.getOptionalRotations(Constants.Key.ENTITY_POSE_BODY, DEFAULT_BODY_POSE));
        this.setLeftArmPose(tag.getOptionalRotations(Constants.Key.ENTITY_POSE_LEFT_ARM, DEFAULT_LEFT_ARM_POSE));
        this.setRightArmPose(tag.getOptionalRotations(Constants.Key.ENTITY_POSE_RIGHT_ARM, DEFAULT_RIGHT_ARM_POSE));
        this.setLeftLegPose(tag.getOptionalRotations(Constants.Key.ENTITY_POSE_LEFT_LEG, DEFAULT_LEFT_LEG_POSE));
        this.setRightLegPose(tag.getOptionalRotations(Constants.Key.ENTITY_POSE_RIGHT_LEG, DEFAULT_RIGHT_LEG_POSE));
    }

    public void saveMannequinToolData(CompoundTag entityTag) {
        addExtendedData(entityTag);
    }

    public void readMannequinToolData(CompoundTag entityTag, ItemStack itemStack) {
        CompoundTag newEntityTag = new CompoundTag();
        if (itemStack.get(MannequinToolOptions.CHANGE_OPTION)) {
            newEntityTag.merge(entityTag);
            newEntityTag.remove(Constants.Key.ENTITY_SCALE);
            newEntityTag.remove(Constants.Key.ENTITY_POSE);
            newEntityTag.remove(Constants.Key.ENTITY_TEXTURE);
        }
        if (itemStack.get(MannequinToolOptions.CHANGE_SCALE)) {
            var oldValue = entityTag.get(Constants.Key.ENTITY_SCALE);
            if (oldValue != null) {
                newEntityTag.put(Constants.Key.ENTITY_SCALE, oldValue);
            }
        }
        if (itemStack.get(MannequinToolOptions.CHANGE_ROTATION)) {
            var oldValue = entityTag.getCompound(Constants.Key.ENTITY_POSE);
            if (itemStack.get(MannequinToolOptions.MIRROR_MODE) && !oldValue.isEmpty()) {
                CompoundTag newPoseTag = oldValue.copy();
                DataSerializers.mirrorRotations(oldValue, Constants.Key.ENTITY_POSE_HEAD, DEFAULT_HEAD_POSE, newPoseTag, Constants.Key.ENTITY_POSE_HEAD, DEFAULT_HEAD_POSE);
                DataSerializers.mirrorRotations(oldValue, Constants.Key.ENTITY_POSE_BODY, DEFAULT_BODY_POSE, newPoseTag, Constants.Key.ENTITY_POSE_BODY, DEFAULT_BODY_POSE);
                DataSerializers.mirrorRotations(oldValue, Constants.Key.ENTITY_POSE_RIGHT_ARM, DEFAULT_RIGHT_ARM_POSE, newPoseTag, Constants.Key.ENTITY_POSE_LEFT_ARM, DEFAULT_LEFT_ARM_POSE);
                DataSerializers.mirrorRotations(oldValue, Constants.Key.ENTITY_POSE_LEFT_ARM, DEFAULT_LEFT_ARM_POSE, newPoseTag, Constants.Key.ENTITY_POSE_RIGHT_ARM, DEFAULT_RIGHT_ARM_POSE);
                DataSerializers.mirrorRotations(oldValue, Constants.Key.ENTITY_POSE_RIGHT_LEG, DEFAULT_RIGHT_LEG_POSE, newPoseTag, Constants.Key.ENTITY_POSE_LEFT_LEG, DEFAULT_LEFT_LEG_POSE);
                DataSerializers.mirrorRotations(oldValue, Constants.Key.ENTITY_POSE_LEFT_LEG, DEFAULT_LEFT_LEG_POSE, newPoseTag, Constants.Key.ENTITY_POSE_RIGHT_LEG, DEFAULT_RIGHT_LEG_POSE);
                oldValue = newPoseTag;
            }
            newEntityTag.put(Constants.Key.ENTITY_POSE, oldValue);
        }
        if (itemStack.get(MannequinToolOptions.CHANGE_TEXTURE)) {
            var oldValue = entityTag.get(Constants.Key.ENTITY_TEXTURE);
            if (oldValue != null) {
                newEntityTag.put(Constants.Key.ENTITY_TEXTURE, oldValue);
            }
        }
        // load into entity
        readExtendedData(newEntityTag);
    }
}
