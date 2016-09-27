package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;

public class ContainerGlobalSkinLibrary extends Container {
    
    private TileEntityGlobalSkinLibrary tileEntity;
    
    public ContainerGlobalSkinLibrary(TileEntityGlobalSkinLibrary tileEntity) {
        this.tileEntity = tileEntity;
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
}
