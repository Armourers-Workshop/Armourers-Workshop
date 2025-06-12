package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

public class BlockBenchObject {

    protected final String name;
    protected final String uuid;

    public BlockBenchObject(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }


    public String name() {
        return name;
    }

    public String uuid() {
        return uuid;
    }

    protected static class Builder {

        protected String name = "";
        protected String uuid = "";

        public void name(String name) {
            this.name = name;
        }

        public void uuid(String uuid) {
            this.uuid = uuid;
        }
    }
}
