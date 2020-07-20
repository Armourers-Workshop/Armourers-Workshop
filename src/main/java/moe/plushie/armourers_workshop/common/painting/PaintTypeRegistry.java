package moe.plushie.armourers_workshop.common.painting;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.painting.IPaintTypeRegistry;
import moe.plushie.armourers_workshop.utils.BitwiseUtils;
import moe.plushie.armourers_workshop.utils.ModLogger;

public final class PaintTypeRegistry implements IPaintTypeRegistry {

    public static PaintTypeRegistry getInstance() {
        return ArmourersWorkshop.getProxy().getPaintTypeRegistry();
    }

    public static final PaintType PAINT_TYPE_NORMAL = new PaintType(255, 0, "normal");

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
    
    public static final PaintType PAINT_TYPE_FLASH_1 = new PaintType(110, 23, "flash_1").setTextureUV(6F, 0F);
    public static final PaintType PAINT_TYPE_FLASH_2 = new PaintType(111, 24, "flash_2").setTextureUV(7F, 0F);
    // WATER(100), LAVA(101), SKY(102), STAR_FIELD(103),

    public static final PaintType PAINT_TYPE_SKIN = new PaintType(253, 10, true, "skin").setExtraColourType(ExtraColourType.SKIN);
    public static final PaintType PAINT_TYPE_HAIR = new PaintType(254, 11, true, "hair").setExtraColourType(ExtraColourType.HAIR);
    public static final PaintType PAINT_TYPE_EYES = new PaintType(251, 12, true, "eye").setExtraColourType(ExtraColourType.EYE);

    public static final PaintType PAINT_TYPE_MISC_1 = new PaintType(252, 13, true, "misc_1").setExtraColourType(ExtraColourType.MISC_1);
    public static final PaintType PAINT_TYPE_MISC_2 = new PaintType(250, 20, true, "misc_2").setExtraColourType(ExtraColourType.MISC_2);
    public static final PaintType PAINT_TYPE_MISC_3 = new PaintType(249, 21, true, "misc_3").setExtraColourType(ExtraColourType.MISC_3);
    public static final PaintType PAINT_TYPE_MISC_4 = new PaintType(248, 22, true, "misc_4").setExtraColourType(ExtraColourType.MISC_4);

    public static final PaintType PAINT_TYPE_NONE = new PaintType(0, 9, "none");

    private final IPaintType[] paintTypes = new IPaintType[256];
    private final ArrayList<IPaintType> registeredTypes = new ArrayList<IPaintType>();
    private int extraChannels = 0;

    public PaintTypeRegistry() {
        for (int i = 0; i < paintTypes.length; i++) {
            paintTypes[i] = PAINT_TYPE_NORMAL;
        }
        for (IPaintType paintType : PaintType.PAINT_TYPES) {
            registerPaintType(paintType);
        }
    }

    @Override
    public boolean registerPaintType(IPaintType paintType) {
        if (paintType == null) {
            ModLogger.log(Level.WARN, "A mod tried to register a null paint type.");
            return false;
        }

        if (paintTypes[paintType.getId()] != PAINT_TYPE_NORMAL) {
            ModLogger.log(Level.WARN, "A mod tried to register a paint type with an id that is in use.");
            return false;
        }

        paintTypes[paintType.getId()] = paintType;
        registeredTypes.add(paintType);
        if (paintType.hasAverageColourChannel()) {
            paintType.setColourChannelIndex(extraChannels);
            extraChannels++;
        }
        return true;
    }

    @Override
    public int getExtraChannels() {
        return extraChannels;
    }

    @Override
    public IPaintType getPaintTypeFromColour(int trgb) {
        int type = 0xFF & (trgb >> 24);
        return getPaintTypeFromIndex(type);
    }

    @Override
    public int setPaintTypeOnColour(IPaintType paintType, int colour) {
        return BitwiseUtils.setUByteToInt(colour, 0, paintType.getId());
    }

    @Override
    public IPaintType getPaintTypeFormByte(byte index) {
        return getPaintTypeFromIndex(index & 0xFF);
    }

    @Override
    public IPaintType getPaintTypeFormName(String name) {
        for (IPaintType paintType : paintTypes) {
            if (paintType.getName().equals(name)) {
                return paintType;
            }
        }
        return PAINT_TYPE_NORMAL;
    }

    @Override
    public ArrayList<IPaintType> getRegisteredTypes() {
        return registeredTypes;
    }

    @Override
    public IPaintType getPaintTypeFromIndex(int index) {
        return paintTypes[index];
    }
}
