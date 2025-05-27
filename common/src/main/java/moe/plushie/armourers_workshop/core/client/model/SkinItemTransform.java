package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class SkinItemTransform {

    public static final SkinItemTransform NO_TRANSFORM = new SkinItemTransform(OpenVector3f.ZERO, OpenVector3f.ZERO, OpenVector3f.ONE);

    private final OpenVector3f translation;
    private final OpenVector3f rotation;
    private final OpenVector3f scale;

    public SkinItemTransform(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public static SkinItemTransform create(OpenTransform3f transform) {
        return new SkinItemTransform(transform.translate(), transform.rotation(), transform.scale());
    }

    public static SkinItemTransform create(OpenVector3f translation, OpenVector3f rotation, OpenVector3f scale) {
        translation = optimize(translation, OpenVector3f.ZERO);
        rotation = optimize(rotation, OpenVector3f.ZERO);
        scale = optimize(scale, OpenVector3f.ONE);
        if (translation == OpenVector3f.ZERO && rotation == OpenVector3f.ZERO && scale == OpenVector3f.ONE) {
            return NO_TRANSFORM;
        }
        return new SkinItemTransform(translation, rotation, scale);
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

    public OpenVector3f getTranslation() {
        return translation;
    }

    public OpenVector3f getRotation() {
        return rotation;
    }

    public OpenVector3f getScale() {
        return scale;
    }

    protected static <T> T optimize(T value, T targetValue) {
        if (value.equals(targetValue)) {
            return targetValue;
        }
        return value;
    }
}
