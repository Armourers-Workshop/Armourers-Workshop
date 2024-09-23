package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.ItemStackStorage;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public class SkinDescriptor implements ISkinDescriptor {

    public static final SkinDescriptor EMPTY = new SkinDescriptor("");

    private final String identifier;
    private final ISkinType type;
    private final Options options;
    private final ColorScheme colorScheme;

    // not a required property, but it can help we reduce memory usage and improve performance.
    private ItemStack skinItemStack;

    public SkinDescriptor(String identifier) {
        this(identifier, SkinTypes.UNKNOWN, Options.DEFAULT, ColorScheme.EMPTY);
    }

    public SkinDescriptor(String identifier, ISkinType type) {
        this(identifier, type, Options.DEFAULT, ColorScheme.EMPTY);
    }

    public SkinDescriptor(String identifier, ISkinType type, ColorScheme colorScheme) {
        this(identifier, type, Options.DEFAULT, colorScheme);
    }

    public SkinDescriptor(String identifier, ISkinType type, Options options, ColorScheme colorScheme) {
        this.identifier = identifier;
        this.type = type;
        this.options = options;
        this.colorScheme = colorScheme;
    }

    public SkinDescriptor(SkinDescriptor descriptor, ColorScheme colorScheme) {
        this(descriptor.getIdentifier(), descriptor.getType(), descriptor.getOptions(), colorScheme);
    }

    public SkinDescriptor(CompoundTag tag) {
        this.identifier = tag.getString(Constants.Key.SKIN_IDENTIFIER);
        this.type = SkinTypes.byName(tag.getString(Constants.Key.SKIN_TYPE));
        this.options = tag.getOptionalSkinOptions(Constants.Key.SKIN_OPTIONS, Options.DEFAULT);
        this.colorScheme = tag.getOptionalColorScheme(Constants.Key.SKIN_DYE, ColorScheme.EMPTY);
    }

    public static SkinDescriptor of(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return EMPTY;
        }
        ItemStackStorage storage = ItemStackStorage.of(itemStack);
        SkinDescriptor descriptor = storage.skinDescriptor;
        if (descriptor != null) {
            return descriptor;
        }
        descriptor = itemStack.getOrDefault(ModDataComponents.SKIN.get(), EMPTY);
        storage.skinDescriptor = descriptor;
        return descriptor;
    }

    public boolean accept(ItemStack itemStack) {
        if (itemStack.isEmpty() || isEmpty()) {
            return false;
        }
        ISkinType skinType = getType();
        if (skinType == SkinTypes.ITEM) {
            return true;
        }
        if (skinType instanceof ISkinToolType) {
            return ((ISkinToolType) skinType).contains(itemStack);
        }
        return false;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (isEmpty()) {
            return nbt;
        }
        nbt.putString(Constants.Key.SKIN_TYPE, type.getRegistryName().toString());
        nbt.putString(Constants.Key.SKIN_IDENTIFIER, identifier);
        nbt.putOptionalSkinOptions(Constants.Key.SKIN_OPTIONS, options, Options.DEFAULT);
        nbt.putOptionalColorScheme(Constants.Key.SKIN_DYE, colorScheme, ColorScheme.EMPTY);
        return nbt;
    }

    public ItemStack sharedItemStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (skinItemStack != null) {
            return skinItemStack;
        }
        ItemStack itemStack = new ItemStack(ModItems.SKIN.get());
        itemStack.set(ModDataComponents.SKIN.get(), this);
        skinItemStack = itemStack;
        return itemStack;
    }

    public ItemStack asItemStack() {
        return sharedItemStack().copy();
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public ISkinType getType() {
        return type;
    }

    public Options getOptions() {
        return options;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return String.format("%s@%s[%s]", identifier, type.getRegistryName().getPath(), type.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinDescriptor that)) return false;
        return identifier.equals(that.identifier) && colorScheme.equals(that.colorScheme);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }


    public static class Options {

        public static Options DEFAULT = new Options();

        private int tooltipFlags = 0;
        private int enableEmbeddedItemRenderer = 0;

        public Options() {
        }

        public Options(CompoundTag tag) {
            this.tooltipFlags = tag.getInt(Constants.Key.OPTIONS_TOOLTIP_FLAGS);
            this.enableEmbeddedItemRenderer = tag.getInt(Constants.Key.OPTIONS_EMBEDDED_ITEM_RENDERER);
        }

        public CompoundTag serializeNBT() {
            var tag = new CompoundTag();
            tag.putOptionalInt(Constants.Key.OPTIONS_TOOLTIP_FLAGS, tooltipFlags, 0);
            tag.putOptionalInt(Constants.Key.OPTIONS_EMBEDDED_ITEM_RENDERER, enableEmbeddedItemRenderer, 0);
            return tag;
        }

        public Options copy() {
            var options = new Options();
            options.tooltipFlags = tooltipFlags;
            options.enableEmbeddedItemRenderer = enableEmbeddedItemRenderer;
            return options;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Options that)) return false;
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

        public void setTooltip(TooltipFlags flags, boolean newValue) {
            if (newValue) {
                tooltipFlags &= ~flags.flags;
            } else {
                tooltipFlags |= flags.flags;
            }
        }

        public boolean getTooltip(TooltipFlags flags) {
            return (tooltipFlags & flags.flags) == 0;
        }

        public void setEnableEmbeddedItemRenderer(int enableEmbeddedItemRenderer) {
            this.enableEmbeddedItemRenderer = enableEmbeddedItemRenderer;
        }

        public int getEmbeddedItemRenderer() {
            return enableEmbeddedItemRenderer;
        }
    }

    public enum TooltipFlags {

        NAME(0x01, () -> ModConfig.Common.tooltipSkinName),
        AUTHOR(0x02, () -> ModConfig.Common.tooltipSkinAuthor),
        TYPE(0x04, () -> ModConfig.Common.tooltipSkinType),
        FLAVOUR(0x08, () -> ModConfig.Common.tooltipFlavour),

        HAS_SKIN(0x10, () -> ModConfig.Common.tooltipHasSkin),
        OPEN_WARDROBE(0x20, () -> ModConfig.Common.tooltipHasSkin),

        PREVIEW(0x80, () -> ModConfig.Common.tooltipSkinPreview);

        private final int flags;
        private final BooleanSupplier supplier;

        TooltipFlags(int flags, BooleanSupplier supplier) {
            this.flags = flags;
            this.supplier = supplier;
        }
    }
}
