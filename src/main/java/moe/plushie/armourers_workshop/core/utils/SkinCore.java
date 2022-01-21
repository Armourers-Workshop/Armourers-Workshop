package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SkinCore {

    public static ResourceLocation TEX_CUBE = resource("textures/armour/cube.png");
    public static ResourceLocation TEX_GUI_PREVIEW = resource("textures/gui/skin-preview.png");

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

}
