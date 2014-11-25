package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

public class ContainerMannequin extends Container {

    private TileEntityMannequin tileEntity;
    
    public ContainerMannequin(InventoryPlayer invPlayer, TileEntityMannequin tileEntity) {
        this.tileEntity = tileEntity;
        
        for (int y = 0; y < 6; y++) {
            addSlotToContainer(new SlotEquipmentSkin(EnumEquipmentType.getOrdinal(y + 1) ,tileEntity, y, 11, 25 + 23 * y));
        }
        
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 48 + 18 * x, 232));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 48 + 18 * x, 174 + y * 18));
            }
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        return null;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
    
    public TileEntityMannequin getTileEntity() {
        return tileEntity;
    }
}
