package riskyken.armourersWorkshop.common.inventory;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.inventory.slot.SlotSkin;

public class ContainerEntityEquipment extends Container {

    public ContainerEntityEquipment(InventoryPlayer invPlayer, InventoryEntitySkin skinInventory) {
        
        ArrayList<ISkinType> skinTypes = skinInventory.getSkinTypes();
        for (int i = 0; i < skinTypes.size(); i++) {
            addSlotToContainer(new SlotSkin(skinTypes.get(i), skinInventory, i, 8 + i * 18, 21));
        }
        
        int hotBarY = 124;
        int playerInvY = 66;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, playerInvY + y * 18));
            }
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return !entityPlayer.isDead;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotId) {
        return null;
    }
}
