package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.ISize2i;

public class Size2i implements ISize2i {

    public int width;
    public int height;

    public Size2i(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
