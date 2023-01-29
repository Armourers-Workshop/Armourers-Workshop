package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

public enum ChunkType {
    SKIN("SKIN"),
    PAINT_DATA("TPDT"),
    PROPERTIES("PPTS"),
    CUBE("PRCB"),
    MARKER("PRMK"),
    TRANSFORM("PRTF"),
    PALETTE("PALE"),
    SKIN_PART("SKPR"),
    SKIN_PART_NAME("PRNM"),
    SKIN_PART_PARENT("PRPP");

    private final String name;

    ChunkType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

