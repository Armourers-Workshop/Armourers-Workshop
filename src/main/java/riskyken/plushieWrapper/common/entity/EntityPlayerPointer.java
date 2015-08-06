package riskyken.plushieWrapper.common.entity;

import net.minecraft.entity.player.EntityPlayer;
import riskyken.plushieWrapper.common.world.BlockLocation;
import riskyken.plushieWrapper.common.world.WorldPointer;

public class EntityPlayerPointer extends EntityLivingBasePointer {

    public EntityPlayerPointer(EntityPlayer entityPlayer) {
        super(entityPlayer);
    }
    
    public EntityPlayer getEntityPlayer() {
        return (EntityPlayer) entity;
    }
    
    public void openGui(Object mod, int modGuiId, WorldPointer world, BlockLocation bl) {
        ((EntityPlayer)entity).openGui(mod, modGuiId, world.getMinecraftWorld(), bl.x, bl.y, bl.z);
    }
}
