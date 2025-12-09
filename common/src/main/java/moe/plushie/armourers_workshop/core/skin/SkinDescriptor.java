package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.api.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.core.data.ItemStackStorage;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;

public class SkinDescriptor implements ISkinDescriptor, IDataSerializable.Immutable {

    public static final SkinDescriptor EMPTY = new SkinDescriptor("");

    public static final IDataCodec<SkinDescriptor> CODEC = ExtraCodecs.serializable(ExtraCodecs.COMPOUND_TAG.alternative(IDataCodec.STRING, TagSerializer::parse), SkinDescriptor::new);

    private final String identifier;
    private final SkinType type;
    private final Options options;
    private final SkinPaintScheme paintScheme;

    // not a required property, but it can help we reduce memory usage and improve performance.
    private ItemStack skinItemStack;

    public SkinDescriptor(String identifier) {
        this(identifier, SkinTypes.UNKNOWN, Options.DEFAULT, SkinPaintScheme.EMPTY);
    }

    public SkinDescriptor(String identifier, SkinType type) {
        this(identifier, type, Options.DEFAULT, SkinPaintScheme.EMPTY);
    }

    public SkinDescriptor(String identifier, SkinType type, SkinPaintScheme paintScheme) {
        this(identifier, type, Options.DEFAULT, paintScheme);
    }

    public SkinDescriptor(String identifier, SkinType type, Options options, SkinPaintScheme paintScheme) {
        this.identifier = identifier;
        this.type = type;
        this.options = options;
        this.paintScheme = paintScheme;
    }

    public SkinDescriptor(IDataSerializer serializer) {
        this.identifier = serializer.read(CodingKeys.IDENTIFIER);
        this.type = serializer.read(CodingKeys.TYPE);
        this.options = serializer.read(CodingKeys.OPTIONS);
        this.paintScheme = serializer.read(CodingKeys.SCHEME);
    }

    public static SkinDescriptor of(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return EMPTY;
        }
        var storage = ItemStackStorage.of(itemStack);
        var descriptor = storage.skinDescriptor;
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
        var skinType = type();
        if (skinType == SkinTypes.ITEM) {
            return true;
        }
        if (skinType instanceof SkinType.Tool toolType) {
            return toolType.contains(itemStack);
        }
        return false;
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        serializer.write(CodingKeys.IDENTIFIER, identifier);
        serializer.write(CodingKeys.TYPE, type);
        serializer.write(CodingKeys.OPTIONS, options);
        serializer.write(CodingKeys.SCHEME, paintScheme);
    }

    public SkinDescriptor withType(SkinType type) {
        return new SkinDescriptor(identifier, type, options, paintScheme);
    }

    public SkinDescriptor withOptions(Options options) {
        return new SkinDescriptor(identifier, type, options, paintScheme);
    }

    public SkinDescriptor withPaintScheme(SkinPaintScheme paintScheme) {
        return new SkinDescriptor(identifier, type, options, paintScheme);
    }

    public ItemStack sharedItemStack() {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (skinItemStack != null) {
            return skinItemStack;
        }
        var itemStack = new ItemStack(ModItems.SKIN.get());
        itemStack.set(ModDataComponents.SKIN.get(), this);
        skinItemStack = itemStack;
        return itemStack;
    }

    public ItemStack asItemStack() {
        return sharedItemStack().copy();
    }

    public boolean isEmpty() {
        return this == EMPTY || identifier.isEmpty();
    }

    public SkinPaintScheme paintScheme() {
        return paintScheme;
    }

    public SkinType type() {
        return type;
    }

    public Options options() {
        return options;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return String.format("%s@%s[%s]", identifier, type.registryName().path(), type.id());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinDescriptor that)) return false;
        return identifier.equals(that.identifier) && paintScheme.equals(that.paintScheme);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<String> IDENTIFIER = IDataSerializerKey.create("Identifier", IDataCodec.STRING, "");
        public static final IDataSerializerKey<SkinType> TYPE = IDataSerializerKey.create("SkinType", SkinTypes.CODEC, SkinTypes.UNKNOWN);
        public static final IDataSerializerKey<Options> OPTIONS = IDataSerializerKey.create("SkinOptions", Options.CODEC, Options.DEFAULT);
        public static final IDataSerializerKey<SkinPaintScheme> SCHEME = IDataSerializerKey.create("SkinDyes", SkinPaintScheme.CODEC, SkinPaintScheme.EMPTY);

        public static final IDataSerializerKey<Integer> TOOLTIP_FLAGS = IDataSerializerKey.create("TooltipFlags", IDataCodec.INT, 0);
        public static final IDataSerializerKey<Integer> USING_EMBEDDED_RENDERER = IDataSerializerKey.create("EmbeddedItemRenderer", IDataCodec.INT, 0);
    }

    public static class Options implements IDataSerializable.Immutable {

        public static Options DEFAULT = new Options();

        public static final IDataCodec<Options> CODEC = ExtraCodecs.serializable(Options::new);

        private int tooltipFlags;
        private int embeddedItemRenderer; // 0 auto, 1 disable, 2 enable, 3 enable without gui.

        public Options() {
            this(0, 0);
        }

        public Options(int tooltipFlags, int embeddedItemRenderer) {
            this.tooltipFlags = tooltipFlags;
            this.embeddedItemRenderer = embeddedItemRenderer;
        }

        public Options(IDataSerializer serializer) {
            this.tooltipFlags = serializer.read(CodingKeys.TOOLTIP_FLAGS);
            this.embeddedItemRenderer = serializer.read(CodingKeys.USING_EMBEDDED_RENDERER);
        }

        @Override
        public void serialize(IDataSerializer serializer) {
            serializer.write(CodingKeys.TOOLTIP_FLAGS, tooltipFlags);
            serializer.write(CodingKeys.USING_EMBEDDED_RENDERER, embeddedItemRenderer);
        }

        public Options copy() {
            return new Options(tooltipFlags, embeddedItemRenderer);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Options that)) return false;
            return tooltipFlags == that.tooltipFlags && embeddedItemRenderer == that.embeddedItemRenderer;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tooltipFlags, embeddedItemRenderer);
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

        public void setEmbeddedItemRenderer(int embeddedItemRenderer) {
            this.embeddedItemRenderer = embeddedItemRenderer;
        }

        public int embeddedItemRenderer() {
            return embeddedItemRenderer;
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
