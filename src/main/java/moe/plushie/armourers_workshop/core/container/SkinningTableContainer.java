package moe.plushie.armourers_workshop.core.container;

import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import moe.plushie.armourers_workshop.init.common.AWBlocks;
import moe.plushie.armourers_workshop.init.common.AWContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

@SuppressWarnings("NullableProblems")
public class SkinningTableContainer extends Container {

    private final IInventory craftingInventory = new Inventory(2);
    private final IInventory craftingResultInventory = new Inventory(1);

    private final IWorldPosCallable access;

    public SkinningTableContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(AWContainerTypes.SKINNING_TABLE, containerId);
        this.access = access;
        this.addPlayerSlots(playerInventory, 8, 94);
        this.addInputSlot(craftingInventory, 0, 37, 22);
        this.addInputSlot(craftingInventory, 1, 37, 58);
        this.addOutputSlot(craftingResultInventory, 0, 119, 40);
    }

    public SkinnableTileEntity getEntity() {
        TileEntity tileEntity = access.evaluate(World::getBlockEntity).orElse(null);
        if (tileEntity instanceof SkinnableTileEntity) {
            return (SkinnableTileEntity) tileEntity;
        }
        return null;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, AWBlocks.SKINNING_TABLE);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.access.execute((world, pos) -> this.clearContainer(player, world, craftingInventory));
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
        addSlot(new Slot(inventory, slot, x, y) {

            @Override
            public void setChanged() {
                super.setChanged();
                onCraftSlotChanges();
            }
        });
    }

    protected void addOutputSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {
            @Override
            public boolean mayPlace(ItemStack p_75214_1_) {
                return false;
            }

            @Override
            public void set(ItemStack itemStack) {
                if (itemStack.isEmpty()) {
                    SkinningRecipes.onCraft(craftingInventory);
                    super.set(itemStack);
                    onCraftSlotChanges();
                }
            }
        });
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

    private void onCraftSlotChanges() {
        craftingResultInventory.setItem(0, SkinningRecipes.getRecipeOutput(craftingInventory));
    }
}