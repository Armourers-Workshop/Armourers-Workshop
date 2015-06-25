package riskyken.minecraftWrapper.common.world;

import net.minecraft.block.Block;
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
}
