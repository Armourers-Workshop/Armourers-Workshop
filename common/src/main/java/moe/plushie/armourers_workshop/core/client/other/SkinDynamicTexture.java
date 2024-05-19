package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class SkinDynamicTexture extends DynamicTexture {

    private final TextureManager textureManager;
    private SkinPaintData paintData;
    private NativeImage downloadedImage;

    private ResourceLocation refer;
    private AbstractTexture referTexture;

    private boolean needsUpdate = true;

    public SkinDynamicTexture() {
        super(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, true);
        this.textureManager = Minecraft.getInstance().getTextureManager();
    }

    public ResourceLocation getRefer() {
        return refer;
    }

    public void setRefer(ResourceLocation refer) {
        if (!Objects.equals(this.refer, refer)) {
            this.refer = refer;
            this.referTexture = Optional.ofNullable(refer).map(textureManager::getTexture).orElse(null);
            this.downloadedImage = null;
            this.setNeedsUpdate();
        }
    }

    public SkinPaintData getPaintData() {
        return paintData;
    }

    public void setPaintData(SkinPaintData paintData) {
        if (this.paintData != paintData) {
            this.paintData = paintData;
            this.setNeedsUpdate();
        }
    }

    @Override
    public void upload() {
        NativeImage downloadedImage = getDownloadedImage();
        NativeImage mergedImage = getPixels();
        if (mergedImage == null || downloadedImage == null) {
            return;
        }
        mergedImage.copyFrom(downloadedImage);
        if (paintData != null) {
            applyPaintColor(mergedImage);
        }
        super.upload();
    }

    private void setNeedsUpdate() {
        this.needsUpdate = true;
        RenderSystem.recordRenderCall(() -> {
            if (this.needsUpdate) {
                this.needsUpdate = false;
                this.upload();
            }
        });
    }

    private void applyPaintColor(NativeImage mergedImage) {
        for (int iy = 0; iy < paintData.getHeight(); ++iy) {
            for (int ix = 0; ix < paintData.getWidth(); ++ix) {
                int color = paintData.getColor(ix, iy);
                if (PaintColor.isOpaque(color)) {
                    int r = color >> 16 & 0xff;
                    int g = color >> 8 & 0xff;
                    int b = color & 0xff;
                    int fixed = b << 16 | g << 8 | r;  // ARGB => ABGR
                    mergedImage.setPixelRGBA(ix, iy, 0xff000000 | fixed);
                }
            }
        }
    }

    private NativeImage getDownloadedImage() {
        if (downloadedImage != null) {
            return downloadedImage;
        }
        if (referTexture != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, referTexture.getId());
            downloadedImage = new NativeImage(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, true);
            downloadedImage.downloadTexture(0, false);
        }
        return downloadedImage;
    }

    // TODO: @SAGESSE replace to new impl.
//    @Override
//    protected void finalize() throws Throwable {
//        close();
//        super.finalize();
//    }
}
