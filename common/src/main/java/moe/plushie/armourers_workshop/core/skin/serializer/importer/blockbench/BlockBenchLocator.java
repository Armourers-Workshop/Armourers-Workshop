package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class BlockBenchLocator extends BlockBenchElement {

    private final OpenVector3f rotation;
    private final OpenVector3f position;

    public BlockBenchLocator(String uuid, String name, String type, boolean allowExport, OpenVector3f rotation, OpenVector3f position) {
        super(uuid, name, type, allowExport);
        this.rotation = rotation;
        this.position = position;
    }

    public OpenVector3f rotation() {
        return rotation;
    }

    public OpenVector3f position() {
        return position;
    }

    protected static class Builder extends BlockBenchElement.Builder {

        protected OpenVector3f rotation = OpenVector3f.ZERO;
        protected OpenVector3f position = OpenVector3f.ZERO;

        public void rotation(OpenVector3f rotation) {
            this.rotation = rotation;
        }

        public void position(OpenVector3f position) {
            this.position = position;
        }

        @Override
        public BlockBenchLocator build() {
            return new BlockBenchLocator(uuid, name, type, allowExport, rotation, position);
        }
    }
}
