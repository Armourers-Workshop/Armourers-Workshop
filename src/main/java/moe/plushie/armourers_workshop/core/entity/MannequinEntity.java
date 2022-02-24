package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.base.AWTags;
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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;


@SuppressWarnings("NullableProblems")
public class MannequinEntity extends ArmorStandEntity {

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
        this.entityData.set(DATA_IS_VISIBLE, getBoolean(nbt, AWConstants.NBT.MANNEQUIN_IS_VISIBLE, true));
        this.entityData.set(DATA_EXTRA_RENDERER, getBoolean(nbt, AWConstants.NBT.MANNEQUIN_EXTRA_RENDER, true));

        CompoundNBT nbt1 = nbt.getCompound(AWConstants.NBT.MANNEQUIN_TEXTURE);
        if (!nbt1.isEmpty()) {
            setTextureDescriptor(new PlayerTextureDescriptor(nbt1));
        }
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
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> dataParameter) {
        super.onSyncedDataUpdated(dataParameter);
        // preload entity texture if needed
        if (dataParameter == DATA_TEXTURE && level.isClientSide()) {
            PlayerTextureLoader.getInstance().loadTexture(entityData.get(DATA_TEXTURE));
        }
    }

    @Override
    public boolean isBaby() {
        return super.isBaby() || entityData.get(DATA_IS_CHILD);
    }

    @Override
    public boolean isFallFlying() {
        return super.isFallFlying() || entityData.get(DATA_IS_FLYING);
    }

    @Override
    public boolean isInvisible() {
        return super.isInvisible() || !entityData.get(DATA_IS_VISIBLE);
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

    public void setTextureDescriptor(PlayerTextureDescriptor descriptor) {
        this.entityData.set(DATA_TEXTURE, descriptor);
    }

    public PlayerTextureDescriptor getTextureDescriptor() {
        return this.entityData.get(DATA_TEXTURE);
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

    private boolean getBoolean(CompoundNBT nbt, String key, boolean defaultValue) {
        if (nbt.contains(key)) {
            return nbt.getBoolean(key);
        }
        return defaultValue;
    }

}
