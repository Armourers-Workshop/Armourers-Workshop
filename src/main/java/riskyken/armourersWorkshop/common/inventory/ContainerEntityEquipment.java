package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class ContainerEntityEquipment extends Container {

    public ContainerEntityEquipment(InventoryPlayer invPlayer, InventoryEntitySkin skinInventory) {
        
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinHead, skinInventory, 0, 44, 28));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinChest, skinInventory, 1, 62, 28));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinLegs, skinInventory, 2, 80, 28));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinSkirt, skinInventory, 3, 98, 28));
        addSlotToContainer(new SlotEquipmentSkin(SkinTypeRegistry.skinFeet, skinInventory, 4, 116, 28));
        
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
