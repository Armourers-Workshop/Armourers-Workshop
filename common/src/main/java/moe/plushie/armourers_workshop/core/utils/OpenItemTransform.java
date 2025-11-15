package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class OpenItemTransform {

    public static final OpenItemTransform NO_TRANSFORM = new OpenItemTransform(OpenVector3f.ZERO, OpenVector3f.ZERO, OpenVector3f.ONE);

    private final OpenVector3f translation;
    private final OpenVector3f rotation;
    private final OpenVector3f scale;

    public OpenItemTransform(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public static OpenItemTransform create(OpenTransform3f transform) {
        return new OpenItemTransform(transform.translate(), transform.rotation(), transform.scale());
    }

    public static OpenItemTransform create(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale) {
        translation = optimize(translation, OpenVector3f.ZERO);
        rotation = optimize(rotation, OpenVector3f.ZERO);
        scale = optimize(scale, OpenVector3f.ONE);
        if (translation == OpenVector3f.ZERO && rotation == OpenVector3f.ZERO && scale == OpenVector3f.ONE) {
            return NO_TRANSFORM;
        }
        return new OpenItemTransform(translation, rotation, scale);
    }

    public void apply(boolean applyLeftHandTransform, IPoseStack poseStack) {
        if (this == NO_TRANSFORM) {
            return;
        }
        int i = applyLeftHandTransform ? -1 : 1;
        if (translation != OpenVector3f.ZERO) {
            poseStack.translate(i * translation.x(), translation.y(), translation.z());
        }
        if (rotation != OpenVector3f.ZERO) {
            float f = rotation.x();
            float g = rotation.y();
            float h = rotation.z();
            if (applyLeftHandTransform) {
                g = -g;
                h = -h;
            }
            poseStack.rotate(OpenQuaternionf.fromEulerAnglesXYZ(f, g, h, true));
        }
        if (scale != OpenVector3f.ONE) {
            poseStack.scale(scale.x(), scale.y(), scale.z());
        }
    }

    public OpenVector3f translation() {
        return translation;
    }

    public OpenVector3f rotation() {
        return rotation;
    }

    public OpenVector3f scale() {
        return scale;
    }

    protected static <T> T optimize(T value, T targetValue) {
        if (value.equals(targetValue)) {
            return targetValue;
        }
        return value;
    }
}
