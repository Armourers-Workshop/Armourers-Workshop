package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class BlockBenchDisplay {

    private final OpenVector3f translation;
    private final OpenVector3f rotation;
    private final OpenVector3f scale;

    public BlockBenchDisplay(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public OpenVector3f translation() {
        return translation;
    }

    public OpenVector3f rotation() {
        return rotation;
    }

    public OpenVector3f scale() {
        return scale;
    }

    protected static class Builder {

        private OpenVector3f translation = OpenVector3f.ZERO;
        private OpenVector3f rotation = OpenVector3f.ZERO;
        private OpenVector3f scale = OpenVector3f.ONE;

        public void translation(OpenVector3f translation) {
            this.translation = translation;
        }

        public void rotation(OpenVector3f rotation) {
            this.rotation = rotation;
        }

        public void scale(OpenVector3f scale) {
            this.scale = scale;
        }

        public BlockBenchDisplay build() {
            return new BlockBenchDisplay(translation, rotation, scale);
        }
    }
}
