package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;
import java.util.function.BooleanSupplier;

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
        if (!(o instanceof SkinOptions)) return false;
        SkinOptions that = (SkinOptions) o;
        return tooltipFlags == that.tooltipFlags && enableEmbeddedItemRenderer == that.enableEmbeddedItemRenderer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tooltipFlags, enableEmbeddedItemRenderer);
    }

    public boolean contains(TooltipFlags flags) {
        // the server disabled this feature.
        if ((tooltipFlags & flags.flags) != 0) {
            return false;
        }
        return flags.supplier.getAsBoolean();
    }

    public int getEmbeddedItemRenderer() {
        return enableEmbeddedItemRenderer;
    }

    public enum TooltipFlags {

        NAME(0x01, () -> ModConfig.Client.tooltipSkinName),
        AUTHOR(0x02, () -> ModConfig.Client.tooltipSkinAuthor),
        TYPE(0x04, () -> ModConfig.Client.tooltipSkinType),
        FLAVOUR(0x08, () -> ModConfig.Client.tooltipFlavour),

        HAS_SKIN(0x10, () -> ModConfig.Client.tooltipHasSkin),
        OPEN_WARDROBE(0x20, () -> ModConfig.Client.tooltipHasSkin),

        PREVIEW(0x80, () -> ModConfig.Client.skinPreEnabled);

        private final int flags;
        private final BooleanSupplier supplier;

        TooltipFlags(int flags, BooleanSupplier supplier) {
            this.flags = flags;
            this.supplier = supplier;
        }
    }
}
