package moe.plushie.armourers_workshop.common.blocks;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class BlockLocation {

    public final int x;
    public final int y;
    public final int z;
    
    public BlockLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public BlockLocation offset(EnumFacing direction) {
        return new BlockLocation(x + direction.getXOffset(), y + direction.getYOffset(), z + direction.getZOffset());
    }
    
    @Override
    public String toString() {
        return "BlockLocation [x=" + x + ", y=" + y + ", z=" + z + "]";
    }
    
    public double getDistance(double x, double y, double z) {
        double disX = this.x - x;
        double disY = this.y - y;
        double disZ = this.z - z;
        return (double)MathHelper.sqrt(disX * disX + disY * disY + disZ * disZ);
    }
    
    public double getDistance(BlockLocation blockLocation) {
        return getDistance(blockLocation.x, blockLocation.y, blockLocation.z);
    }
    
    @Override
    public BlockLocation clone() {
        return new BlockLocation(x, y, z);
    }
    
    @Override
    public int hashCode() {
        return x ^ y * 137 ^ z * 11317;
    }
    
    public BlockPos getPos() {
        return new BlockPos(x, y, z);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BlockLocation other = (BlockLocation) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        if (z != other.z)
            return false;
        return true;
    }
}
