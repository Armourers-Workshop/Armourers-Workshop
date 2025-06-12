package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;

public class BlockBenchCubeFace {

    private final int textureId;
    private final int rotation;
    private final OpenRectangle2f rect;

    public BlockBenchCubeFace(int textureId, int rotation, OpenRectangle2f rect) {
        this.textureId = textureId;
        this.rotation = rotation;
        this.rect = rect;
    }

    public int textureId() {
        return textureId;
    }

    public int rotation() {
        return rotation;
    }

    public OpenRectangle2f rect() {
        return rect;
    }

    protected static class Builder {

        private int texture = -1;
        private int rotation = 0;

        private OpenRectangle2f rect = OpenRectangle2f.ZERO;

        public void uv(OpenRectangle2f rect) {
            this.rect = rect;
        }

        public void texture(int texture) {
            this.texture = texture;
        }

        public void rotation(int rotation) {
            this.rotation = rotation;
        }

        public BlockBenchCubeFace build() {
            return new BlockBenchCubeFace(texture, rotation, rect);
        }
    }
}
