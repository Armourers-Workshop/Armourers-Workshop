package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.core.blockentity.SkinningTableBlockEntity;
import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class SkinningTableMenu extends AbstractBlockEntityMenu<SkinningTableBlockEntity> {

    private final Container inventory;

    public SkinningTableMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IGlobalPos access) {
        super(menuType, block, containerId, access);
        this.inventory = blockEntity.getInventory();
        this.addPlayerSlots(playerInventory, 8, 94);
        this.addInputSlot(inventory, 1, 37, 22);
        this.addInputSlot(inventory, 2, 37, 58);
        this.addOutputSlot(inventory, 0, 119, 40);
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
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            @Override
            public void set(ItemStack itemStack) {
                if (itemStack.isEmpty()) {
                    SkinningRecipes.onCraft(inventory, blockEntity.getOptions());
                    super.set(itemStack);
                    onCraftSlotChanges();
                }
            }
        });
    }

    public void onCraftSlotChanges() {
        inventory.setItem(0, SkinningRecipes.getRecipeOutput(inventory, blockEntity.getOptions()));
    }
}
