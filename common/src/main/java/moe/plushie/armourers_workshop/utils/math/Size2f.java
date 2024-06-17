package moe.plushie.armourers_workshop.utils.math;

import java.util.Objects;

@SuppressWarnings("unused")
public class Size2f {

    public static final Size2f ZERO = new Size2f(0, 0);

    public float width;
    public float height;

    public Size2f(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Size2f that)) return false;
        return Float.compare(width, that.width) == 0 && Float.compare(height, that.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return String.format("(%g %g)", width, height);
    }
}
