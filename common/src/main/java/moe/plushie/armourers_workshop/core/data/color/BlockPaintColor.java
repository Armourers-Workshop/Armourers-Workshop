package moe.plushie.armourers_workshop.core.data.color;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class BlockPaintColor implements IDataSerializable.Immutable {

    public static final BlockPaintColor WHITE = new BlockPaintColor();

    public static final IDataCodec<BlockPaintColor> CODEC = ExtraCodecs.serializable(BlockPaintColor::new);

    protected SkinPaintColor paintColor;
    protected EnumMap<OpenDirection, SkinPaintColor> paintColors;

    protected boolean isDirty = true;

    public BlockPaintColor() {
        this(SkinPaintColor.WHITE);
    }

    public BlockPaintColor(SkinPaintColor paintColor) {
        this.paintColor = paintColor;
    }

    public BlockPaintColor(IDataSerializer serializer) {
        this.paintColor = serializer.read(CodingKeys.ALL);
        this.paintColors = null;
        for (var entry : CodingKeys.SIDES.entrySet()) {
            var paintColor = serializer.read(entry.getValue());
            if (paintColor != null) {
                if (paintColors == null) {
                    paintColors = new EnumMap<>(OpenDirection.class);
                }
                paintColors.put(entry.getKey().direction(), paintColor);
            }
        }
        this.mergePaintColorIfNeeded();
    }


    @Override
    public void serialize(IDataSerializer serializer) {
        this.mergePaintColorIfNeeded();
        serializer.write(CodingKeys.ALL, paintColor);
        if (paintColors == null) {
            return;
        }
        for (var entry : CodingKeys.SIDES.entrySet()) {
            var paintColor = paintColors.get(entry.getKey().direction());
            if (paintColor != null) {
                serializer.write(entry.getValue(), paintColor);
            }
        }
    }

    public void putAll(SkinPaintColor paintColor) {
        this.paintColor = paintColor;
        this.paintColors = null;
        this.isDirty = true;
    }

    public void put(OpenDirection dir, SkinPaintColor paintColor) {
        // split the color when have a diff color.
        if (this.paintColors == null) {
            if (Objects.equals(this.paintColor, paintColor)) {
                return; // not any changes.
            }
            this.paintColors = getPaintColors(this.paintColor);
            this.paintColor = null;
        }
        if (paintColor != null) {
            this.paintColors.put(dir, paintColor);
        } else {
            this.paintColors.remove(dir);
        }
        this.isDirty = true;
    }

    public SkinPaintColor get(OpenDirection dir) {
        return getOrDefault(dir, null);
    }

    public SkinPaintColor getOrDefault(OpenDirection dir, SkinPaintColor defaultValue) {
        if (paintColor != null) {
            return paintColor;
        }
        if (paintColors != null) {
            return paintColors.getOrDefault(dir, defaultValue);
        }
        return defaultValue;
    }

    public Collection<SkinPaintColor> values() {
        if (paintColor != null) {
            return Collections.singleton(paintColor);
        }
        if (paintColors != null) {
            return paintColors.values();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockPaintColor that)) return false;
        return Objects.equals(paintColor, that.paintColor) && Objects.equals(paintColors, that.paintColors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paintColor, paintColors);
    }

    public BlockPaintColor copy() {
        var newValue = new BlockPaintColor(paintColor);
        if (paintColors != null) {
            newValue.paintColors = new EnumMap<>(paintColors);
        }
        return newValue;
    }

    public boolean isEmpty() {
        if (paintColors != null) {
            return paintColors.isEmpty();
        }
        return paintColor == null;
    }

    public boolean isPureColor() {
        return paintColor != null;
    }

    private void mergePaintColorIfNeeded() {
        if (!this.isDirty) {
            return;
        }
        this.isDirty = false;
        if (this.paintColors == null) {
            return;
        }
        int total = 0;
        SkinPaintColor lastColor = null;
        for (var paintColor : this.paintColors.values()) {
            if (lastColor != null && !lastColor.equals(paintColor)) {
                return;
            }
            lastColor = paintColor;
            total += 1;
        }
        if (total == 6) {
            this.paintColor = lastColor;
            this.paintColors = null;
        }
    }

    private EnumMap<OpenDirection, SkinPaintColor> getPaintColors(SkinPaintColor paintColor) {
        var paintColors = new EnumMap<OpenDirection, SkinPaintColor>(OpenDirection.class);
        if (paintColor != null) {
            for (var dir : OpenDirection.values()) {
                paintColors.put(dir, paintColor);
            }
        }
        return paintColors;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<SkinPaintColor> ALL = IDataSerializerKey.create("All", SkinPaintColor.CODEC, null);

        public static final Map<Side, IDataSerializerKey<SkinPaintColor>> SIDES = Collections.immutableMap(it -> {
            for (var side : Side.values()) {
                var name = side.serializedName;
                var key = IDataSerializerKey.create(name, SkinPaintColor.CODEC, null);
                it.put(side, key);
            }
        });
    }

    // Assume the mapping for facing to the north.
    private enum Side {
        DOWN("Down", OpenDirection.DOWN),
        UP("Up", OpenDirection.UP),
        FRONT("Front", OpenDirection.NORTH),
        BACK("Back", OpenDirection.SOUTH),
        LEFT("Left", OpenDirection.WEST),
        RIGHT("Right", OpenDirection.EAST);

        final String serializedName;
        final OpenDirection direction;

        Side(String serializedName, OpenDirection direction) {
            this.serializedName = serializedName;
            this.direction = direction;
        }

        public OpenDirection direction() {
            return direction;
        }
    }
}
