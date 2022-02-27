package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.base.AWTags;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.ContainerOpener;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;


@SuppressWarnings("NullableProblems")
public class MannequinEntity extends ArmorStandEntity {

    public static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0f, 0.0f, 0.0f);
    public static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0f, 0.0f, 0.0f);
    public static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0f, 0.0f, -10.0f);
    public static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0f, 0.0f, 10.0f);
    public static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0f, 0.0f, -1.0f);
    public static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0f, 0.0f, 1.0f);

    public static final DataParameter<Boolean> DATA_IS_CHILD = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> DATA_IS_FLYING = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> DATA_IS_VISIBLE = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> DATA_IS_GHOST = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> DATA_EXTRA_RENDERER = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<PlayerTextureDescriptor> DATA_TEXTURE = EntityDataManager.defineId(MannequinEntity.class, PlayerTextureDescriptor.SERIALIZER);

    public MannequinEntity(EntityType<? extends MannequinEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHILD, false);
        this.entityData.define(DATA_IS_FLYING, false);
        this.entityData.define(DATA_IS_VISIBLE, true);
        this.entityData.define(DATA_IS_GHOST, false);
        this.entityData.define(DATA_EXTRA_RENDERER, true);
        this.entityData.define(DATA_TEXTURE, PlayerTextureDescriptor.EMPTY);
    }


    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(DATA_IS_CHILD, nbt.getBoolean(AWConstants.NBT.MANNEQUIN_IS_CHILD));
        this.entityData.set(DATA_IS_FLYING, nbt.getBoolean(AWConstants.NBT.MANNEQUIN_IS_FLYING));
        this.entityData.set(DATA_IS_GHOST, nbt.getBoolean(AWConstants.NBT.MANNEQUIN_IS_GHOST));
        this.entityData.set(DATA_IS_VISIBLE, readBoolean(nbt, AWConstants.NBT.MANNEQUIN_IS_VISIBLE, true));
        this.entityData.set(DATA_EXTRA_RENDERER, readBoolean(nbt, AWConstants.NBT.MANNEQUIN_EXTRA_RENDER, true));

        CompoundNBT nbt1 = nbt.getCompound(AWConstants.NBT.MANNEQUIN_TEXTURE);
        if (!nbt1.isEmpty()) {
            setTextureDescriptor(new PlayerTextureDescriptor(nbt1));
        }

        CompoundNBT poseNBT = nbt.getCompound(AWConstants.NBT.MANNEQUIN_POSE);
        this.readCustomPose(poseNBT);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean(AWConstants.NBT.MANNEQUIN_IS_CHILD, entityData.get(DATA_IS_CHILD));
        nbt.putBoolean(AWConstants.NBT.MANNEQUIN_IS_FLYING, entityData.get(DATA_IS_FLYING));
        nbt.putBoolean(AWConstants.NBT.MANNEQUIN_IS_GHOST, entityData.get(DATA_IS_GHOST));
        nbt.putBoolean(AWConstants.NBT.MANNEQUIN_IS_VISIBLE, entityData.get(DATA_IS_VISIBLE));
        nbt.putBoolean(AWConstants.NBT.MANNEQUIN_EXTRA_RENDER, entityData.get(DATA_EXTRA_RENDERER));

        PlayerTextureDescriptor descriptor = getTextureDescriptor();
        if (!descriptor.isEmpty()) {
            nbt.put(AWConstants.NBT.MANNEQUIN_TEXTURE, descriptor.serializeNBT());
        }

        nbt.put(AWConstants.NBT.MANNEQUIN_POSE, saveCustomPose());
    }

    @Override
    public boolean isBaby() {
        return super.isBaby() || entityData.get(DATA_IS_CHILD);
    }

    @Override
    public boolean isInvisible() {
        return !entityData.get(DATA_IS_VISIBLE);
    }

    public boolean isFakeFlying() {
        return entityData.get(DATA_IS_FLYING);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive() && !entityData.get(DATA_IS_GHOST);
    }

    @Override
    public ActionResultType interactAt(PlayerEntity player, Vector3d pos, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.isMarker() || itemstack.getItem() == Items.NAME_TAG) {
            return ActionResultType.PASS;
        }
        if (player.level.isClientSide) {
            return ActionResultType.CONSUME;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(this);
        if (wardrobe != null) {
            ContainerOpener.openContainer(SkinWardrobeContainer.TYPE, player, wardrobe);
        }
        return ActionResultType.SUCCESS;
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
        writeRotations(nbt, "HEAD", DEFAULT_HEAD_POSE, entityData.get(DATA_HEAD_POSE));
        writeRotations(nbt, "Body", DEFAULT_BODY_POSE, entityData.get(DATA_BODY_POSE));
        writeRotations(nbt, "LeftArm", DEFAULT_LEFT_ARM_POSE, entityData.get(DATA_LEFT_ARM_POSE));
        writeRotations(nbt, "RightArm", DEFAULT_RIGHT_ARM_POSE, entityData.get(DATA_RIGHT_ARM_POSE));
        writeRotations(nbt, "LeftLeg", DEFAULT_LEFT_LEG_POSE, entityData.get(DATA_LEFT_LEG_POSE));
        writeRotations(nbt, "RightLeg", DEFAULT_RIGHT_LEG_POSE, entityData.get(DATA_RIGHT_LEG_POSE));
        return nbt;
    }

    public void readCustomPose(CompoundNBT nbt) {
        this.setHeadPose(readRotations(nbt, "HEAD", DEFAULT_HEAD_POSE));
        this.setBodyPose(readRotations(nbt, "Body", DEFAULT_BODY_POSE));
        this.setLeftArmPose(readRotations(nbt, "LeftArm", DEFAULT_LEFT_ARM_POSE));
        this.setRightArmPose(readRotations(nbt, "RightArm", DEFAULT_RIGHT_ARM_POSE));
        this.setLeftLegPose(readRotations(nbt, "LeftLeg", DEFAULT_LEFT_LEG_POSE));
        this.setRightLegPose(readRotations(nbt, "RightLeg", DEFAULT_RIGHT_LEG_POSE));
    }

    private void writeRotations(CompoundNBT nbt, String key, Rotations defaultValue, Rotations currentValue) {
        if (!defaultValue.equals(currentValue)) {
            nbt.put(key, currentValue.save());
        }
    }

    private Rotations readRotations(CompoundNBT nbt, String key, Rotations defaultValue) {
        ListNBT listNBT = nbt.getList(key, Constants.NBT.TAG_FLOAT);
        if (listNBT.isEmpty()) {
            return defaultValue;
        }
        return new Rotations(listNBT);
    }

    private boolean readBoolean(CompoundNBT nbt, String key, boolean defaultValue) {
        if (nbt.contains(key)) {
            return nbt.getBoolean(key);
        }
        return defaultValue;
    }

}
