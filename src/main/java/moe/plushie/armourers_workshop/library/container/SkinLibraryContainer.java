package moe.plushie.armourers_workshop.library.container;

import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class SkinLibraryContainer extends Container {

    public int inventoryWidth = 162;
    public int inventoryHeight = 76;

    protected final IInventory inventory = new Inventory(2);
    protected final IWorldPosCallable access;

    protected final PlayerInventory playerInventory;

    public SkinLibraryContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        this(ModContainerTypes.SKIN_LIBRARY, containerId, playerInventory, access);
    }

    public SkinLibraryContainer(@Nullable ContainerType<?> containerType, int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(containerType, containerId);
        this.access = access;
        this.playerInventory = playerInventory;
        this.reload(0, 0, 240, 240);
    }

    public void reload(int x, int y, int width, int height) {
        int inventoryX = 6;
        int inventoryY = height - inventoryHeight - 4;
        this.slots.clear();
        this.addPlayerSlots(playerInventory, inventoryX, inventoryY);
        this.addInputSlot(inventory, 0, inventoryX, inventoryY - 27);
        this.addOutputSlot(inventory, 1, inventoryX + inventoryWidth - 22, inventoryY - 27);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.access.execute((world, pos) -> this.clearContainer(player, world, inventory));
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.SKIN_LIBRARY);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
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
        if (!moveItemStackTo(itemStack, 36, slots.size() - 1, false)) {
            return ItemStack.EMPTY;
        }
        slot.setChanged();
        return ItemStack.EMPTY;
    }

    protected void addInputSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y));
    }

    protected void addOutputSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y));
    }


    protected void addPlayerSlots(IInventory inventory, int slotsX, int slotsY) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, slotsX + col * 18, slotsY + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, slotsX + col * 18, slotsY + row * 18));
            }
        }
    }


    public class LockableSlot extends Slot {

        public LockableSlot(IInventory inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.getItem() instanceof BottleItem;
        }

        @Override
        public void setChanged() {
            super.setChanged();
//            applySkin(getOutputStack());
        }
    }
}