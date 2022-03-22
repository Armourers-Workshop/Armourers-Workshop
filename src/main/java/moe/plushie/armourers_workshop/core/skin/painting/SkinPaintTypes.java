package moe.plushie.armourers_workshop.core.skin.painting;

import moe.plushie.armourers_workshop.core.skin.data.SkinDyeType;
import moe.plushie.armourers_workshop.core.utils.AWLog;
import moe.plushie.armourers_workshop.core.utils.SkinResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class SkinPaintTypes {

    private static final ArrayList<SkinPaintType> ALL_SORTED_TYPES = new ArrayList<>();
    private static final SkinPaintType[] ALL_PAINT_MAPPING = new SkinPaintType[256];
    private static final Map<String, SkinPaintType> ALL_PAINT_TYPES = new HashMap<>();

    public static final SkinPaintType NORMAL = register("normal", 255, 0);

    public static final SkinPaintType DYE_1 = register("dye_1", 1, 1).setDyeType(SkinDyeType.DYE_1);
    public static final SkinPaintType DYE_2 = register("dye_2", 2, 2).setDyeType(SkinDyeType.DYE_2);
    public static final SkinPaintType DYE_3 = register("dye_3", 3, 3).setDyeType(SkinDyeType.DYE_3);
    public static final SkinPaintType DYE_4 = register("dye_4", 4, 4).setDyeType(SkinDyeType.DYE_4);
    public static final SkinPaintType DYE_5 = register("dye_5", 5, 5).setDyeType(SkinDyeType.DYE_5);
    public static final SkinPaintType DYE_6 = register("dye_6", 6, 6).setDyeType(SkinDyeType.DYE_6);
    public static final SkinPaintType DYE_7 = register("dye_7", 7, 7).setDyeType(SkinDyeType.DYE_7);
    public static final SkinPaintType DYE_8 = register("dye_8", 8, 8).setDyeType(SkinDyeType.DYE_8);

    public static final SkinPaintType RAINBOW = register("rainbow", 104, 14).setTexture(1F, 0F);

    public static final SkinPaintType PULSE_1 = register("pulse_1", 105, 15).setTexture(2F, 0F);
    public static final SkinPaintType PULSE_2 = register("pulse_2", 106, 16).setTexture(3F, 0F);

    public static final SkinPaintType TEXTURE = register("texture", 107, 17);

    public static final SkinPaintType FLICKER_1 = register("flicker_1", 108, 18).setTexture(4F, 0F);
    public static final SkinPaintType FLICKER_2 = register("flicker_2", 109, 19).setTexture(5F, 0F);

    public static final SkinPaintType FLASH_1 = register("flash_1", 110, 23).setTexture(6F, 0F);
    public static final SkinPaintType FLASH_2 = register("flash_2", 111, 24).setTexture(7F, 0F);

    public static final SkinPaintType SKIN = register("skin", 253, 10).setDyeType(SkinDyeType.SKIN);
    public static final SkinPaintType HAIR = register("hair", 254, 11).setDyeType(SkinDyeType.HAIR);
    public static final SkinPaintType EYES = register("eye", 251, 12).setDyeType(SkinDyeType.EYE);

    public static final SkinPaintType MISC_1 = register("misc_1", 252, 13).setDyeType(SkinDyeType.MISC_1);
    public static final SkinPaintType MISC_2 = register("misc_2", 250, 20).setDyeType(SkinDyeType.MISC_2);
    public static final SkinPaintType MISC_3 = register("misc_3", 249, 21).setDyeType(SkinDyeType.MISC_3);
    public static final SkinPaintType MISC_4 = register("misc_4", 248, 22).setDyeType(SkinDyeType.MISC_4);

    public static final SkinPaintType NONE = register("none", 0, 9);

    public static SkinPaintType byName(String registryName) {
        return ALL_PAINT_TYPES.getOrDefault(registryName, NONE);
    }

    public static SkinPaintType byId(int index) {
        SkinPaintType paintType = ALL_PAINT_MAPPING[index & 0xff];
        if (paintType != null) {
            return paintType;
        }
        return NONE;
    }

    private static SkinPaintType register(String name, int id, int index) {
        SkinPaintType paintType = new SkinPaintType(index, id);
        paintType.setRegistryName(new SkinResourceLocation("armourers", name));
        if (ALL_PAINT_TYPES.containsKey(paintType.getRegistryName().toString())) {
            AWLog.warn("A mod tried to register a paint type with an id that is in use.");
            return paintType;
        }
        ALL_SORTED_TYPES.add(paintType);
        ALL_PAINT_TYPES.put(paintType.getRegistryName().toString(), paintType);
        ALL_PAINT_MAPPING[paintType.getId() & 0xff] = paintType;
        AWLog.debug("Registering Skin Paint '{}'", paintType.getRegistryName());
        return paintType;
    }

    public static Collection<SkinPaintType> values() {
        return ALL_SORTED_TYPES;
    }
}
