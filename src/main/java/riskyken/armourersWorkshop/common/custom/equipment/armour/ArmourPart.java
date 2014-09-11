package riskyken.armourersWorkshop.common.custom.equipment.armour;

public enum ArmourPart {
    HEAD(1, 0, 1, 20, 20, 20, 11, 1, 11),
    
    CHEST(4, 0, 1, 14, 14, 10, 11, 13, 6),
    
    LEFT_ARM(2, 0, 11, 8, 18, 10, 7, 11, 16),
    
    RIGHT_ARM(12, 0, 11, 8, 18, 10, 15, 11, 16),
    
    LEFT_LEG(2, 5, 7, 8, 9, 8, 6, 14, 11),
    
    RIGHT_LEG(12, 5, 7, 8, 9, 8, 16, 14, 11),
    
    SKIRT(1, 0, 1, 20, 13, 20, 11, 14, 11),
    
    LEFT_FOOT(2, 0, 6, 8, 5, 10, 6, 14, 11),
    
    RIGHT_FOOT(12, 0, 6, 8, 5, 10, 16, 14, 11);
    
    private final int xOffset;
    private final int yOffset;
    private final int zOffset;
    
    private final int xSize;
    private final int ySize;
    private final int zSize;
    
    private final int xOrigin;
    private final int yOrigin;
    private final int zOrigin;
    
    ArmourPart(int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, int xOrigin, int yOrigin, int zOrigin) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.zOrigin = zOrigin;
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
    
    public int getXOrigin() {
        return xOrigin;
    }
    
    public int getYOrigin() {
        return yOrigin;
    }
    
    public int getZOrigin() {
        return zOrigin;
    }
    
    public static ArmourPart getOrdinal(int id) {
        return ArmourPart.values()[id];
    }
}
