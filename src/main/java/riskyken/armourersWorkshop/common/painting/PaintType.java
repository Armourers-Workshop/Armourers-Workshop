package riskyken.armourersWorkshop.common.painting;

import riskyken.armourersWorkshop.utils.BitwiseUtils;

public enum PaintType {
    NONE(0),
    DYE_1(1),
    DYE_2(2),
    DYE_3(3),
    DYE_4(4),
    DYE_5(5),
    DYE_6(6),
    DYE_7(7),
    DYE_8(8),
    WATER(248),
    LAVA(249),
    SKY(250),
    STAR_FIELD(251),
    RAINBOW(252),
    SKIN(253),
    HAIR(254),
    NORMAL(255);
    
    private final int key;
    
    private PaintType(int key) {
        this.key = key;
    }
    
    public int getKey() {
        return key;
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
