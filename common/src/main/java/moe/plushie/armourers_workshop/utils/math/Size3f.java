package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.utils.ObjectUtils;

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
    public String toString() {
        return String.format("(%g %g %g)", width, height, depth);
    }
}
