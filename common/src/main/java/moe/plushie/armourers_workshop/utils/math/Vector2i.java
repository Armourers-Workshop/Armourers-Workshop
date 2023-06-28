package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IVector2i;
import moe.plushie.armourers_workshop.utils.MathUtils;

@SuppressWarnings("unused")
public class Vector2i implements IVector2i {

    public int x;
    public int y;

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i(double x, double y) {
        this(MathUtils.floor(x), MathUtils.floor(y));
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    protected void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    protected void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return String.format("(%d %d)", x, y);
    }
}
