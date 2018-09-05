package moe.plushie.armourers_workshop.common.tileentities;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class ModTileEntity extends TileEntity {
    
    /**
     * Sync the tile entity with the clients.
     */
    public void syncWithClients() {
        if (!getWorld().isRemote) {
            syncWithNearbyPlayers(this);
        }
        
        //worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    /**
     * Marks the tile entity as dirty and sync it with the clients.
     */
    public void dirtySync() {
        markDirty();
        syncWithClients();
    }
    
    public static void syncWithNearbyPlayers(TileEntity tileEntity) {
        World world = tileEntity.getWorld();
        List<EntityPlayer> players = world.playerEntities;
        for (EntityPlayer player : players) {
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP mp = (EntityPlayerMP)player;
                if (tileEntity.getDistanceSq(mp.posX, mp.posY, mp.posZ) < 64) {
                    mp.connection.sendPacket(tileEntity.getUpdatePacket());
                }
            }
        }
    }
}
