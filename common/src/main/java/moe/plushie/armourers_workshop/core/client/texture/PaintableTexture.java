package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.bake.BakedPlayerSkin;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.utils.RenderSystem;

@OnlyIn(Dist.CLIENT)
public class PaintableTexture extends ImageTexture {

    private SkinPaintData paintData;

    private BakedPlayerSkin refer;

    private boolean needsUpdate = true;

    public PaintableTexture(String name) {
        super(name);
        this.resize(SkinPaintData.TEXTURE_WIDTH, SkinPaintData.TEXTURE_HEIGHT);
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

    public void setRefer(BakedPlayerSkin refer) {
        if (!Objects.equals(this.refer, refer)) {
            this.refer = refer;
            this.setNeedsUpdate();
        }
    }

    public BakedPlayerSkin refer() {
        return refer;
    }

    @Override
    public void upload() {
        // it is ready?
        if (refer == null) {
            return;
        }
        var image = refer.body().contents();
        resize(image.getWidth(), image.getHeight());
        fill(image);
        if (paintData != null) {
            applyPaintColor();
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

    private void applyPaintColor() {
        var scale = Math.max(width() / paintData.width(), 1);
        for (var y = 0; y < paintData.height(); ++y) {
            for (var x = 0; x < paintData.width(); ++x) {
                var color = paintData.getColor(x, y);
                if (SkinPaintColor.isOpaque(color)) {
                    fill(x * scale, y * scale, x * scale + scale, y * scale + scale, color);
                }
            }
        }
    }
}
