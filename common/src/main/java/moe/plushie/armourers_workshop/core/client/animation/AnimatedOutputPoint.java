package moe.plushie.armourers_workshop.core.client.animation;

public class AnimatedOutputPoint extends AnimatedPoint {

    private final AnimatedOutputMode mode;
    private final AnimatedTransform transform;

    public AnimatedOutputPoint(AnimatedTransform transform, AnimatedOutputMode mode) {
        this.mode = mode;
        this.transform = transform;
    }

    @Override
    public void setTranslation(float x, float y, float z) {
        // always update and mark dirty, because relies on flags by method called.
        translation.set(x, y, z);
        setDirty(0x10);
    }

    @Override
    public void setRotation(float x, float y, float z) {
        // always update and mark dirty, because relies on flags by method called.
        rotation.set(x, y, z);
        setDirty(0x20);
    }

    @Override
    public void setScale(float x, float y, float z) {
        // always update and mark dirty, because relies on flags by method called.
        scale.set(x, y, z);
        setDirty(0x40);
    }

    @Override
    public void setDirty(int newFlags) {
        super.setDirty(newFlags);
        if (transform != null) {
            transform.setDirty(newFlags);
        }
    }

    public AnimatedOutputMode mode() {
        return mode;
    }
}
