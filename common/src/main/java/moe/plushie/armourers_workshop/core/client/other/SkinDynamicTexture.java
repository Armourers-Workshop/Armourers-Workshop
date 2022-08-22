package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;


/**
 * @author RiskyKen
 */
public class SkinDynamicTexture extends DynamicTexture {

    private final TextureManager textureManager;
    private SkinPaintData paintData;
    private NativeImage downloadedImage;
    private ResourceLocation refer;
    private boolean needsUpdate = true;
    private boolean uploaded = false;
    private int changeTotal = 0;


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
            this.downloadedImage = null;
            this.needsUpdate = true;
        }
    }

    public SkinPaintData getPaintData() {
        return paintData;
    }

    public void setPaintData(SkinPaintData paintData) {
        if (this.paintData != paintData) {
            this.paintData = paintData;
            this.needsUpdate = true;
            this.changeTotal = 0;
            if (paintData == null) {
                return;
            }
            for (int value : paintData.getData()) {
                if (PaintColor.isOpaque(value)) {
                    this.changeTotal += 1;
                }
            }
        }
    }

    @Override
    public void upload() {
        needsUpdate = false;
        NativeImage downloadedImage = getDownloadedImage();
        NativeImage mergedImage = getPixels();
        if (mergedImage == null || downloadedImage == null || paintData == null) {
            uploaded = false;
            return;
        }
        mergedImage.copyFrom(downloadedImage);
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
        super.bind();
        mergedImage.upload(0, 0, 0, false);
        uploaded = true;
    }

    @Override
    public void bind() {
        if (changeTotal != 0) {
            if (needsUpdate) {
                upload();
            }
            if (uploaded) {
                super.bind();
                return;
            }
        }
        if (refer != null) {
            RenderSystem.bind(refer);
        }
    }

    private NativeImage getDownloadedImage() {
        if (downloadedImage != null) {
            return downloadedImage;
        }
        if (refer != null) {
            downloadedImage = new NativeImage(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT, true);
            RenderSystem.bind(refer);
            downloadedImage.downloadTexture(0, false);
        }
        return downloadedImage;
    }

    @Override
    protected void finalize() throws Throwable {
        releaseId();
        super.finalize();
    }
}
