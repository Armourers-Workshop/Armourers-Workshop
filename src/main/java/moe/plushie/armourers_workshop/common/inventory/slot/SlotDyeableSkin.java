package moe.plushie.armourers_workshop.common.inventory.slot;

import moe.plushie.armourers_workshop.common.inventory.ContainerDyeTable;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotDyeableSkin extends Slot {
    
    private final ContainerDyeTable container;
    
    public SlotDyeableSkin(IInventory inventory, int slotIndex, int xPosition, int yPosition, ContainerDyeTable container) {
        super(inventory, slotIndex, xPosition, yPosition);
        this.container = container;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        SkinDescriptor sp = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (sp != null) {
            if (stack.getItem() == ModItems.equipmentSkin) {
                return true;
            } else {
                if (sp.lockSkin) {
                    return true;
                }
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
            SkinDescriptor sp = SkinNBTHelper.getSkinDescriptorFromStack(stack);
            if (sp != null) {
                if (stack.getItem() == ModItems.equipmentSkin) {
                    container.skinAdded(stack);
                } else {
                    if (sp.lockSkin) {
                        container.skinAdded(stack);
                    }
                }
            }
        }
        super.onSlotChanged();
    }
}
