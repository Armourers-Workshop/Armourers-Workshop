package moe.plushie.armourers_workshop.compat.extensions.com.mojang.blaze3d.platform.NativeImage;

import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.lwjgl.opengl.GL11;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.22)")
@Extension
public class ABI {

    public static void downloadFromTexture(@This NativeImage image, AbstractTexture texture) {
        var textureId = texture.getId();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        image.downloadTexture(0, false);
    }
}
