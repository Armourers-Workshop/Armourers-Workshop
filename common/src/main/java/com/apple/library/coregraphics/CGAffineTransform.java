package com.apple.library.coregraphics;

import com.apple.library.impl.InterpolableImpl;

import java.util.Objects;

/**
 *               |------------------ CGAffineTransformComponents ----------------|
 * | a  b  0 |   | sx  0  0 |   |  1  0  0 |   | cos(t)  sin(t)  0 |   | 1  0  0 |
 * | c  d  0 | = |  0 sy  0 | * | sh  1  0 | * |-sin(t)  cos(t)  0 | * | 0  1  0 |
 * | tx ty 1 |   |  0  0  0 |   |  0  0  1 |   |   0       0     1 |   | tx ty 1 |
 * CGAffineTransform    scale           shear            rotation          translation
 */
public class CGAffineTransform implements InterpolableImpl<CGAffineTransform> {

    public static final CGAffineTransform IDENTITY = new CGAffineTransform(1, 0, 1, 0, 0, 0);

    public float a;
    public float b;
    public float c;
    public float d;
    public float tx;
    public float ty;

    public CGAffineTransform(float a, float b, float c, float d, float tx, float ty) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.tx = tx;
        this.ty = ty;
    }

    public static CGAffineTransform createTranslation(float tx, float ty) {
        return new CGAffineTransform(1, 0, 0, 1, tx, ty);
    }

    public static CGAffineTransform createScale(float sx, float sy) {
        return new CGAffineTransform(sx, 0, 0, sy, 0, 0);
    }

    public static CGAffineTransform createRotation(float angle) {
        // is near
        if (Math.abs(angle - Math.PI / 2f) < 0.0001) {
            return new CGAffineTransform(0, 1, -1, 0, 0, 0);
        }
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        return new CGAffineTransform(c, s, -s, c, 0, 0);
    }

    public void rotate(float angle) {
        if (angle == 0) {
            return;
        }
        multiply(createRotation(angle), this, this);
    }

    public void translate(float tx, float ty) {
        if (tx == 0 && ty == 0) {
            return;
        }
        multiply(createTranslation(tx, ty), this, this);
    }

    public void scale(float sx, float sy) {
        if (sx == 0 && sy == 0) {
            return;
        }
        multiply(createScale(sx, sy), this, this);
    }

    public void concat(CGAffineTransform t) {
        if (t == IDENTITY) {
            return;
        }
        multiply(this, t, this);
    }

    public void invert() {
        float determinant = a * d - c * b;
        if (determinant == 0) {
            return;
        }
        float na = d / determinant;
        float nb = -b / determinant;
        float nc = -c / determinant;
        float nd = a / determinant;
        float ntx = (-d * tx + c * ty) / determinant;
        float nty = (b * tx - a * ty) / determinant;
        this.a = na;
        this.b = nb;
        this.c = nc;
        this.d = nd;
        this.tx = ntx;
        this.ty = nty;
    }

    public CGAffineTransform inverted() {
        CGAffineTransform t = copy();
        t.invert();
        return t;
    }

    public boolean isIdentity() {
        if (this == IDENTITY) {
            return true;
        }
        return this.equals(IDENTITY);
    }

    public CGAffineTransform copy() {
        return new CGAffineTransform(a, b, c, d, tx, ty);
    }

    @Override
    public CGAffineTransform interpolating(CGAffineTransform in, float t) {
        if (t <= 0) {
            return this;
        }
        if (t >= 1) {
            return in;
        }
        float v = 1 - t;
        float a = v * this.a + t * in.c;
        float b = v * this.a + t * in.d;
        float c = v * this.c + t * in.c;
        float d = v * this.c + t * in.d;
        float tx = v * this.tx + t * in.tx;
        float ty = v * this.tx + t * in.ty;
        return new CGAffineTransform(a, b, c, d, tx, ty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CGAffineTransform transform = (CGAffineTransform) o;
        return Float.compare(transform.a, a) == 0 && Float.compare(transform.b, b) == 0 && Float.compare(transform.c, c) == 0 && Float.compare(transform.d, d) == 0 && Float.compare(transform.tx, tx) == 0 && Float.compare(transform.ty, ty) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d, tx, ty);
    }

    @Override
    public String toString() {
        return String.format("[%f, %f, %f, %f, %f, %f]", a, b, c, d, tx, ty);
    }

    private static void multiply(CGAffineTransform t1, CGAffineTransform t2, CGAffineTransform ret) {
        float a = t1.a * t2.a + t1.b * t2.c;
        float b = t1.a * t2.b + t1.b * t2.d;
        float c = t1.c * t2.a + t1.d * t2.c;
        float d = t1.c * t2.b + t1.d * t2.d;
        float tx = t1.tx * t2.a + t1.ty * t2.c + t2.tx;
        float ty = t1.tx * t2.b + t1.ty * t2.d + t2.ty;
        ret.a = a;
        ret.b = b;
        ret.c = c;
        ret.d = d;
        ret.tx = tx;
        ret.ty = ty;
    }
}
