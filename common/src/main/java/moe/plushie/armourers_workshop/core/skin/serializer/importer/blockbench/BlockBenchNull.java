package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class BlockBenchNull extends BlockBenchElement {

    private final OpenVector3f position;

    public BlockBenchNull(String uuid, String name, String type, boolean allowExport, OpenVector3f position) {
        super(uuid, name, type, allowExport);
        this.position = position;
    }

    public OpenVector3f position() {
        return position;
    }

    protected static class Builder extends BlockBenchElement.Builder {

        protected OpenVector3f position = OpenVector3f.ZERO;

        public void position(OpenVector3f position) {
            this.position = position;
        }

        @Override
        public BlockBenchNull build() {
            return new BlockBenchNull(uuid, name, type, allowExport, position);
        }
    }
}
