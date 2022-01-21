package moe.plushie.armourers_workshop.core.skin.painting;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.SkinDyeType;
import moe.plushie.armourers_workshop.core.utils.SkinLog;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class SkinPaintTypes {

    private static final ISkinPaintType[] ALL_PAINT_MAPPING = new ISkinPaintType[256];
    private static final Map<String, ISkinPaintType> ALL_PAINT_TYPES = new HashMap<>();
    private static int ALL_EXTRA_CHANNELS = 0;

    public static final ISkinPaintType NORMAL = register("normal", 255, 0, false);

    public static final ISkinPaintType DYE_1 = register("dye_1", 1, 1, true).setDyeType(SkinDyeType.DYE_1);
    public static final ISkinPaintType DYE_2 = register("dye_2", 2, 2, true).setDyeType(SkinDyeType.DYE_2);
    public static final ISkinPaintType DYE_3 = register("dye_3", 3, 3, true).setDyeType(SkinDyeType.DYE_3);
    public static final ISkinPaintType DYE_4 = register("dye_4", 4, 4, true).setDyeType(SkinDyeType.DYE_4);
    public static final ISkinPaintType DYE_5 = register("dye_5", 5, 5, true).setDyeType(SkinDyeType.DYE_5);
    public static final ISkinPaintType DYE_6 = register("dye_6", 6, 6, true).setDyeType(SkinDyeType.DYE_6);
    public static final ISkinPaintType DYE_7 = register("dye_7", 7, 7, true).setDyeType(SkinDyeType.DYE_7);
    public static final ISkinPaintType DYE_8 = register("dye_8", 8, 8, true).setDyeType(SkinDyeType.DYE_8);

    public static final ISkinPaintType RAINBOW = register("rainbow", 104, 14, true).setTexture(1F, 0F);

    public static final ISkinPaintType PULSE_1 = register("pulse_1", 105, 15, false).setTexture(2F, 0F);
    public static final ISkinPaintType PULSE_2 = register("pulse_2", 106, 16, false).setTexture(3F, 0F);

    public static final ISkinPaintType TEXTURE = register("texture", 107, 17, false);

    public static final ISkinPaintType FLICKER_1 = register("flicker_1", 108, 18, false).setTexture(4F, 0F);
    public static final ISkinPaintType FLICKER_2 = register("flicker_2", 109, 19, false).setTexture(5F, 0F);

    public static final ISkinPaintType FLASH_1 = register("flash_1", 110, 23, false).setTexture(6F, 0F);
    public static final ISkinPaintType FLASH_2 = register("flash_2", 111, 24, false).setTexture(7F, 0F);

    public static final ISkinPaintType SKIN = register("skin", 253, 10, true).setDyeType(SkinDyeType.SKIN);
    public static final ISkinPaintType HAIR = register("hair", 254, 11, true).setDyeType(SkinDyeType.HAIR);
    public static final ISkinPaintType EYES = register("eye", 251, 12, true).setDyeType(SkinDyeType.EYE);

    public static final ISkinPaintType MISC_1 = register("misc_1", 252, 13, true).setDyeType(SkinDyeType.MISC_1);
    public static final ISkinPaintType MISC_2 = register("misc_2", 250, 20, true).setDyeType(SkinDyeType.MISC_2);
    public static final ISkinPaintType MISC_3 = register("misc_3", 249, 21, true).setDyeType(SkinDyeType.MISC_3);
    public static final ISkinPaintType MISC_4 = register("misc_4", 248, 22, true).setDyeType(SkinDyeType.MISC_4);

    public static final ISkinPaintType NONE = register("none", 0, 9, false);

    public static ISkinPaintType byName(String name) {
        ISkinPaintType paintType = ALL_PAINT_TYPES.get(name);
        if (paintType != null) {
            return paintType;
        }
        return NONE;
    }

    public static ISkinPaintType byId(int index) {
        ISkinPaintType paintType = ALL_PAINT_MAPPING[index & 0xFF];
        if (paintType != null) {
            return paintType;
        }
        return NONE;
    }

    private static SkinPaintType register(String name, int id, int index, boolean hasColourChannel) {
        SkinPaintType paintType = new SkinPaintType(index, id, hasColourChannel);
        paintType.setRegistryName("armourers:" + name);
        if (ALL_PAINT_TYPES.containsKey(paintType.getRegistryName())) {
            SkinLog.warn("A mod tried to register a paint type with an id that is in use.");
            return paintType;
        }
        ALL_PAINT_TYPES.put(paintType.getRegistryName(), paintType);
        ALL_PAINT_MAPPING[paintType.getId() & 0xFF] = paintType;
        if (hasColourChannel) {
            paintType.setChannelIndex(ALL_EXTRA_CHANNELS++);
        }
        SkinLog.debug("Registering Skin Paint '{}'", paintType.getRegistryName());
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
    public static int getTotalExtraChannels() {
        return ALL_EXTRA_CHANNELS;
    }
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
