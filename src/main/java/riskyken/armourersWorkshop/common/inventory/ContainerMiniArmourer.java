package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.inventory.slot.SlotEquipmentSkinTemplate;
import riskyken.armourersWorkshop.common.inventory.slot.SlotOutput;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;

public class ContainerMiniArmourer extends Container {

    private TileEntityMiniArmourer tileEntity;
    
    public ContainerMiniArmourer(InventoryPlayer invPlayer, TileEntityMiniArmourer tileEntity) {
        this.tileEntity = tileEntity;
        
        addSlotToContainer(new SlotEquipmentSkinTemplate(tileEntity, 0, 37, 58));
        addSlotToContainer(new SlotOutput(tileEntity, 1, 119, 58));
        
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
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotID) {
        return null;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return tileEntity.isUseableByPlayer(entityPlayer);
    }
    
    public TileEntityMiniArmourer getTileEntity() {
        return tileEntity;
    }
}
