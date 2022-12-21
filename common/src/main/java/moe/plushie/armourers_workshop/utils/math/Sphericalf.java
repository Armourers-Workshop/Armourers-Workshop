package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.utils.MathUtils;

public class Sphericalf {

    private static final float EPS = 0.000001f;

    public float radius;
    public float phi;
    public float theta;

    public Sphericalf() {
        set(1f, 0f, 0f);
    }

    public Sphericalf set(float radius, float phi, float theta) {
        this.radius = radius;
        this.phi = phi;
        this.theta = theta;
        return this;
    }

    public Sphericalf makeSafe() {
        this.phi = Math.max(EPS, Math.min(MathUtils.PI - EPS, phi));
        return this;
    }

    public Sphericalf setFromVector3(Vector3f vector) {
        return setFromCartesianCoords(vector.getX(), vector.getY(), vector.getZ());
    }

    public Sphericalf setFromCartesianCoords(float x, float y, float z) {
        this.radius = MathUtils.sqrt(x * x + y * y + z * z);
        if (this.radius == 0f) {
            this.theta = 0f;
            this.phi = 0f;
        } else {
            this.theta = MathUtils.atan2(x, z);
            this.phi = MathUtils.acos(MathUtils.clamp(y / radius, -1, 1));
        }
        return this;
    }
}
