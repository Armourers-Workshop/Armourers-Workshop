package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;

public class ContainerMiniArmourerBuilding extends Container {

    private TileEntityMiniArmourer tileEntity;
    
    public ContainerMiniArmourerBuilding(TileEntityMiniArmourer tileEntity) {
        this.tileEntity = tileEntity;
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
