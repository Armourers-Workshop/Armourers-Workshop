package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class SkinOptions {

    public static SkinOptions DEFAULT = new SkinOptions();

    private int tooltipFlags = 0;
    private int enableEmbeddedItemRenderer = 0;

    public SkinOptions() {
    }

    public SkinOptions(CompoundTag tag) {
        this.tooltipFlags = tag.getInt(Constants.Key.OPTIONS_TOOLTIP_FLAGS);
        this.enableEmbeddedItemRenderer = tag.getInt(Constants.Key.OPTIONS_EMBEDDED_ITEM_RENDERER);
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        DataSerializers.putInt(nbt, Constants.Key.OPTIONS_TOOLTIP_FLAGS, tooltipFlags, 0);
        DataSerializers.putInt(nbt, Constants.Key.OPTIONS_EMBEDDED_ITEM_RENDERER, enableEmbeddedItemRenderer, 0);
        return nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinOptions that)) return false;
        return tooltipFlags == that.tooltipFlags && enableEmbeddedItemRenderer == that.enableEmbeddedItemRenderer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tooltipFlags, enableEmbeddedItemRenderer);
    }

    public int getEmbeddedItemRenderer() {
        return enableEmbeddedItemRenderer;
    }

    public int getTooltipFlags() {
        return tooltipFlags;
    }
}
