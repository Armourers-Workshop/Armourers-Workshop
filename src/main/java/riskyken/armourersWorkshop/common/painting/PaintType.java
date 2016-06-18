package riskyken.armourersWorkshop.common.painting;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.BitwiseUtils;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public enum PaintType {
    NORMAL(255),
    DYE_1(1),
    DYE_2(2),
    DYE_3(3),
    DYE_4(4),
    DYE_5(5),
    DYE_6(6),
    DYE_7(7),
    DYE_8(8),
    NONE(0),
    //WATER(248),
    //LAVA(249),
    //SKY(250),
    //STAR_FIELD(251),
    //RAINBOW(252),
    SKIN(253),
    HAIR(254);
    
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
        for (int i = 0; i < values().length; i++) {
            if (values()[i].key == uKey) {
                return values()[i];
            }
        }
        return PaintType.NORMAL;
    }
    
    public static int setPaintTypeOnColour(PaintType paintType, int colour) {
        return BitwiseUtils.setUByteToInt(colour, 0, paintType.key);
    }
    
    public String getLocalizedName() {
        String unlocalizedText = "paintType." + LibModInfo.ID.toLowerCase() + ":";
        unlocalizedText += this.name().toLowerCase() + ".name";
        return TranslateUtils.translate(unlocalizedText);
    }
 }
