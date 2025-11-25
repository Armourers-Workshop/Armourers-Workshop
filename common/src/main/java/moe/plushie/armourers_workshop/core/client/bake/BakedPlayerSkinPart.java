package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;

import java.awt.image.BufferedImage;

public class BakedPlayerSkinPart {

    private final int id = OpenRandomSource.nextInt(BakedPlayerSkinPart.class);

    private final int width;
    private final int height;
    private final int scale;

    private final BufferedImage contents;

    public BakedPlayerSkinPart(BufferedImage contents) {
        this.width = contents.getWidth();
        this.height = contents.getHeight();
        this.scale = Math.max(width / 64, 1); // 1 = 64 / 64, 16 = 1024 / 64
        this.contents = contents;
    }

    public SkinPaintColor getColor(int u, int v) {
        // apply the content scale.
        u *= scale;
        v *= scale;
        // is over size?
        if (u < 0 || u >= width || v < 0 || v > height) {
            return SkinPaintColor.CLEAR;
        }
        var color = contents.getRGB(u, v);
        return SkinPaintColor.of(color, SkinPaintTypes.NORMAL);
    }

    public SkinPaintColor getColor(OpenVector2i texturePos) {
        return getColor(texturePos.x, texturePos.y);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int scale() {
        return scale;
    }

    public BufferedImage contents() {
        return contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BakedPlayerSkinPart that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
