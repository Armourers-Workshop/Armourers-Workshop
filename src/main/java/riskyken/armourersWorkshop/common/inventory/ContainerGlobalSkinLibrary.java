package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;

public class ContainerGlobalSkinLibrary extends Container implements IButtonPress {
    
    private TileEntityGlobalSkinLibrary tileEntity;
    
    public ContainerGlobalSkinLibrary(InventoryPlayer invPlayer, TileEntityGlobalSkinLibrary tileEntity) {
        this.tileEntity = tileEntity;
        int playerInvY = 20;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new SlotHidable(invPlayer, x, 5 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotHidable(invPlayer, x + y * 9 + 9, 5 + 18 * x, playerInvY + y * 18));
            }
        }
    }
    
    public TileEntityGlobalSkinLibrary getTileEntity() {
        return tileEntity;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return entityPlayer.getDistanceSq(tileEntity.xCoord + 0.5, tileEntity.yCoord + 0.5, tileEntity.zCoord + 0.5) <= 64 & !entityPlayer.isDead;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotId) {
        return null;
    }

    @Override
    public void buttonPressed(byte buttonId) {
        getTileEntity().buttonPressed(buttonId);
    }
}
