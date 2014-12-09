package riskyken.armourersWorkshop.api.common.equipment;

import net.minecraftforge.common.util.ForgeDirection;



public enum EnumEquipmentPart {
    HEAD(
            8, 8, 8,
            -4, 0, -4,
            new int[] {1, 12, 6, 6, 6, 6},
            0, 0, 0,
            EnumBodyPart.HEAD),
    
    CHEST(
            8, 12, 4,
            -4, -12, -2,
            new int[] {1, 1, 5, 8, 2, 2},
            0, 0, 0,
            EnumBodyPart.CHEST),
    
    LEFT_ARM(
            4, 12, 4,
            -1, -10, -2,
            new int[] {1, 4, 2, 2, 3, 2},
            11, 0, 0,
            EnumBodyPart.LEFT_ARM),
    
    RIGHT_ARM(
            4, 12, 4,
            -3, -10, -2,
            new int[] {1, 4, 2, 2, 2, 3},
            -11, 0, 0,
            EnumBodyPart.RIGHT_ARM),
    
    LEFT_LEG(
            4, 12, 4,
            -2, -12, -2,
            new int[] {-4, 1, 2, 2, 2, 1},
            6, 0, 0,
            EnumBodyPart.LEFT_LEG),
    
    RIGHT_LEG(
            4, 12, 4,
            -2, -12, -2,
            new int[] {-4, 1, 2, 2, 1, 2},
            -6, 0, 0,
            EnumBodyPart.RIGHT_LEG),
    
    SKIRT(
            8, 12, 4,
            -4, -12, -2,
            new int[] {0, 3, 8, 8, 6, 6},
            0, 0, 0,
            EnumBodyPart.LEFT_LEG),
    
    LEFT_FOOT(
            4, 12, 4,
            -2, -12, -2,
            new int[] {1, -8, 4, 2, 2, 1},
            6, 0, 0,
            EnumBodyPart.LEFT_LEG),
    
    RIGHT_FOOT(
            4, 12, 4,
            -2, -12, -2,
            new int[] {1, -8, 4, 2, 1, 2},
            -6, 0, 0,
            EnumBodyPart.RIGHT_LEG),
    
    WEAPON(
            4, 4, 12,
            -2, -2, -2,
            new int[] {18, 28, 8, 0, 8, 8},
            0, 18, 0,
            null),
    BOW(
            4, 4, 12,
            -2, -2, -2,
            new int[] {18, 18, 8, 0, 8, 8},
            0, 18, 0,
            null);
    
    public final int xSize;
    public final int ySize;
    public final int zSize;
    
    public final int xOrigin;
    public final int yOrigin;
    public final int zOrigin;
    
    public final int[] buildSpace;
    
    public final int xLocation;
    public final int yLocation;
    public final int zLocation;
    
    public final EnumBodyPart bodyPart;
    
    EnumEquipmentPart(
            int xSize, int ySize, int zSize,
            int xOrigin, int yOrigin, int zOrigin,
            int[] buildSpace,
            int xLocation, int yLocation, int zLocation,
            EnumBodyPart bodyPart) {
        
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.zOrigin = zOrigin;
        
        this.buildSpace = buildSpace;
        
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.zLocation = zLocation;
        
        this.bodyPart = bodyPart;
    }
    
    public int getStartX() {
        return xOrigin - ((xSize / 2) + xOrigin) - getBuildSpaceForDirection(ForgeDirection.WEST);
    }
    
    public int getStartY() {
        return -getBuildSpaceForDirection(ForgeDirection.DOWN);
    }
    public int getStartZ() {
        return zOrigin - getBuildSpaceForDirection(ForgeDirection.NORTH);
    }
    
    public int getTotalXSize() {
        return getBuildSpaceForDirection(ForgeDirection.EAST) + getBuildSpaceForDirection(ForgeDirection.WEST) + xSize;
    }
    
    public int getTotalYSize() {
        return getBuildSpaceForDirection(ForgeDirection.UP) + getBuildSpaceForDirection(ForgeDirection.DOWN) + ySize;
    }
    
    public int getTotalZSize() {
        return getBuildSpaceForDirection(ForgeDirection.NORTH) + getBuildSpaceForDirection(ForgeDirection.SOUTH) + zSize;
    }
    
    public int getBuildSpaceForDirection(ForgeDirection direction) {
        if (direction != ForgeDirection.UNKNOWN) {
            return buildSpace[direction.ordinal()];
        }
        return 0;
    }
    
    public static EnumEquipmentPart getOrdinal(int id) {
        return EnumEquipmentPart.values()[id];
    }
}
