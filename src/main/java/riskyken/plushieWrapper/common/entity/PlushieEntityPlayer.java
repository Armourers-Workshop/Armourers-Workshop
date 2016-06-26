package riskyken.plushieWrapper.common.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlushieEntityPlayer extends PlushieEntityLivingBase {

    public PlushieEntityPlayer(EntityPlayer entityPlayer) {
        super(entityPlayer);
    }
    
    public EntityPlayer getEntityPlayer() {
        return (EntityPlayer) entity;
    }
    
    public void openGui(Object mod, int modGuiId, World world, BlockPos bl) {
        ((EntityPlayer)entity).openGui(mod, modGuiId, world, bl.getX(), bl.getY(), bl.getZ());
    }
}
