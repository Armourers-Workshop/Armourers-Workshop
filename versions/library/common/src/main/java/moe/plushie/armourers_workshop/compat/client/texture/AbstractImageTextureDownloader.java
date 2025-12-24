package moe.plushie.armourers_workshop.compat.client.texture;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.core.client.texture.PlayerSkinDecoder;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;

@Available("[1.16, 1.26)")
@OnlyIn(Dist.CLIENT)
public class AbstractImageTextureDownloader {

    public void download(OpenResourceLocation location, IResultHandler<BufferedImage> handler) {
        RenderSystem.safeCall(() -> {
            try {
                var texture = Minecraft.getInstance().getTextureManager().getTexture(location.get(), null);
                if (texture == null) {
                    throw new IllegalAccessException("can't found texture " + location);
                }
                texture.bind();
                var image = downloadTexture();
                handler.accept(image);
            } catch (Throwable e) {
                handler.abort(e);
            }
        });
    }

    private BufferedImage downloadTexture() {
        var width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        var height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        var buffer = BufferUtils.createByteBuffer(width * height * 4);

        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        var decoder = PlayerSkinDecoder.getInstance();
        return decoder.decode(width, height, buffer);
    }
}
