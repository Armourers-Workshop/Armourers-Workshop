package riskyken.plushieWrapper.common.world;

public class BlockLocation {

    public int x;
    public int y;
    public int z;
    
    public BlockLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int hashCode() {
        return x ^ y * 31 ^ z * 137;
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

    @Override
    public String toString() {
        return "BlockLocation [x=" + x + ", y=" + y + ", z=" + z + "]";
    }
}
