package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class AnimatedPoint {

    protected final Vector3f translate = new Vector3f();
    protected final Vector3f rotation = new Vector3f();
    protected final Vector3f scale = new Vector3f(1, 1, 1);

    protected int dirty = 0;

    public void setTranslate(float x, float y, float z) {
        if (x != 0 || y != 0 || z != 0) {
            translate.set(x, y, z);
            setDirty(0x10);
        }
    }

    public Vector3f getTranslate() {
        if ((dirty & 0x10) != 0) {
            return translate;
        }
        return Vector3f.ZERO;
    }

    public void setRotate(float x, float y, float z) {
        if (x != 0 || y != 0 || z != 0) {
            rotation.set(x, y, z);
            setDirty(0x20);
        }
    }

    public Vector3f getRotation() {
        if ((dirty & 0x20) != 0) {
            return rotation;
        }
        return Vector3f.ZERO;
    }

    public void setScale(float x, float y, float z) {
        if (x != 1 || y != 1 || z != 1) {
            scale.set(x, y, z);
            setDirty(0x40);
        }
    }

    public Vector3f getScale() {
        if ((dirty & 0x40) != 0) {
            return scale;
        }
        return Vector3f.ONE;
    }

    public void setTranslate(Vector3f value) {
        setTranslate(value.getX(), value.getY(), value.getZ());
    }

    public void setRotate(Vector3f value) {
        setRotate(value.getX(), value.getY(), value.getZ());
    }

    public void setScale(Vector3f value) {
        setScale(value.getX(), value.getY(), value.getZ());
    }

    public void clear() {
        dirty = 0x00;
    }

    public void setDirty(int newFlags) {
        dirty |= newFlags;
    }
}
