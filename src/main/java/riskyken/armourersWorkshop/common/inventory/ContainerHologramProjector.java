package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.inventory.slot.SlotSkinTemplate;
import riskyken.armourersWorkshop.common.tileentities.TileEntityHologramProjector;

public class ContainerHologramProjector extends Container {

    private final TileEntityHologramProjector tileEntity;
    
    public ContainerHologramProjector(InventoryPlayer invPlayer, TileEntityHologramProjector tileEntity) {
        this.tileEntity = tileEntity;
        
        int playerInvY = 142;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new SlotHidable(invPlayer, x, 8 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotHidable(invPlayer, x + y * 9 + 9, 8 + 18 * x, playerInvY + y * 18));
            }
        }
        
        addSlotToContainer(new SlotSkinTemplate(tileEntity, 0, 8, 110));
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return tileEntity.isUseableByPlayer(entityplayer);
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slot) {
        return null;
    }
    
    public TileEntityHologramProjector getTileEntity() {
        return tileEntity;
    }
}
