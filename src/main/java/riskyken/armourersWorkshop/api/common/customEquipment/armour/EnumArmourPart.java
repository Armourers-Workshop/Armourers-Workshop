package riskyken.armourersWorkshop.api.common.customEquipment.armour;

public enum EnumArmourPart {
    HEAD(
            1, 0, 1,
            20, 20, 20,
            11, 1, 11
            ),
    
    CHEST(
            4, 0, 1,
            14, 14, 10,
            11, 13, 6
            ),
    
    LEFT_ARM(
            1, 0, 11,
            9, 18, 10,
            7, 11, 16
            ),
    
    RIGHT_ARM(
            12, 0, 11,
            9, 18, 10,
            15, 11, 16
            ),
    
    LEFT_LEG(
            2, 5, 7,
            8, 9, 8,
            6, 14, 11
            ),
    
    RIGHT_LEG(
            12, 5, 7,
            8, 9, 8,
            16, 14, 11
            ),
    
    SKIRT(
            1, 0, 1,
            20, 14, 20,
            11, 14, 11
            ),
    
    LEFT_FOOT(
            2, 0, 5,
            8, 5, 12,
            6, 14, 11
            ),
    
    RIGHT_FOOT(
            12, 0, 5,
            8, 5, 12,
            16, 14, 11
            );
    
    public final int xOffset;
    public final int yOffset;
    public final int zOffset;
    
    public final int xSize;
    public final int ySize;
    public final int zSize;
    
    public final int xOrigin;
    public final int yOrigin;
    public final int zOrigin;
    
    EnumArmourPart(int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, int xOrigin, int yOrigin, int zOrigin) {
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
    
    public static EnumArmourPart getOrdinal(int id) {
        return EnumArmourPart.values()[id];
    }
}
