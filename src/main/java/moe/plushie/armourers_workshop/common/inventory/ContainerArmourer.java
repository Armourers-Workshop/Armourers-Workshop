package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.common.inventory.slot.SlotOutput;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkinTemplate;
import moe.plushie.armourers_workshop.common.items.ItemArmourContainerItem;
import moe.plushie.armourers_workshop.common.items.ItemSkin;
import moe.plushie.armourers_workshop.common.items.ItemSkinTemplate;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerArmourer extends ModTileContainer<TileEntityArmourer> {
    
    public ContainerArmourer(InventoryPlayer invPlayer, TileEntityArmourer tileEntity) {
        super(invPlayer, tileEntity);

        addSlotToContainer(new SlotSkinTemplate(tileEntity, 0, 64, 21));
        addSlotToContainer(new SlotOutput(tileEntity, 1, 147, 21));

        addPlayerSlots(8, 142);
    }

    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();
            
            if ((
                    stack.getItem() instanceof ItemSkinTemplate) |
                    stack.getItem() instanceof ItemSkin |
                    stack.getItem() instanceof ItemArmourContainerItem) {
                if (!this.mergeItemStack(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
            

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(playerIn, stack);

            return result;
        }
        return ItemStack.EMPTY;
    }
}
