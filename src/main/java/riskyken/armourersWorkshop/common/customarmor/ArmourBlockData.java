package riskyken.armourersWorkshop.common.customarmor;

public class ArmourBlockData {

    @Override
    public String toString() {
        return "ArmourBlockData [x=" + x + ", y=" + y + ", z=" + z
                + ", colour=" + colour + "]";
    }

    public int x;
    public int y;
    public int z;
    public int colour;

    public ArmourBlockData() {
    }

    public ArmourBlockData(int x, int y, int z, int colour) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.colour = colour;
    }
}
