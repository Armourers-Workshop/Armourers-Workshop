package riskyken.armourersWorkshop.common.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.inventory.ContainerDyeTable;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class SlotDyeableSkin extends Slot {
    
    private final ContainerDyeTable container;
    
    public SlotDyeableSkin(IInventory inventory, int slotIndex, int xPosition, int yPosition, ContainerDyeTable container) {
        super(inventory, slotIndex, xPosition, yPosition);
        this.container = container;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.getItem() == ModItems.equipmentSkin) {
            if (SkinNBTHelper.stackHasSkinData(stack)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }
    
    @Override
    public void onSlotChanged() {
        ItemStack stack = getStack();
        if (stack == null) {
            container.skinRemoved();
        } else {
            if (stack.getItem() == ModItems.equipmentSkin) {
                container.skinAdded(stack);
            }
        }
        super.onSlotChanged();
    }
}
