package riskyken.minecraftWrapper.common.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class WorldPointer {
    
    private World world;
    
    public WorldPointer(World world) {
        this.world = world;
    }
    
    public Block getBlock(BlockLocation blockLocation) {
        return world.getBlock(blockLocation.x, blockLocation.y, blockLocation.z);
    }
    
    public boolean isRemote() {
        return world.isRemote;
    }
    
    public World getMinecraftWorld() {
        return world;
    }

    public TileEntity getTileEntity(BlockLocation blockLocation) {
        return world.getTileEntity(blockLocation.x, blockLocation.y, blockLocation.z);
    }
    
    public Random rand() {
        return world.rand;
    }

    public void playSoundEffect(double x, double y, double z, String soundName, float volume, float pitch) {
        world.playSoundEffect(x, y, z, soundName, volume, pitch);
    }

    public int getBlockMetadata(BlockLocation blockLocation) {
        return world.getBlockMetadata(blockLocation.x, blockLocation.y, blockLocation.z);
    }
    
    /**
     * 
     * @param blockLocation Location of the block to update.
     * @param meta New meta data for the block.
     * @param flags 1 = update block, 2 = send to client, 4 = don't re-render (client only).
     */
    public void setBlockMetaData(BlockLocation blockLocation, int meta, int flags) {
        world.setBlockMetadataWithNotify(blockLocation.x, blockLocation.y, blockLocation.z, meta, flags);
    }
}
