package moe.plushie.armourers_workshop.core.container;

import moe.plushie.armourers_workshop.api.common.IHasInventory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class AbstractBlockContainer<T extends Block> extends Container {

    protected final T block;
    protected final IWorldPosCallable access;

    public AbstractBlockContainer(int containerId, @Nullable ContainerType<?> containerType, T block, IWorldPosCallable access) {
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

    @Nullable
    public TileEntity getTileEntity() {
        return access.evaluate(World::getBlockEntity).orElse(null);
    }

    @Nullable
    public IInventory getTileInventory() {
        TileEntity tileEntity = access.evaluate(World::getBlockEntity).orElse(null);
        if (tileEntity instanceof IHasInventory) {
            return ((IHasInventory) tileEntity).getInventory();
        }
        return null;
    }
}