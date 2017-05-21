package riskyken.armourersWorkshop.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.common.inventory.ContainerDyeTable;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.proxies.ClientProxy;

public class SlotDyeBottle extends Slot {
    
    private final ContainerDyeTable container;
    
    public SlotDyeBottle(IInventory inventory, int slotIndex, int xPosition, int yPosition, ContainerDyeTable container) {
        super(inventory, slotIndex, xPosition, yPosition);
        this.container = container;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        ItemStack skinStack = inventory.getStackInSlot(0);
        if (skinStack != null && skinStack.getItem() == ModItems.equipmentSkin) {
            if (stack.getItem() == ModItems.dyeBottle) {
                if (PaintingHelper.getToolHasPaint(stack)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void onSlotChanged() {
        ItemStack stack = getStack();
        if (stack == null) {
            container.dyeRemoved(getSlotIndex() - 1);
        } else {
            if (stack.getItem() == ModItems.dyeBottle) {
                container.dyeAdded(stack, getSlotIndex() - 1);
            }
        }
        super.onSlotChanged();
    }
    
    @Override
    public IIcon getBackgroundIconIndex() {
        return ClientProxy.dyeBottleSlotIcon;
    }
}
