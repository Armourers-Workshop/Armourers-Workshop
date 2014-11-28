package riskyken.armourersWorkshop.common.abstraction;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public final class RenderAbstraction {
    
    public static Tessellator getTessellator() {
        return Tessellator.instance;
    }
    
    public static TextureManager getTextureManager() {
        return CommonAbstraction.getMinecraft().renderEngine;
    }
    
    public static void bindTexture(ResourceLocation resourceLocation) {
        getTextureManager().bindTexture(resourceLocation);
    }
}
