package riskyken.armourersWorkshop.api.common.equipment;

public enum EnumBodyPart {
    HEAD(
            8, 8, 8,
            0, 0, false,
            4, -1, 4),
            
    //Should be 4, 0, 4
    CHEST(
            8, 12, 4,
            16, 16, false,
            4, 12, 2),
    //Should be 4, 11, 2
    LEFT_ARM(
            4, 12, 4,
            40, 16, false,
            1, 9, 2),
    //Should be 3, 10 ,2
    RIGHT_ARM(
            4, 12, 4,
            40, 16, true,
            3, 9, 2),
    //Should be 1, 10, 2
    LEFT_LEG(
            4, 12, 4,
            0, 16, false,
            2, 12, 2),
    
    RIGHT_LEG(
            4, 12, 4,
            0, 16, true,
            2, 12, 2),
            
    WEAPON_ARM(
            4, 12, 4,
            40, 16, true,
            0, 0, 0);
    
    public final int xSize;
    public final int ySize;
    public final int zSize;
    
    public final int textureX;
    public final int textureY;
    public final boolean mirrorTexture;
    
    public final int xOrigin;
    public final int yOrigin;
    public final int zOrigin;
    
    EnumBodyPart(int xSize, int ySize, int zSize, int textureX, int textureY, boolean mirrorTexture, int xOrigin, int yOrigin, int zOrigin) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        
        this.textureX = textureX;
        this.textureY = textureY;
        this.mirrorTexture = mirrorTexture;
        
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.zOrigin = zOrigin;
    }
}
