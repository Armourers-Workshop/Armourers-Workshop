package riskyken.armourersWorkshop.api.common.equipment;



public enum EnumEquipmentPart {
    HEAD(
            8, 8, 8,
            4, 0, 4,
            6, 1, 12, 6,
            0, 0,
            EnumBodyPart.HEAD),
    
    CHEST(
            8, 12, 4,
            4, 12, 2,
            1, 1, 1, 1,
            0, 0,
            EnumBodyPart.CHEST),
    
    LEFT_ARM(
            4, 12, 4,
            2, 10, 2,
            1, 1, 4, 1,
            12, 0,
            EnumBodyPart.LEFT_ARM),
    
    RIGHT_ARM(
            4, 12, 4,
            2, 10, 2,
            1, 1, 4, 1,
            -12, 0,
            EnumBodyPart.RIGHT_ARM),
    
    LEFT_LEG(
            4, 12, 4,
            2, 12, 2,
            1, 1, 1, 1,
            6, 0,
            EnumBodyPart.LEFT_LEG),
    
    RIGHT_LEG(
            4, 12, 4,
            2, 12, 2,
            1, 1, 1, 1,
            -6, 0,
            EnumBodyPart.RIGHT_LEG),
    
    SKIRT(
            8, 12, 4,
            4, 12, 2,
            1, 1, 1, 1,
            0, 0,
            null),
    
    LEFT_FOOT(
            4, 5, 4,
            2, 12, 2,
            1, 1, 1, 1,
            6, 0,
            EnumBodyPart.LEFT_LEG),
    
    RIGHT_FOOT(
            4, 5, 4,
            2, 12, 2,
            1, 1, 1, 1,
            -6, 0,
            EnumBodyPart.RIGHT_LEG),
    
    WEAPON(
            20, 40, 20,
            10, 20, 10,
            0, 0, 0, 0,
            0, 0,
            null);
    
    public final int xSize;
    public final int ySize;
    public final int zSize;
    
    public final int xOrigin;
    public final int yOrigin;
    public final int zOrigin;
    
    public final int xBuildSpace;
    public final int botBuildSpace;
    public final int topBuildSpace;
    public final int zBuildSpace;
    
    public final int xLocation;
    public final int zLocation;
    
    public final EnumBodyPart bodyPart;
    
    EnumEquipmentPart(
            int xSize, int ySize, int zSize,
            int xOrigin, int yOrigin, int zOrigin,
            int xBuildSpace, int botBuildSpace, int topBuildSpace, int zBuildSpace,
            int xLocation, int zLocation,
            EnumBodyPart bodyPart) {
        
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.zOrigin = zOrigin;
        
        this.xBuildSpace = xBuildSpace;
        this.botBuildSpace = botBuildSpace;
        this.topBuildSpace = topBuildSpace;
        this.zBuildSpace = zBuildSpace;
        
        this.xLocation = xLocation;
        this.zLocation = zLocation;
        
        this.bodyPart = bodyPart;
    }
    
    public static EnumEquipmentPart getOrdinal(int id) {
        return EnumEquipmentPart.values()[id];
    }
}
