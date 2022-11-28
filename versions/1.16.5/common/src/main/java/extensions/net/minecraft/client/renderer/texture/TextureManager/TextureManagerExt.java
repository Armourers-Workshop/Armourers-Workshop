package extensions.net.minecraft.client.renderer.texture.TextureManager;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@Extension
public class TextureManagerExt {

    @Nullable
    public static AbstractTexture getTexture(@This TextureManager textureManager, ResourceLocation resourceLocation, Object arg2) {
        return textureManager.getTexture(resourceLocation);
    }
}
