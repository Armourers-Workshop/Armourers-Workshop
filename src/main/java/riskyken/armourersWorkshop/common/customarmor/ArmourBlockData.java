package riskyken.armourersWorkshop.common.customarmor;

public class ArmourBlockData {
    

    @Override
    public String toString() {
        return "ArmourBlockData [x=" + x + ", y=" + y + ", z=" + z
                + ", colour=" + colour + ", glowing=" + glowing + "]";
    }
    
    public int x;
    public int y;
    public int z;
    public int colour;
    public boolean glowing;
    
    public ArmourBlockData() {
    }
    
    public ArmourBlockData(int x, int y, int z, int colour, boolean glowing) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.colour = colour;
        this.glowing = glowing;
    }
}
