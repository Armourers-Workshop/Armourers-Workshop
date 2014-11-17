package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.tileentities.TileEntityParticleEffect;

public class ContainerParticleEffect extends Container {

    TileEntityParticleEffect tileEntity;
    
    public ContainerParticleEffect(TileEntityParticleEffect tileEntity) {
        this.tileEntity = tileEntity;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
}
