package moe.plushie.armourers_workshop.core.data.color;

import moe.plushie.armourers_workshop.api.painting.IBlockPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Objects;

public class BlockPaintColor implements IBlockPaintColor {

    public static final BlockPaintColor WHITE = new BlockPaintColor(PaintColor.WHITE);

    public static final BlockPaintColor EMPTY = new BlockPaintColor();

    protected IPaintColor paintColor;
    protected EnumMap<Side, IPaintColor> paintColors;

    public BlockPaintColor() {
    }

    public BlockPaintColor(IPaintColor paintColor) {
        this.paintColor = paintColor;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.paintColor = DataSerializers.getPaintColor(nbt, "All", null);
        this.paintColors = null;
        for (Side side : Side.values()) {
            IPaintColor paintColor = DataSerializers.getPaintColor(nbt, side.name, null);
            if (paintColor != null) {
                if (this.paintColors == null) {
                    this.paintColors = new EnumMap<>(Side.class);
                }
                this.paintColors.put(side, paintColor);
            }
        }
        this.mergePaintColorIfNeeded();
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (paintColor != null) {
            DataSerializers.putPaintColor(nbt, "All", paintColor, null);
        }
        if (paintColors != null) {
            paintColors.forEach((side, paintColor) -> DataSerializers.putPaintColor(nbt, side.name, paintColor, null));
        }
        return nbt;
    }

    public void putAll(IPaintColor paintColor) {
        this.paintColor = paintColor;
        this.paintColors = null;
    }

    @Override
    public void put(Direction dir, IPaintColor paintColor) {
        if (this.paintColors == null) {
            if (Objects.equals(this.paintColor, paintColor)) {
                return; // not any changes.
            }
            this.paintColors = getPaintColors(this.paintColor);
            this.paintColor = null;
        }
        Side side = Side.of(dir);
        if (paintColor != null) {
            this.paintColors.put(side, paintColor);
        } else {
            this.paintColors.remove(side);
        }
        this.mergePaintColorIfNeeded();
    }


    @Override
    public IPaintColor get(Direction dir) {
        return getOrDefault(dir, null);
    }

    @Override
    public IPaintColor getOrDefault(Direction dir, IPaintColor defaultValue) {
        if (paintColor != null) {
            return paintColor;
        }
        if (paintColors != null) {
            return paintColors.getOrDefault(Side.of(dir), defaultValue);
        }
        return defaultValue;
    }

    public Collection<IPaintColor> values() {
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
        if (!(o instanceof BlockPaintColor)) return false;
        BlockPaintColor that = (BlockPaintColor) o;
        return Objects.equals(paintColor, that.paintColor) && Objects.equals(paintColors, that.paintColors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paintColor, paintColors);
    }

    @Override
    public boolean isEmpty() {
        if (paintColors != null) {
            return paintColors.isEmpty();
        }
        return paintColor == null;
    }

    @Override
    public boolean isPureColor() {
        return paintColor != null;
    }

    private void mergePaintColorIfNeeded() {
        if (this.paintColors == null) {
            return;
        }
        int total = 0;
        IPaintColor lastColor = null;
        for (IPaintColor paintColor : this.paintColors.values()) {
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

    private EnumMap<Side, IPaintColor> getPaintColors(IPaintColor paintColor) {
        EnumMap<Side, IPaintColor> paintColors = new EnumMap<>(Side.class);
        if (paintColor != null) {
            for (Side side : Side.values()) {
                paintColors.put(side, paintColor);
            }
        }
        return paintColors;
    }

    // Assume the mapping for facing to the north.
    public enum Side {
        DOWN("Down", Direction.DOWN),
        UP("Up", Direction.UP),
        FRONT("Front", Direction.NORTH),
        BACK("Back", Direction.SOUTH),
        LEFT("Left", Direction.WEST),
        RIGHT("Right", Direction.EAST);

        final String name;
        final Direction direction;

        Side(String name, Direction direction) {
            this.name = name;
            this.direction = direction;
        }

        public static Side of(Direction direction) {
            return values()[direction.ordinal()];
        }

        public String getName() {
            return name;
        }

        public Direction getDirection() {
            return direction;
        }
    }
}
