package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWTags;
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

    public static final DataParameter<Integer> DATA_OPTIONS = EntityDataManager.defineId(MannequinEntity.class, DataSerializers.INT);


    public MannequinEntity(EntityType<? extends MannequinEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_OPTIONS, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.setOption(Option.NO_CLIP, nbt.getBoolean("NoClip"));
        this.setOption(Option.IS_CHILD, nbt.getBoolean("Child"));
        this.setOption(Option.IS_FLYING, nbt.getBoolean("Flying"));
        this.setOption(Option.EXTRA_RENDER, nbt.getBoolean("ExtraRender"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("NoClip", getOption(Option.NO_CLIP));
        nbt.putBoolean("Child", getOption(Option.IS_CHILD));
        nbt.putBoolean("Flying", getOption(Option.IS_FLYING));
        nbt.putBoolean("ExtraRender", getOption(Option.EXTRA_RENDER));
    }


    @Override
    public boolean isBaby() {
        return getOption(Option.IS_CHILD) || super.isBaby();
    }

    @Override
    public boolean isFallFlying() {
        return getOption(Option.IS_FLYING);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive() && getOption(Option.NO_CLIP);
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

    public boolean getOption(Option option) {
        if (option == Option.IS_VISIBLE) {
            return !isInvisible();
        }
        int flag = this.entityData.get(DATA_OPTIONS);
        if (((flag >> option.ordinal()) & 1) != 0) {
            return !option.defaultValue;
        }
        return option.defaultValue;
    }

    public void setOption(Option option, boolean value) {
        if (option == Option.IS_VISIBLE) {
            setInvisible(!value);
            return;
        }
        int flag = this.entityData.get(DATA_OPTIONS);
        int newFlag = flag;
        if (value != option.defaultValue) {
            newFlag |= 1 << option.ordinal();
        } else {
            newFlag &= ~(1 << option.ordinal());
        }
        if (newFlag != flag) {
            this.entityData.set(DATA_OPTIONS, newFlag);
        }
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

    public enum Option {
        IS_VISIBLE(true),
        IS_CHILD(false),
        IS_FLYING(false),
        NO_CLIP(true),
        EXTRA_RENDER(true);
        final boolean defaultValue;

        Option(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}
