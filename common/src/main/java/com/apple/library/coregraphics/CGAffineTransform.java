package com.apple.library.coregraphics;

/**
 *                   |------------------ CGAffineTransformComponents ----------------|
 *     | a  b  0 |   | sx  0  0 |   |  1  0  0 |   | cos(t)  sin(t)  0 |   | 1  0  0 |
 *     | c  d  0 | = |  0 sy  0 | * | sh  1  0 | * |-sin(t)  cos(t)  0 | * | 0  1  0 |
 *     | tx ty 1 |   |  0  0  0 |   |  0  0  1 |   |   0       0     1 |   | tx ty 1 |
 * CGAffineTransform    scale           shear            rotation          translation
 */
public class CGAffineTransform {

    public static final CGAffineTransform IDENTITY = new CGAffineTransform(1, 0, 1, 0, 0, 0);

    public final float a;
    public final float b;
    public final float c;
    public final float d;
    public final float tx;
    public final float ty;

    public CGAffineTransform(float a, float b, float c, float d, float tx, float ty) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.tx = tx;
        this.ty = ty;
    }

    public static CGAffineTransform translation(float tx, float ty) {
        return new CGAffineTransform(1, 0, 1, 0, tx, ty);
    }

    public static CGAffineTransform scale(float sx, float sy) {
        return new CGAffineTransform(sx, 0, sy, 0, 0, 0);
    }

    public static CGAffineTransform rotation(float angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        return new CGAffineTransform(c, s, -s, c, 0, 0);
    }
}
