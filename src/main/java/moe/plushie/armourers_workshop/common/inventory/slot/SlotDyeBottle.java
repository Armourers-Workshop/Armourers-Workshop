package moe.plushie.armourers_workshop.common.inventory.slot;

import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.inventory.ContainerDyeTable;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotDyeBottle extends Slot {
    
    private final ContainerDyeTable container;
    private boolean locked;
    
    public SlotDyeBottle(IInventory inventory, int slotIndex, int xPosition, int yPosition, ContainerDyeTable container) {
        super(inventory, slotIndex, xPosition, yPosition);
        this.container = container;
        this.locked = false;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        ItemStack skinStack = inventory.getStackInSlot(0);
        if (skinStack != null && SkinNBTHelper.stackHasSkinData(skinStack)) {
            if (stack.getItem() == ModItems.dyeBottle) {
                if (PaintingHelper.getToolHasPaint(stack)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean canTakeStack(EntityPlayer player) {
        if (!ConfigHandler.lockDyesOnSkins) {
            return true;
        }
        return !locked;
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
    /*
    @Override
    public IIcon getBackgroundIconIndex() {
        return ClientProxy.dyeBottleSlotIcon;
    }*/
}
