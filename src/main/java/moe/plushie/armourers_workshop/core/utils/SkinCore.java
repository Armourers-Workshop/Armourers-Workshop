package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.bake.BakedSkinTexture;
import moe.plushie.armourers_workshop.core.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.bake.SkinLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkinCore {

    private static BakedSkinTexture reader = null;

    public final static SkinBakery bakery = new SkinBakery();
    public final static SkinLoader loader = new SkinLoader();

    public static final ResourceLocation TEX_WARDROBE_1 = SkinCore.resource("textures/gui/wardrobe/wardrobe-1.png");
    public static final ResourceLocation TEX_WARDROBE_2 = SkinCore.resource("textures/gui/wardrobe/wardrobe-2.png");

    public static final ResourceLocation TEX_BUTTONS = SkinCore.resource("textures/gui/_controls/buttons.png");

    public static final ResourceLocation TEX_PLAYER_INVENTORY = SkinCore.resource("textures/gui/player_inventory.png");

    public static final ResourceLocation TEX_ITEMS = SkinCore.resource("textures/atlas/items.png");

    public static final ResourceLocation TEX_CUBE = resource("textures/armour/cube.png");
    public static final ResourceLocation TEX_GUI_PREVIEW = resource("textures/gui/skin-preview.png");

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(getModId(), path);
    }

    public static String getModId() {
        return "armourers_workshop";
    }

    public static String getModChannel() {
        return "arms-ws";
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

    @OnlyIn(Dist.CLIENT)
    public static BakedSkinTexture loadBakedSkinTexture(ResourceLocation location) {
        if (location == null) {
            return null;
        }
        if (reader == null) {
            reader = new BakedSkinTexture(location);
        }
        return null;
    }
}
