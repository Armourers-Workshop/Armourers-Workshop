package moe.plushie.armourers_workshop.compat.extensions.com.mojang.blaze3d.systems.RenderSystem;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.utils.RenderSystem;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[18, 26)")
@Extension
public class ShaderTextureExt {

    public static void setShaderTexture(@ThisClass Class<?> clazz, int i, OpenResourceKey texture) {
        RenderSystem.setShaderTexture(i, texture.get());
    }
}
