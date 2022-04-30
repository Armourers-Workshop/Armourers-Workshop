package moe.plushie.armourers_workshop.core.container;

import moe.plushie.armourers_workshop.api.common.IHasInventory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class AbstractBlockContainer extends Container {

    protected final Block block;
    protected final IWorldPosCallable access;

    public AbstractBlockContainer(int containerId, @Nullable ContainerType<?> containerType, Block block, IWorldPosCallable access) {
        super(containerType, containerId);
        this.access = access;
        this.block = block;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, this.block);
    }

    public ItemStack quickMoveStack(PlayerEntity player, int index, int slotSize) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = slot.getItem();
        if (index >= 36) {
            if (!(moveItemStackTo(itemStack, 9, 36, false) || moveItemStackTo(itemStack, 0, 9, false))) {
                return ItemStack.EMPTY;
            }
            slot.set(ItemStack.EMPTY);
            return itemStack.copy();
        }
        if (!moveItemStackTo(itemStack, 36, slotSize, false)) {
            return ItemStack.EMPTY;
        }
        slot.setChanged();
        return ItemStack.EMPTY;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends TileEntity> T getTileEntity(Class<T> clazz) {
        TileEntity[] tileEntities = {null};
        access.execute((world, pos) -> tileEntities[0] = world.getBlockEntity(pos));
        if (clazz.isInstance(tileEntities[0])) {
            return (T) tileEntities[0];
        }
        return null;
    }

    @Nullable
    public IInventory getTileInventory() {
        TileEntity tileEntity = getTileEntity(TileEntity.class);
        if (tileEntity instanceof IHasInventory) {
            return ((IHasInventory) tileEntity).getInventory();
        }
        return null;
    }
}