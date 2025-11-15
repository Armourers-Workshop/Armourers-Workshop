package moe.plushie.armourers_workshop.compat.extensions.com.mojang.blaze3d.systems.RenderSystem;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.18, 1.22)")
@Extension
public class ShaderTextureExt {

    public static void setShaderTexture(@ThisClass Class<?> clazz, int i, OpenResourceLocation texture) {
        RenderSystem.setShaderTexture(i, texture.toLocation());
    }
}
