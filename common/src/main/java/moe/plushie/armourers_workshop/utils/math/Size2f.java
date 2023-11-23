package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.ISize2i;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

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
    public String toString() {
        return String.format("(%g %g)", width, height);
    }
}
