package riskyken.armourersWorkshop.common.painting;

import riskyken.armourersWorkshop.utils.BitwiseUtils;

public enum PaintType {
    NONE(0),
    SKIN(1),
    HAIR(2),
    //RAINBOW(3),
    NORMAL(255);
    
    private final int key;
    
    private PaintType(int key) {
        this.key = key;
    }
    
    public static PaintType getPaintTypeFormSKey(byte key) {
        int uKey = key & 0xFF;
        return getPaintTypeFromUKey(uKey);
    }
    
    public static PaintType getPaintTypeFromColour(int colour) {
        int uKey = colour >>> 24;
        return getPaintTypeFromUKey(uKey);
    }
    
    public static PaintType getPaintTypeFromUKey(int uKey) {
        switch (uKey) {
        case 0:
            return PaintType.NONE;
        case 1:
            return PaintType.SKIN;
        case 2:
            return PaintType.HAIR;
        case 255:
            return PaintType.NORMAL;
        }
        return PaintType.NORMAL;
    }
    
    public static int setPaintTypeOnColour(PaintType paintType, int colour) {
        return BitwiseUtils.setUByteToInt(colour, 0, paintType.key);
    }
 }
