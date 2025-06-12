package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

public class BlockBenchElement extends BlockBenchObject {

    private final String type;

    private final boolean allowExport;

    public BlockBenchElement(String uuid, String name, String type, boolean allowExport) {
        super(uuid, name);
        this.type = type;
        this.allowExport = allowExport;
    }

    public String type() {
        return type;
    }

    public boolean allowExport() {
        return allowExport;
    }

    protected static class Builder extends BlockBenchObject.Builder {

        protected String type = "cube";

        protected boolean allowExport = true;

        public void type(String type) {
            this.type = type;
        }

        public void export(boolean allowExport) {
            this.allowExport = allowExport;
        }

        public BlockBenchElement build() {
            return new BlockBenchElement(uuid, name, type, allowExport);
        }
    }
}
