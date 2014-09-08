package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.items.ItemCustomArmourTemplate;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageServerLibraryFileList;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;

public class ContainerArmourLibrary extends Container {

    private TileEntityArmourLibrary armourLibrary;
    
    public ContainerArmourLibrary(InventoryPlayer invPlayer, TileEntityArmourLibrary armourLibrary) {
        this.armourLibrary = armourLibrary;

        addSlotToContainer(new Slot(armourLibrary, 0, 64, 21));
        addSlotToContainer(new SlotOutput(armourLibrary, 1, 147, 21));

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 232));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 174 + y * 18));
            }
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        Slot slot = getSlot(slotID);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotID < 2) {
                if (!this.mergeItemStack(stack, 11, 38, false)) {
                    if (!this.mergeItemStack(stack, 2, 11, false)) {
                        return null;
                    }
                }
            } else {
                if (stack.getItem() instanceof ItemCustomArmourTemplate) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return null;
                    }
                } else {
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
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return armourLibrary.isUseableByPlayer(player);
    }
    
    public TileEntityArmourLibrary getTileEntity() {
        return armourLibrary;
    }
    
    public boolean sentList;
    
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object player : crafters) {
            if (!sentList) {
                if (player instanceof EntityPlayerMP) {
                    sentList = true;
                    PacketHandler.networkWrapper.sendTo(new MessageServerLibraryFileList(armourLibrary.getFileNames()), (EntityPlayerMP) player);
                }
            }
        }
    }
}
