package moe.plushie.armourers_workshop.common.lib;

public enum EnumGuiId {

    COLOUR_MIXER,
    ARMOURER,
    GUIDE_BOOK(false),
    TOOL_OPTIONS(false),
    ARMOUR_LIBRARY,
    WARDROBE_PLAYER(false),
    MANNEQUIN,
    MINI_ARMOURER(false),
    WARDROBE_ENTITY(false),
    SKNNING_TABLE,
    DYE_TABLE,
    DEBUG_TOOL(false),
    GLOBAL_SKIN_LIBRARY,
    ADMIN_PANEL(false),
    SKINNABLE,
    HOLOGRAM_PROJECTOR,
    OUTFIT_MAKER,
    ADVANCED_SKIN_BUILDER;

    private final boolean tile;

    private EnumGuiId() {
        this(true);
    }

    private EnumGuiId(boolean tile) {
        this.tile = tile;
    }

    public boolean isTile() {
        return tile;
    }
}
