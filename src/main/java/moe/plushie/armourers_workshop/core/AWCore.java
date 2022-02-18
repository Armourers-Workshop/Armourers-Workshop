package moe.plushie.armourers_workshop.core;

import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.render.bake.SkinLoader;
import net.minecraft.util.ResourceLocation;

public class AWCore {

    public final static SkinBakery bakery = new SkinBakery();
    public final static SkinLoader loader = new SkinLoader();

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(getModId(), path);
    }

    public static String getModId() {
        return "armourers_workshop";
    }

    public static void init() {

    }


//    public static Skin loadSkin(String identifier) {
//        BakedSkin skin = loadBakedSkin(identifier);
//        if (skin != null) {
//            return skin.getSkin();
//        }
//        return null;
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public static BakedSkin loadBakedSkin(String identifier) {
//        if (identifier.isEmpty()) {
//            return null;
//        }
//        int iq = Integer.parseInt(identifier);
//        if (iq < skins.size()) {
//            return skins.get(iq);
//        }
//        return null;
//    }

}
