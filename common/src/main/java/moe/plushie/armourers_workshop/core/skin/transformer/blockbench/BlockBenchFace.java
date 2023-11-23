package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.utils.math.Rectangle2f;

public class BlockBenchFace {

    private final int textureId;
    private final Rectangle2f rect;

    public BlockBenchFace(int textureId, Rectangle2f rect) {
        this.textureId = textureId;
        this.rect = rect;
    }

    public int getTextureId() {
        return textureId;
    }

    public Rectangle2f getRect() {
        return rect;
    }

    public static class Builder {

        private int texture = -1;
        private Rectangle2f rect = Rectangle2f.ZERO;

        public void uv(Rectangle2f rect) {
            this.rect = rect;
        }

        public void texture(int texture) {
            this.texture = texture;
        }

        public BlockBenchFace build() {
            return new BlockBenchFace(texture, rect);
        }
    }

}
