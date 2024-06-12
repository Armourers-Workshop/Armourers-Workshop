package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.utils.ObjectUtils;

import java.util.Objects;

@SuppressWarnings("unused")
public class Size3f {

    public static final Size3f ZERO = new Size3f(0, 0, 0);

    public float width;
    public float height;
    public float depth;

    public Size3f(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Size3f that)) return false;
        return Float.compare(width, that.width) == 0 && Float.compare(height, that.height) == 0 && Float.compare(depth, that.depth) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, depth);
    }

    @Override
    public String toString() {
        return String.format("(%g %g %g)", width, height, depth);
    }
}
