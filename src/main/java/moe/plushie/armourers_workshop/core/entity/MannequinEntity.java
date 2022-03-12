package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.base.AWTags;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.ContainerOpener;
import moe.plushie.armourers_workshop.core.utils.TrigUtils;
import moe.plushie.armourers_workshop.core.wardrobe.Wardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.WardrobeContainer;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;


@SuppressWarnings("NullableProblems")
public class MannequinEntity extends ArmorStandEntity {

    public static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0f, 0.0f, 0.0f);
    public static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0f, 0.0f, 0.0f);
    public static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0f, 0.0f, -10.0f);
    public static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0f, 0.0f, 10.0f);
    public static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0f, 0.0f, -1.0f);
    public static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0f, 0.0f, 1.0f);

    public static final EntitySize MARKER_DIMENSIONS = new EntitySize(0.0f, 0.0f, true);
    public static final EntitySize BABY_DIMENSIONS = EntitySize.scalable(0.5f, 1.0f);
    public static final EntitySize STANDING_DIMENSIONS = EntitySize.scalable(0.6f, 1.88f);

    public static final DataParameter<Boolean> DATA_IS_CHILD = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> DATA_IS_FLYING = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> DATA_IS_GHOST = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> DATA_IS_VISIBLE = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Float> DATA_SCALE = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<Boolean> DATA_EXTRA_RENDERER = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<PlayerTextureDescriptor> DATA_TEXTURE = EntityDataManager.defineId(MannequinEntity.class, AWDataSerializers.PLAYER_TEXTURE);

    public MannequinEntity(EntityType<? extends MannequinEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHILD, false);
        this.entityData.define(DATA_IS_FLYING, false);
        this.entityData.define(DATA_IS_GHOST, false);
        this.entityData.define(DATA_IS_VISIBLE, true);
        this.entityData.define(DATA_EXTRA_RENDERER, true);
        this.entityData.define(DATA_SCALE, 1.0f);
        this.entityData.define(DATA_TEXTURE, PlayerTextureDescriptor.EMPTY);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);

        this.entityData.set(DATA_IS_CHILD, AWDataSerializers.getBoolean(nbt, AWConstants.NBT.ENTITY_IS_SMALL, false));
        this.entityData.set(DATA_IS_FLYING, AWDataSerializers.getBoolean(nbt, AWConstants.NBT.ENTITY_IS_FLYING, false));
        this.entityData.set(DATA_IS_GHOST, AWDataSerializers.getBoolean(nbt, AWConstants.NBT.ENTITY_IS_GHOST, false));
        this.entityData.set(DATA_IS_VISIBLE, AWDataSerializers.getBoolean(nbt, AWConstants.NBT.ENTITY_IS_VISIBLE, true));
        this.entityData.set(DATA_EXTRA_RENDERER, AWDataSerializers.getBoolean(nbt, AWConstants.NBT.ENTITY_EXTRA_RENDER, true));

        this.entityData.set(DATA_SCALE, AWDataSerializers.getFloat(nbt, AWConstants.NBT.ENTITY_SCALE, 1.0f));
        this.entityData.set(DATA_TEXTURE, AWDataSerializers.getTextureDescriptor(nbt, AWConstants.NBT.ENTITY_TEXTURE, PlayerTextureDescriptor.EMPTY));

        this.readCustomPose(nbt.getCompound(AWConstants.NBT.ENTITY_POSE));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);

        AWDataSerializers.putBoolean(nbt, AWConstants.NBT.ENTITY_IS_SMALL, entityData.get(DATA_IS_CHILD), false);
        AWDataSerializers.putBoolean(nbt, AWConstants.NBT.ENTITY_IS_FLYING, entityData.get(DATA_IS_FLYING), false);
        AWDataSerializers.putBoolean(nbt, AWConstants.NBT.ENTITY_IS_GHOST, entityData.get(DATA_IS_GHOST), false);
        AWDataSerializers.putBoolean(nbt, AWConstants.NBT.ENTITY_IS_VISIBLE, entityData.get(DATA_IS_VISIBLE), true);
        AWDataSerializers.putBoolean(nbt, AWConstants.NBT.ENTITY_EXTRA_RENDER, entityData.get(DATA_EXTRA_RENDERER), true);

        AWDataSerializers.putFloat(nbt, AWConstants.NBT.ENTITY_SCALE, getScale(), 1.0f);
        AWDataSerializers.putTextureDescriptor(nbt, AWConstants.NBT.ENTITY_TEXTURE, getTextureDescriptor(), PlayerTextureDescriptor.EMPTY);

        nbt.put(AWConstants.NBT.ENTITY_POSE, saveCustomPose());
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> dataParameter) {
        if (dataParameter == DATA_IS_CHILD) {
            refreshDimensions();
        }
        if (dataParameter == DATA_SCALE) {
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
    public EntitySize getDimensions(Pose pose) {
        if (isMarker()) {
            return MARKER_DIMENSIONS;
        }
        EntitySize entitySize = STANDING_DIMENSIONS;
        if (isBaby()) {
            entitySize = BABY_DIMENSIONS;
        }
        return entitySize.scale(getScale());
    }

    @Override
    public float getStandingEyeHeight(Pose pose, EntitySize entitySize) {
        float eyeHeight = 1.62f;
        if (isBaby()) {
            eyeHeight = 0.88f;
        }
        return eyeHeight * getScale();
    }

    @Override
    public void setYBodyRot(float rot) {
        this.yRotO = this.yRot = 0;
        this.yBodyRotO = this.yBodyRot = 0;
        this.yHeadRotO = this.yHeadRot = 0;
        Rotations rotations = getBodyPose();
        setBodyPose(new Rotations(rotations.getX(), rot, rotations.getZ()));
    }

    @Override
    public ActionResultType interactAt(PlayerEntity player, Vector3d pos, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.isMarker()) {
            return ActionResultType.PASS;
        }
        if (player.level.isClientSide) {
            return ActionResultType.CONSUME;
        }
        if (itemstack.getItem() == Items.NAME_TAG) {
            ITextComponent customName = null;
            if (itemstack.hasCustomHoverName() && !player.isShiftKeyDown()) {
                customName = itemstack.getHoverName();
            }
            setCustomName(customName);
            return ActionResultType.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            double angle = TrigUtils.getAngleDegrees(player.getX(), player.getZ(), getX(), getZ()) + 90.0;
            setYBodyRot((float) angle);
            return ActionResultType.SUCCESS;
        }
        Wardrobe wardrobe = Wardrobe.of(this);
        if (wardrobe != null) {
            ContainerOpener.open(WardrobeContainer.TYPE, player, wardrobe);
        }
        return ActionResultType.SUCCESS;
    }

    public PlayerTextureDescriptor getTextureDescriptor() {
        return this.entityData.get(DATA_TEXTURE);
    }

    public void setTextureDescriptor(PlayerTextureDescriptor descriptor) {
        this.entityData.set(DATA_TEXTURE, descriptor);
    }

    public void setExtraRenderer(boolean value) {
        this.entityData.set(DATA_EXTRA_RENDERER, value);
    }

    public boolean isExtraRenderer() {
        return this.entityData.get(DATA_EXTRA_RENDERER);
    }

    public IInventory getInventory() {
        return new Inventory(getMainHandItem(), getOffhandItem()) {
            @Override
            public void setItem(int index, ItemStack itemStack) {
                super.setItem(index, itemStack);
                setItemSlot(EquipmentSlotType.values()[index], itemStack);
            }

            @Override
            public boolean canPlaceItem(int index, ItemStack itemStack) {
                if (itemStack.isEmpty()) {
                    return true;
                }
                SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
                if (descriptor.getType() instanceof ISkinToolType) {
                    return true;
                }
                Item item = itemStack.getItem();
                return AWTags.isWeaponItem(item) || AWTags.isToolItem(item);
            }
        };
    }

    public CompoundNBT saveCustomPose() {
        CompoundNBT nbt = new CompoundNBT();
        AWDataSerializers.putRotations(nbt, "Head", entityData.get(DATA_HEAD_POSE), DEFAULT_HEAD_POSE);
        AWDataSerializers.putRotations(nbt, "Body", entityData.get(DATA_BODY_POSE), DEFAULT_BODY_POSE);
        AWDataSerializers.putRotations(nbt, "LeftArm", entityData.get(DATA_LEFT_ARM_POSE), DEFAULT_LEFT_ARM_POSE);
        AWDataSerializers.putRotations(nbt, "RightArm", entityData.get(DATA_RIGHT_ARM_POSE), DEFAULT_RIGHT_ARM_POSE);
        AWDataSerializers.putRotations(nbt, "LeftLeg", entityData.get(DATA_LEFT_LEG_POSE), DEFAULT_LEFT_LEG_POSE);
        AWDataSerializers.putRotations(nbt, "RightLeg", entityData.get(DATA_RIGHT_LEG_POSE), DEFAULT_RIGHT_LEG_POSE);
        return nbt;
    }

    public void readCustomPose(CompoundNBT nbt) {
        this.setHeadPose(AWDataSerializers.getRotations(nbt, "Head", DEFAULT_HEAD_POSE));
        this.setBodyPose(AWDataSerializers.getRotations(nbt, "Body", DEFAULT_BODY_POSE));
        this.setLeftArmPose(AWDataSerializers.getRotations(nbt, "LeftArm", DEFAULT_LEFT_ARM_POSE));
        this.setRightArmPose(AWDataSerializers.getRotations(nbt, "RightArm", DEFAULT_RIGHT_ARM_POSE));
        this.setLeftLegPose(AWDataSerializers.getRotations(nbt, "LeftLeg", DEFAULT_LEFT_LEG_POSE));
        this.setRightLegPose(AWDataSerializers.getRotations(nbt, "RightLeg", DEFAULT_RIGHT_LEG_POSE));
    }
}
