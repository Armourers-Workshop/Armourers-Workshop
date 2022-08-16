package moe.plushie.armourers_workshop.utils.math;

import com.google.common.base.MoreObjects;
import moe.plushie.armourers_workshop.utils.MathUtils;

public class Vector2i {

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

    public int getX() {
        return this.x;
    }

    protected void setX(int p_223471_1_) {
        this.x = p_223471_1_;
    }

    public int getY() {
        return this.y;
    }

    protected void setY(int p_185336_1_) {
        this.y = p_185336_1_;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).toString();
    }
}
