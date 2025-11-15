package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IVector2f;
import moe.plushie.armourers_workshop.core.utils.Objects;

@SuppressWarnings("unused")
public class OpenVector2f implements IVector2f {

    public static final int BYTES = Float.BYTES * 3;

    public static OpenVector2f ZERO = new OpenVector2f();

    public float x;
    public float y;

    public OpenVector2f() {
    }

    public OpenVector2f(IVector2f value) {
        this(value.x(), value.y());
    }

    public OpenVector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public OpenVector2f(double x, double y) {
        this((float) x, (float) y);
    }

    public static OpenVector2f of(long value) {
        float p1 = Float.intBitsToFloat((int) value);
        float p2 = Float.intBitsToFloat((int) (value >> 32));
        return new OpenVector2f(p1, p2);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public float x() {
        return this.x;
    }

    @Override
    public float y() {
        return this.y;
    }

    public long asLong() {
        int p1 = Float.floatToRawIntBits(x);
        int p2 = Float.floatToRawIntBits(y);
        return ((long) p2 << 32) | p1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenVector2f that)) return false;
        return Float.compare(that.x, x) == 0 && Float.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public String toString() {
        return OpenMath.format("(%f %f)", x, y);
    }
}
