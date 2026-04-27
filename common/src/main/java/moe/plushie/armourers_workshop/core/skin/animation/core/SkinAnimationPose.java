package moe.plushie.armourers_workshop.core.skin.animation.core;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class SkinAnimationPose {

    protected final OpenVector3f translation = new OpenVector3f(0, 0, 0);
    protected final OpenVector3f rotation = new OpenVector3f(0, 0, 0);
    protected final OpenVector3f scale = new OpenVector3f(1, 1, 1);

    public void setTranslation(OpenVector3f value) {
        setTranslation(value.x(), value.y(), value.z());
    }

    public void setTranslation(float x, float y, float z) {
        translation.set(x, y, z);
    }

    public OpenVector3f translation() {
        return translation;
    }

    public void setRotation(OpenVector3f value) {
        setRotation(value.x(), value.y(), value.z());
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public OpenVector3f rotation() {
        return rotation;
    }

    public void setScale(OpenVector3f value) {
        setScale(value.x(), value.y(), value.z());
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    public OpenVector3f scale() {
        return scale;
    }

    public void reset() {
        translation.set(0, 0, 0);
        rotation.set(0, 0, 0);
        scale.set(1, 1, 1);
    }

//    public static class Linked extends SkinAnimationOutput {
//
//        private final Mode mode;
//        private final SkinAnimationTransform transform;
//
//        public Linked(SkinAnimationTransform transform, Mode mode) {
//            this.mode = mode;
//            this.transform = transform;
//            // link to output
//            if (transform != null) {
//                transform.link(this);
//            }
//        }
//
//        @Override
//        public void setTranslation(float x, float y, float z) {
//            // always update and mark dirty, because relies on flags by method called.
//            translation.set(x, y, z);
//            setDirty(0x10);
//        }
//
//        @Override
//        public void setRotation(float x, float y, float z) {
//            // always update and mark dirty, because relies on flags by method called.
//            rotation.set(x, y, z);
//            setDirty(0x20);
//        }
//
//        @Override
//        public void setScale(float x, float y, float z) {
//            // always update and mark dirty, because relies on flags by method called.
//            scale.set(x, y, z);
//            setDirty(0x40);
//        }
//
//        @Override
//        public void setDirty(int newFlags) {
//            super.setDirty(newFlags);
//            if (transform != null) {
//                transform.setDirty(newFlags);
//            }
//        }
//
//        public SkinAnimationTransform transform() {
//            return transform;
//        }
//
//        public Mode mode() {
//            return mode;
//        }
//    }
}
