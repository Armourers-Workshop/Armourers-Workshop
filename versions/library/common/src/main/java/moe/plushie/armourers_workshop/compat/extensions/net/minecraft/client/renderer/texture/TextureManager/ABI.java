package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.client.renderer.texture.TextureManager;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.20, 1.22)")
@Extension
public class ABI {

    public static void register(@This TextureManager textureManager, OpenResourceLocation location, AbstractTexture texture) {
        textureManager.register(location.toLocation(), texture);
    }

    public static void release(@This TextureManager textureManager, OpenResourceLocation location) {
        textureManager.release(location.toLocation());
    }
}
