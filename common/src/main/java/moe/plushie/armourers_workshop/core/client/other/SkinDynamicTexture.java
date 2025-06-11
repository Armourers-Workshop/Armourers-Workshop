package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureModel;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenNativeImage;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class SkinDynamicTexture extends DynamicTexture {

    private final TextureManager textureManager;
    private SkinPaintData paintData;
    private OpenNativeImage downloadedImage;

    private OpenResourceLocation refer;
    private AbstractTexture referTexture;

    private boolean needsUpdate = true;

    public SkinDynamicTexture() {
        super(EntityTextureModel.TEXTURE_WIDTH, EntityTextureModel.TEXTURE_HEIGHT, true);
        this.textureManager = Minecraft.getInstance().getTextureManager();
    }

    public OpenResourceLocation getRefer() {
        return refer;
    }

    public void setRefer(OpenResourceLocation refer) {
        if (!Objects.equals(this.refer, refer)) {
            this.refer = refer;
            this.referTexture = Objects.flatMap(refer, it -> textureManager.getTexture(it.toLocation()));
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
        var downloadedImage = getDownloadedImage();
        var mergedImage = OpenNativeImage.of(getPixels());
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

    private OpenNativeImage getDownloadedImage() {
        if (downloadedImage != null) {
            return downloadedImage;
        }
        if (referTexture != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, referTexture.getId());
            downloadedImage = OpenNativeImage.of(0, EntityTextureModel.TEXTURE_WIDTH, EntityTextureModel.TEXTURE_HEIGHT);
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
