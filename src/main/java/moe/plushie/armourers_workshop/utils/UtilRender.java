package moe.plushie.armourers_workshop.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public final class UtilRender {
    
    public static void bindTexture(ResourceLocation resourceLocation) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
    }
}
