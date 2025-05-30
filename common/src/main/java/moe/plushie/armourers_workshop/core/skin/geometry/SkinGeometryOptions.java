package moe.plushie.armourers_workshop.core.skin.geometry;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryOptions;
import moe.plushie.armourers_workshop.core.utils.Objects;

public class SkinGeometryOptions implements ISkinGeometryOptions {

    public static final SkinGeometryOptions EMPTY = new SkinGeometryOptions();

    private long value = 0;
    private int renderOrder = 0; // 0X11: 0 default, 1 behind, 2 in_front

    public SkinGeometryOptions() {
    }

    public SkinGeometryOptions(long value) {
        this.value = value;
        this.renderOrder = (int) (value & 0x03);
    }

    public void setRenderOrder(int renderOrder) {
        this.renderOrder = renderOrder;
        this.value = (value & ~0x03) | (renderOrder & 0x03);
    }

    @Override
    public int getRenderOrder() {
        return renderOrder;
    }

    @Override
    public boolean isEmpty() {
        return value == 0;
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinGeometryOptions that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("[renderOrder=%d]", renderOrder);
    }
}
