package riskyken.armourersWorkshop.common.customarmor;

public enum ArmourPart {
    HEAD(5, 0, 5, 12, 16, 12),
    CHEST(4, 0, 1, 14, 14, 10),
    LEFT_ARM(3, 0, 13, 6, 14, 6),
    RIGHT_ARM(13, 0, 13, 6, 14, 6),
    LEFT_LEG(4, 0, 8, 6, 13, 6),
    RIGHT_LEG(12, 0, 8, 6, 13, 6),
    SKIRT(1, 0, 1, 20, 13, 20);
    
    private final int xOffset;
    private final int yOffset;
    private final int zOffset;
    private final int xSize;
    private final int ySize;
    private final int zSize;
    
    ArmourPart(int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
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
    
    public int getYOffset() {
        return yOffset;
    }
    
    public int getZOffset() {
        return zOffset;
    }
    
    public static ArmourPart getOrdinal(int id) {
        return ArmourPart.values()[id];
    }
}
