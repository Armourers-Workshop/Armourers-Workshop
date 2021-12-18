package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.util.ResourceLocation;

public class SkinCore {

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(getModId(), path);
    }

    public static String getModId() {
        return "armourers_workshop";
    }
}
