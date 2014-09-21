package riskyken.armourersWorkshop.common;

public enum BodyPart {
    HEAD(
            8, 8, 8,
            0, 0, false
            ),
    
    CHEST(
            8, 12, 4,
            16, 16, false
            ),
    
    LEFT_ARM(
            4, 12, 4,
            40, 16, false
            ),
    
    RIGHT_ARM(
            4, 12, 4,
            40, 16, true
            ),
    
    LEFT_LEG(
            4, 12, 4,
            0, 16, false
            ),
    
    RIGHT_LEG(
            4, 12, 4,
            0, 16, true
            );
    
    public final int xSize;
    public final int ySize;
    public final int zSize;
    
    public final int textureX;
    public final int textureY;
    public final boolean mirrorTexture;
    
    BodyPart(int xSize, int ySize, int zSize, int textureX, int textureY, boolean mirrorTexture) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        
        this.textureX = textureX;
        this.textureY = textureY;
        this.mirrorTexture = mirrorTexture;
    }
}
