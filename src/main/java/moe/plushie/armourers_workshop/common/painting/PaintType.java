package moe.plushie.armourers_workshop.common.painting;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.utils.BitwiseUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;

public enum PaintType {
    NORMAL(255, 0),
    DYE_1(1, 1),
    DYE_2(2, 2),
    DYE_3(3, 3),
    DYE_4(4, 4),
    DYE_5(5, 5),
    DYE_6(6, 6),
    DYE_7(7, 7),
    DYE_8(8, 8),
    //WATER(100),
    //LAVA(101),
    //SKY(102),
    //STAR_FIELD(103),
    //RAINBOW(104),
    SKIN(253, 10),
    HAIR(254, 11),
    EYE(251, 12),
    MISC(252, 13),
    
    
    NONE(0, 9);
    
    
    private final int key;
    private final int markerId;
    
    private PaintType(int key, int markerId) {
        this.key = key;
        this.markerId = markerId;
    }
    
    public int getKey() {
        return key;
    }
    
    public int getMarkerId() {
        return markerId;
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
