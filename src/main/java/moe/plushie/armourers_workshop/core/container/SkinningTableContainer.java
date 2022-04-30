package moe.plushie.armourers_workshop.core.container;

import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.block.Block;
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
public class SkinningTableContainer extends AbstractBlockContainer {

    private final IInventory craftingInventory = new Inventory(2);
    private final IInventory craftingResultInventory = new Inventory(1);

    public SkinningTableContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(containerId, ModContainerTypes.SKINNING_TABLE, ModBlocks.SKINNING_TABLE, access);
        this.addPlayerSlots(playerInventory, 8, 94);
        this.addInputSlot(craftingInventory, 0, 37, 22);
        this.addInputSlot(craftingInventory, 1, 37, 58);
        this.addOutputSlot(craftingResultInventory, 0, 119, 40);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.access.execute((world, pos) -> this.clearContainer(player, world, craftingInventory));
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
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