package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SkinningTableMenu extends AbstractBlockContainerMenu {

    private final Container craftingInventory = new SimpleContainer(2);
    private final Container craftingResultInventory = new SimpleContainer(1);

    public SkinningTableMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(containerId, ModMenus.SKINNING_TABLE, ModBlocks.SKINNING_TABLE, access);
        this.addPlayerSlots(playerInventory, 8, 94);
        this.addInputSlot(craftingInventory, 0, 37, 22);
        this.addInputSlot(craftingInventory, 1, 37, 58);
        this.addOutputSlot(craftingResultInventory, 0, 119, 40);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((world, pos) -> this.clearContainer(player, world, craftingInventory));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }

    protected void addInputSlot(Container inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {

            @Override
            public void setChanged() {
                super.setChanged();
                onCraftSlotChanges();
            }
        });
    }

    protected void addOutputSlot(Container inventory, int slot, int x, int y) {
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

    protected void addPlayerSlots(Container inventory, int slotsX, int slotsY) {
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