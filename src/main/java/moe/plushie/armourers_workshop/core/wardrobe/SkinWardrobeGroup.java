package moe.plushie.armourers_workshop.core.wardrobe;

public enum SkinWardrobeGroup {
    SKINS(true),
    OUTFITS(true),
    DYES(true),
    COLORS(false);

    final boolean exchanges;

    SkinWardrobeGroup(boolean exchanges) {
        this.exchanges = exchanges;
    }

    public boolean shouldRenderPlayerInventory() {
        return exchanges;
    }
}
