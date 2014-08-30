package riskyken.armourersWorkshop.common.customarmor;

public enum ArmourPart {
    HEAD(1, 1, 20, 20, 20),
    CHEST(4, 1, 14, 14, 10),
    LEFT_ARM(2, 12, 8, 18, 8),
    RIGHT_ARM(12, 12, 8, 18, 8),
    LEFT_LEG(2, 7, 8, 13, 8),
    RIGHT_LEG(12, 7, 8, 13, 8),
    SKIRT(1, 1, 20, 13, 20);
    
    private final int xOffset;
    private final int zOffset;
    private final int xSize;
    private final int ySize;
    private final int zSize;
    
    ArmourPart(int xOffset, int zOffset, int xSize, int ySize, int zSize) {
        this.xOffset = xOffset;
        this.zOffset = zOffset;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }
    
    public int getXSize() {
        return xSize;
    }
    
    public int getYSize() {
        return ySize;
    }
    
    public int getZSize() {
        return zSize;
    }
    
    public int getXOffset() {
        return xOffset;
    }
    
    public int getZOffset() {
        return zOffset;
    }
    
    public static ArmourPart getOrdinal(int id) {
        return ArmourPart.values()[id];
    }
}
