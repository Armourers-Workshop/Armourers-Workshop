package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.skin.SkinOptions;
import moe.plushie.armourers_workshop.init.ModConfig;

import java.util.function.BooleanSupplier;

public enum SkinTooltipFlags {

    NAME(0x01, () -> ModConfig.Client.tooltipSkinName),
    AUTHOR(0x02, () -> ModConfig.Client.tooltipSkinAuthor),
    TYPE(0x04, () -> ModConfig.Client.tooltipSkinType),
    FLAVOUR(0x08, () -> ModConfig.Client.tooltipFlavour),

    HAS_SKIN(0x10, () -> ModConfig.Client.tooltipHasSkin),
    OPEN_WARDROBE(0x20, () -> ModConfig.Client.tooltipHasSkin),

    PREVIEW(0x80, () -> ModConfig.Client.skinPreEnabled);

    private final int flags;
    private final BooleanSupplier supplier;

    SkinTooltipFlags(int flags, BooleanSupplier supplier) {
        this.flags = flags;
        this.supplier = supplier;
    }

    public boolean isEnabled(SkinOptions options) {
        // the server disabled this feature.
        if ((options.getTooltipFlags() & flags) != 0) {
            return false;
        }
        return supplier.getAsBoolean();
    }
}
