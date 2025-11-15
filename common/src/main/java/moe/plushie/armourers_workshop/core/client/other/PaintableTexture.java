package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.compat.client.AbstractDynamicTexture;
import moe.plushie.armourers_workshop.compat.client.AbstractNativeImage;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureModel;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenNativeImage;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;

public class PaintableTexture extends AbstractDynamicTexture {

    private final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

    private SkinPaintData paintData;
    private AbstractNativeImage downloadedImage;

    private OpenResourceLocation refer;
    private AbstractTexture referTexture;

    private boolean needsUpdate = true;

    public PaintableTexture(String name) {
        super(name, EntityTextureModel.TEXTURE_WIDTH, EntityTextureModel.TEXTURE_HEIGHT, true);
    }

    public void setRefer(OpenResourceLocation refer) {
        if (!Objects.equals(this.refer, refer)) {
            this.refer = refer;
            this.referTexture = Objects.flatMap(refer, it -> textureManager.getTexture(it.toLocation()));
            this.downloadedImage = null;
            this.setNeedsUpdate();
        }
    }

    public OpenResourceLocation refer() {
        return refer;
    }

    public void setPaintData(SkinPaintData paintData) {
        if (this.paintData != paintData) {
            this.paintData = paintData;
            this.setNeedsUpdate();
        }
    }

    public SkinPaintData paintData() {
        return paintData;
    }

    @Override
    public void upload() {
        var downloadedImage = downloadedImage();
        var mergedImage = AbstractNativeImage.of(getPixels());
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

    private void applyPaintColor(OpenNativeImage mergedImage) {
        for (var iy = 0; iy < paintData.height(); ++iy) {
            for (var ix = 0; ix < paintData.width(); ++ix) {
                var color = paintData.getColor(ix, iy);
                if (SkinPaintColor.isOpaque(color)) {
                    mergedImage.setPixel(ix, iy, color);
                }
            }
        }
    }

    private AbstractNativeImage downloadedImage() {
        if (downloadedImage != null) {
            return downloadedImage;
        }
        if (referTexture != null) {
            downloadedImage = AbstractNativeImage.fromTexture(referTexture, EntityTextureModel.TEXTURE_WIDTH, EntityTextureModel.TEXTURE_HEIGHT);
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
