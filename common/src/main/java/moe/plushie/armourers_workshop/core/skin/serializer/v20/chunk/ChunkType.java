package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

public enum ChunkType {
    SKIN("SKIN"),
    CUBE_DATA("CCBO"),
    PAINT_DATA("PADT"),
    PREVIEW_DATA("VCBO"),
    PALETTE("PALE"),
    PROPERTIES("PPTS"),
    SKIN_PART("SKPR"),
    SKIN_PART_NAME("PRNM"),
    MARKER("PRMK"),
    SKIN_SETTINGS("SET3");

    private final String name;

    ChunkType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

