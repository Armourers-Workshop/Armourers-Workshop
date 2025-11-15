package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.compat.core.menu.AbstractContainerSlot;
import moe.plushie.armourers_workshop.core.blockentity.SkinningTableBlockEntity;
import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class SkinningTableMenu extends BlockEntityContainerMenu<SkinningTableBlockEntity> {

    private final Container inventory;

    public SkinningTableMenu(IMenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IGlobalPos access) {
        super(menuType, block, containerId, access);
        this.inventory = blockEntity.getContainer();
        this.addPlayerSlots(playerInventory, 8, 94);
        this.addInputSlot(inventory, 1, 37, 22);
        this.addInputSlot(inventory, 2, 37, 58);
        this.addOutputSlot(inventory, 0, 119, 40);
    }

    protected void addInputSlot(Container inventory, int slot, int x, int y) {
        addSlot(new AbstractContainerSlot(inventory, slot, x, y) {

            @Override
            protected void abi$setChanged() {
                super.abi$setChanged();
                onCraftSlotChanges();
            }
        });
    }

    protected void addOutputSlot(Container inventory, int slot, int x, int y) {
        addSlot(new AbstractContainerSlot(inventory, slot, x, y) {
            @Override
            protected boolean abi$mayPlace(ItemStack itemStack) {
                return false;
            }

            @Override
            protected void abi$setItem(ItemStack itemStack) {
                if (itemStack.isEmpty()) {
                    SkinningRecipes.onCraft(inventory, blockEntity.options());
                    super.abi$setItem(itemStack);
                    onCraftSlotChanges();
                }
            }
        });
    }

    public void onCraftSlotChanges() {
        inventory.setItem(0, SkinningRecipes.getRecipeOutput(inventory, blockEntity.options()));
    }

    @Override
    protected ItemStack abi$quickMoveStack(Player player, int index) {
        return abi$quickMoveStack(player, index, slots.size() - 1);
    }
}
