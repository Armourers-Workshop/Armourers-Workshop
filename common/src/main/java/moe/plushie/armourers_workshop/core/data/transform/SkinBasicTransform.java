package moe.plushie.armourers_workshop.core.data.transform;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.io.IOException;
import java.util.Objects;

public class SkinBasicTransform implements ITransformf, ISkinTransform {

    public static final SkinBasicTransform IDENTITY = new SkinBasicTransform();

    private Vector3f pivot = Vector3f.ZERO;
    private Vector3f translate = Vector3f.ZERO;
    private Vector3f rotation = Vector3f.ZERO;
    private Vector3f scale = Vector3f.ONE;

    public static SkinBasicTransform create(Vector3f translate, Vector3f rotation, Vector3f scale) {
        return create(translate, rotation, scale, Vector3f.ZERO);
    }

    public static SkinBasicTransform create(Vector3f translate, Vector3f rotation, Vector3f scale, Vector3f pivot) {
        //
        if (translate.equals(Vector3f.ZERO) && rotation.equals(Vector3f.ZERO) && scale.equals(Vector3f.ONE) && pivot.equals(Vector3f.ZERO)) {
            return IDENTITY;
        }
        SkinBasicTransform transform = new SkinBasicTransform();
        transform.pivot = pivot;
        transform.translate = translate;
        transform.rotation = rotation;
        transform.scale = scale;
        return transform;
    }

    public static SkinBasicTransform createRotationTransform(Vector3f rotation) {
        if (!rotation.equals(Vector3f.ZERO)) {
            SkinBasicTransform transform = new SkinBasicTransform();
            transform.rotation = rotation;
            return transform;
        }
        return IDENTITY;
    }

    public static SkinBasicTransform createScaleTransform(float sx, float sy, float sz) {
        if (sx != 1 || sy != 1 || sz != 1) {
            SkinBasicTransform transform = new SkinBasicTransform();
            transform.scale = new Vector3f(sx, sy, sz);
            return transform;
        }
        return IDENTITY;
    }

    public static SkinBasicTransform createScaleTransform(Vector3f scale) {
        if (!scale.equals(Vector3f.ONE)) {
            SkinBasicTransform transform = new SkinBasicTransform();
            transform.scale = scale;
            return transform;
        }
        return IDENTITY;
    }

    public static SkinBasicTransform createTranslateTransform(float tx, float ty, float tz) {
        if (tx != 0 || ty != 0 || tz != 0) {
            SkinBasicTransform transform = new SkinBasicTransform();
            transform.translate = new Vector3f(tx, ty, tz);
            return transform;
        }
        return IDENTITY;
    }

    public static SkinBasicTransform createTranslateTransform(Vector3f offset) {
        if (!offset.equals(Vector3f.ZERO)) {
            SkinBasicTransform transform = new SkinBasicTransform();
            transform.translate = offset;
            return transform;
        }
        return IDENTITY;
    }

    @Override
    public void pre(IPoseStack poseStack) {
        if (this == IDENTITY) {
            return;
        }
    }

    @Override
    public void post(IPoseStack poseStack) {
        if (this == IDENTITY) {
            return;
        }
        if (rotation != Vector3f.ZERO) {
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(pivot.getX(), pivot.getY(), pivot.getZ());
            }
            poseStack.rotate(Vector3f.ZP.rotationDegrees(rotation.getZ()));
            poseStack.rotate(Vector3f.YP.rotationDegrees(rotation.getY()));
            poseStack.rotate(Vector3f.XP.rotationDegrees(rotation.getX()));
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
        if (translate != Vector3f.ZERO) {
            poseStack.translate(translate.getX(), translate.getY(), translate.getZ());
        }
        if (scale != Vector3f.ONE) {
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
        }
    }

    public void readFromStream(IInputStream stream) throws IOException {
        translate = optimize(stream.readVector3f(), Vector3f.ZERO);
        rotation = optimize(stream.readVector3f(), Vector3f.ZERO);
        scale = optimize(stream.readVector3f(), Vector3f.ONE);
        pivot = optimize(stream.readVector3f(), Vector3f.ZERO);
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeVector3f(translate);
        stream.writeVector3f(rotation);
        stream.writeVector3f(scale);
        stream.writeVector3f(pivot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkinBasicTransform that = (SkinBasicTransform) o;
        return pivot.equals(that.pivot) && translate.equals(that.translate) && rotation.equals(that.rotation) && scale.equals(that.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pivot, translate, rotation, scale);
    }

    @Override
    public boolean isIdentity() {
        return this == IDENTITY;
    }

    @Override
    public Vector3f getPivot() {
        return pivot;
    }

    @Override
    public Vector3f getTranslate() {
        return translate;
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }

    @Override
    public Vector3f getScale() {
        return scale;
    }

    private <T> T optimize(T value, T targetValue) {
        if (value.equals(targetValue)) {
            return targetValue;
        }
        return value;
    }
}
