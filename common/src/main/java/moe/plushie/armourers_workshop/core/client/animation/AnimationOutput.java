package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class AnimationOutput {

    private int flags = 0x00;

    private final Vector3f translate = new Vector3f();
    private final Vector3f rotation = new Vector3f();
    private final Vector3f scale = new Vector3f(1, 1, 1);

    private final AnimationTransform transform;

    public AnimationOutput(AnimationTransform transform) {
        this.transform = transform;
        if (transform != null) {
            transform.link(this);
        }
    }

    public void translate(float x, float y, float z) {
        translate.set(x, y, z);
        mark(0x10);
    }

    public void rotate(float x, float y, float z) {
        rotation.set(x, y, z);
        mark(0x20);
    }

    public void scale(float x, float y, float z) {
        scale.set(x, y, z);
        mark(0x40);
    }

    public void reset() {
        mark(flags);
        flags = 0x00;
    }

    public void mark(int newFlags) {
        flags |= newFlags;
        if (transform != null) {
            transform.mark(newFlags);
        }
    }

    public Vector3f getTranslate() {
        return translate;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }
}
