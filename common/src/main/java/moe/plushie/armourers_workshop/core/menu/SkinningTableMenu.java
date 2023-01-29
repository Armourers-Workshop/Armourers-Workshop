package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IContainerLevelAccess;
import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class SkinningTableMenu extends AbstractBlockContainerMenu {

    private final Container craftingInventory = new SimpleContainer(2);
    private final Container craftingResultInventory = new SimpleContainer(1);

    public SkinningTableMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IContainerLevelAccess access) {
        super(menuType, block, containerId, access);
        this.addPlayerSlots(playerInventory, 8, 94);
        this.addInputSlot(craftingInventory, 0, 37, 22);
        this.addInputSlot(craftingInventory, 1, 37, 58);
        this.addOutputSlot(craftingResultInventory, 0, 119, 40);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, craftingInventory);
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

    private void onCraftSlotChanges() {
        craftingResultInventory.setItem(0, SkinningRecipes.getRecipeOutput(craftingInventory));
    }
}
