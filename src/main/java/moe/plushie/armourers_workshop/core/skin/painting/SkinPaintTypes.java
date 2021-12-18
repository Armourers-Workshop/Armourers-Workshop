package moe.plushie.armourers_workshop.core.skin.painting;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.common.IExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintType;
import moe.plushie.armourers_workshop.core.utils.SkinLog;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class SkinPaintTypes {

    private static final ISkinPaintType[] ALL_PAINT_MAPPING = new ISkinPaintType[256];
    private static final Map<String, ISkinPaintType> ALL_PAINT_TYPES = new HashMap<>();

    public static final ISkinPaintType NORMAL = register("normal", 0, 255, false);

    public static final ISkinPaintType DYE_1 = register("dye_1", 1, 1, true);
    public static final ISkinPaintType DYE_2 = register("dye_2", 2, 2, true);
    public static final ISkinPaintType DYE_3 = register("dye_3", 3, 3, true);
    public static final ISkinPaintType DYE_4 = register("dye_4", 4, 4, true);
    public static final ISkinPaintType DYE_5 = register("dye_5", 5, 5, true);
    public static final ISkinPaintType DYE_6 = register("dye_6", 6, 6, true);
    public static final ISkinPaintType DYE_7 = register("dye_7", 7, 7, true);
    public static final ISkinPaintType DYE_8 = register("dye_8", 8, 8, true);

    public static final ISkinPaintType RAINBOW = register("rainbow", 14, 104, true).setTextureUV(1F, 0F);

    public static final ISkinPaintType PULSE_1 = register("pulse_1", 15, 105, false).setTextureUV(2F, 0F);
    public static final ISkinPaintType PULSE_2 = register("pulse_2", 16, 106, false).setTextureUV(3F, 0F);

    public static final ISkinPaintType TEXTURE = register("texture", 17, 107, false);

    public static final ISkinPaintType FLICKER_1 = register("flicker_1", 18, 108, false).setTextureUV(4F, 0F);
    public static final ISkinPaintType FLICKER_2 = register("flicker_2", 19, 109, false).setTextureUV(5F, 0F);

    public static final ISkinPaintType FLASH_1 = register("flash_1", 23, 110, false).setTextureUV(6F, 0F);
    public static final ISkinPaintType FLASH_2 = register("flash_2", 24, 111, false).setTextureUV(7F, 0F);

    public static final ISkinPaintType SKIN = register("skin", 10, 253, true).setExtraColourType(ExtraColourType.SKIN);
    public static final ISkinPaintType HAIR = register("hair", 11, 254, true).setExtraColourType(ExtraColourType.HAIR);
    public static final ISkinPaintType EYES = register("eye", 12, 251, true).setExtraColourType(ExtraColourType.EYE);

    public static final ISkinPaintType MISC_1 = register("misc_1", 13, 252, true).setExtraColourType(ExtraColourType.MISC_1);
    public static final ISkinPaintType MISC_2 = register("misc_2", 20, 250, true).setExtraColourType(ExtraColourType.MISC_2);
    public static final ISkinPaintType MISC_3 = register("misc_3", 21, 249, true).setExtraColourType(ExtraColourType.MISC_3);
    public static final ISkinPaintType MISC_4 = register("misc_4", 22, 248, true).setExtraColourType(ExtraColourType.MISC_4);

    public static final ISkinPaintType NONE = register("none", 9, 0, false);

//    private int extraChannels = 0;

    public static ISkinPaintType byName(String name) {
        ISkinPaintType paintType = ALL_PAINT_TYPES.get(name);
        if (paintType != null) {
            return paintType;
        }
        return NORMAL;
    }

    public static ISkinPaintType byId(int index) {
        ISkinPaintType paintType = ALL_PAINT_MAPPING[index & 0xFF];
        if (paintType != null) {
            return paintType;
        }
        return NORMAL;
    }

    private static SkinPaintType register(String name, int index, int id, boolean hasColourChannel) {
        SkinPaintType paintType = new SkinPaintType(index, id, hasColourChannel);
        paintType.setRegistryName("armourers:" + name);
        if (ALL_PAINT_TYPES.containsKey(paintType.getRegistryName())) {
            SkinLog.warn("A mod tried to register a paint type with an id that is in use.");
            return paintType;
        }
        ALL_PAINT_TYPES.put(paintType.getRegistryName(), paintType);
        ALL_PAINT_MAPPING[paintType.getId() & 0xFF] = paintType;
        SkinLog.info("Registering Skin Paint '{}'", paintType.getRegistryName());
        return paintType;
    }

//    public SkinSkinPaintTypes() {
//        for (int i = 0; i < paintTypes.length; i++) {
//            paintTypes[i] = PAINT_TYPE_NORMAL;
//        }
//        for (ISkinSkinPaintType paintType : SkinPaintType.PAINT_TYPES) {
//            registerSkinPaintType(paintType);
//        }
//    }
//
//    @Override
//    public boolean registerSkinPaintType(ISkinSkinPaintType paintType) {
//        if (paintType == null) {
//            SkinLog.warn("A mod tried to register a null paint type.");
//            return false;
//        }
//
//        if (paintTypes[paintType.getId()] != PAINT_TYPE_NORMAL) {
//            SkinLog.warn("A mod tried to register a paint type with an id that is in use.");
//            return false;
//        }
//
//        paintTypes[paintType.getId()] = paintType;
//        registeredTypes.add(paintType);
//        if (paintType.hasAverageColourChannel()) {
//            paintType.setColourChannelIndex(extraChannels);
//            extraChannels++;
//        }
//        return true;
//    }
//
//    @Override
//    public int getExtraChannels() {
//        return extraChannels;
//    }
//
//    @Override
//    public ISkinSkinPaintType getSkinPaintTypeFromColour(int trgb) {
//        int type = 0xFF & (trgb >> 24);
//        return getSkinPaintTypeFromIndex(type);
//    }
//
//    @Override
//    public int setSkinPaintTypeOnColour(ISkinSkinPaintType paintType, int colour) {
//        return BitwiseUtils.setUByteToInt(colour, 0, paintType.getId());
//    }
////
//    @Override
//    public ISkinSkinPaintType getSkinPaintTypeFormName(String name) {
//        for (ISkinSkinPaintType paintType : paintTypes) {
//            if (paintType.getName().equals(name)) {
//                return paintType;
//            }
//        }
//        return PAINT_TYPE_NORMAL;
//    }
//
//    @Override
//    public ArrayList<ISkinSkinPaintType> getRegisteredTypes() {
//        return registeredTypes;
//    }
//
//    @Override
//    public ISkinSkinPaintType getSkinPaintTypeFromIndex(int index) {
//        return paintTypes[index];
//    }
}
