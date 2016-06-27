package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.crafting.ItemSkinningRecipes;
import riskyken.armourersWorkshop.common.inventory.slot.SlotInput;
import riskyken.armourersWorkshop.common.inventory.slot.SlotOutput;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinningTable;

public class ContainerSkinningTable extends Container {

    private final TileEntitySkinningTable tileEntity;
    private final IInventory craftingInventory;
    private final IInventory outputInventory;
    
    public ContainerSkinningTable(InventoryPlayer invPlayer, TileEntitySkinningTable tileEntity) {
        this.tileEntity = tileEntity;
        craftingInventory = tileEntity.getCraftingInventory();
        outputInventory = tileEntity.getOutputInventory();
        
        int hotBarY = 152;
        int playerInvY = 94;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, playerInvY + y * 18));
            }
        }
        
        addSlotToContainer(new SlotInput(craftingInventory, 0, 37, 22, this));
        addSlotToContainer(new SlotInput(craftingInventory, 1, 37, 58, this));
        addSlotToContainer(new SlotOutput(outputInventory, 0, 119, 40, this));
    }
    
    @Override
    public void onCraftMatrixChanged(IInventory p_75130_1_) {
        ItemSkinningRecipes.onCraft(craftingInventory);
        super.onCraftMatrixChanged(p_75130_1_);
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return player.getDistanceSq(tileEntity.getPos()) <= 64;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = getSlot(slotId);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotId > 35) {
                if (!this.mergeItemStack(stack, 9, 36, false)) {
                    if (!this.mergeItemStack(stack, 0, 9, false)) {
                        return null;
                    }
                }
            } else {
                boolean slotted = false;
                for (int i = 36; i < 38; i++) {
                    Slot targetSlot = getSlot(i);
                    if (this.mergeItemStack(stack, i, i + 1, false)) {
                        slotted = true;
                        break;
                    }
                }
                if (!slotted) {
                    return null;
                }
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            slot.onPickupFromSlot(player, stack);

            return result;
        }
        return null;
    }
}
