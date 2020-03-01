package moe.plushie.armourers_workshop.common.painting;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.utils.BitwiseUtils;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class PaintRegistry {

    public static final PaintType PAINT_TYPE_NONE = new PaintType(0, 9, "none");

    public static final PaintType PAINT_TYPE_DYE_1 = new PaintType(1, 1, true, "dye_1");
    public static final PaintType PAINT_TYPE_DYE_2 = new PaintType(2, 2, true, "dye_2");
    public static final PaintType PAINT_TYPE_DYE_3 = new PaintType(3, 3, true, "dye_3");
    public static final PaintType PAINT_TYPE_DYE_4 = new PaintType(4, 4, true, "dye_4");
    public static final PaintType PAINT_TYPE_DYE_5 = new PaintType(5, 5, true, "dye_5");
    public static final PaintType PAINT_TYPE_DYE_6 = new PaintType(6, 6, true, "dye_6");
    public static final PaintType PAINT_TYPE_DYE_7 = new PaintType(7, 7, true, "dye_7");
    public static final PaintType PAINT_TYPE_DYE_8 = new PaintType(8, 8, true, "dye_8");

    public static final PaintType PAINT_TYPE_RAINBOW = new PaintType(104, 14, true, "rainbow").setTextureUV(1F, 0F);
    public static final PaintType PAINT_TYPE_PULSE_1 = new PaintType(105, 15, "pulse_1").setTextureUV(2F, 0F);
    public static final PaintType PAINT_TYPE_PULSE_2 = new PaintType(106, 16, "pulse_2").setTextureUV(3F, 0F);

    public static final PaintType PAINT_TYPE_TEXTURE = new PaintType(107, 17, "texture");

    public static final PaintType PAINT_TYPE_FLICKER_1 = new PaintType(108, 18, "flicker_1").setTextureUV(4F, 0F);
    public static final PaintType PAINT_TYPE_FLICKER_2 = new PaintType(109, 19, "flicker_2").setTextureUV(5F, 0F);
    // WATER(100), LAVA(101), SKY(102), STAR_FIELD(103), RAINBOW(104),

    public static final PaintType PAINT_TYPE_SKIN = new PaintType(253, 10, true, "skin").setExtraColourType(ExtraColourType.SKIN);
    public static final PaintType PAINT_TYPE_HAIR = new PaintType(254, 11, true, "hair").setExtraColourType(ExtraColourType.HAIR);
    public static final PaintType PAINT_TYPE_EYES = new PaintType(251, 12, true, "eye").setExtraColourType(ExtraColourType.EYE);
    public static final PaintType PAINT_TYPE_MISC_1 = new PaintType(252, 13, true, "misc_1").setExtraColourType(ExtraColourType.MISC_1);
    public static final PaintType PAINT_TYPE_MISC_2 = new PaintType(250, 20, true, "misc_2").setExtraColourType(ExtraColourType.MISC_2);
    public static final PaintType PAINT_TYPE_MISC_3 = new PaintType(249, 21, true, "misc_3").setExtraColourType(ExtraColourType.MISC_3);
    public static final PaintType PAINT_TYPE_MISC_4 = new PaintType(248, 22, true, "misc_4").setExtraColourType(ExtraColourType.MISC_4);

    public static final PaintType PAINT_TYPE_NORMAL = new PaintType(255, 0, "normal");

    private static final PaintType[] PAINT_TYPES = new PaintType[256];
    private static final ArrayList<PaintType> REGISTERED_TYPES = new ArrayList<PaintType>();
    private static int extraChannels = 0;

    public static void init() {
        try {
            registerPaintType(PAINT_TYPE_NORMAL);

            registerPaintType(PAINT_TYPE_DYE_1);
            registerPaintType(PAINT_TYPE_DYE_2);
            registerPaintType(PAINT_TYPE_DYE_3);
            registerPaintType(PAINT_TYPE_DYE_4);
            registerPaintType(PAINT_TYPE_DYE_5);
            registerPaintType(PAINT_TYPE_DYE_6);
            registerPaintType(PAINT_TYPE_DYE_7);
            registerPaintType(PAINT_TYPE_DYE_8);

            registerPaintType(PAINT_TYPE_TEXTURE);

            registerPaintType(PAINT_TYPE_RAINBOW);

            registerPaintType(PAINT_TYPE_PULSE_1);
            registerPaintType(PAINT_TYPE_PULSE_2);
            registerPaintType(PAINT_TYPE_FLICKER_1);
            registerPaintType(PAINT_TYPE_FLICKER_2);

            registerPaintType(PAINT_TYPE_SKIN);
            registerPaintType(PAINT_TYPE_HAIR);
            registerPaintType(PAINT_TYPE_EYES);
            registerPaintType(PAINT_TYPE_MISC_1);
            registerPaintType(PAINT_TYPE_MISC_2);
            registerPaintType(PAINT_TYPE_MISC_3);
            registerPaintType(PAINT_TYPE_MISC_4);

            registerPaintType(PAINT_TYPE_NONE);
        } catch (Exception e) {
            e.printStackTrace();
            FMLCommonHandler.instance().raiseException(e, e.getMessage(), true);
        }

        for (int i = 0; i < PAINT_TYPES.length; i++) {
            if (PAINT_TYPES[i] == null) {
                PAINT_TYPES[i] = PAINT_TYPE_NORMAL;
            }
        }
    }

    private static void registerPaintType(PaintType paintType) throws Exception {
        if (PAINT_TYPES[paintType.getId()] != null) {
            throw new Exception("Paint id " + paintType.getId() + " is already in use.");
        }
        PAINT_TYPES[paintType.getId()] = paintType;
        REGISTERED_TYPES.add(paintType);
        if (paintType.hasAverageColourChannel()) {
            paintType.setColourChannelIndex(extraChannels);
            extraChannels++;
        }
    }

    public static int getExtraChannels() {
        return extraChannels;
    }

    public static PaintType getPaintTypeFromColour(int trgb) {
        int type = 0xFF & (trgb >> 24);
        return getPaintTypeFromIndex(type);
    }

    public static int setPaintTypeOnColour(PaintType paintType, int colour) {
        return BitwiseUtils.setUByteToInt(colour, 0, paintType.getId());
    }

    public static PaintType getPaintTypeFormByte(byte index) {
        return getPaintTypeFromIndex(index & 0xFF);
    }

    public static PaintType getPaintTypeFormName(String name) {
        for (PaintType paintType : PAINT_TYPES) {
            if (paintType.getName().equals(name)) {
                return paintType;
            }
        }
        return PAINT_TYPE_NORMAL;
    }

    public static ArrayList<PaintType> getRegisteredTypes() {
        return REGISTERED_TYPES;
    }

    public static PaintType getPaintTypeFromIndex(int index) {
        return PAINT_TYPES[index];
    }
}
