package moe.plushie.armourers_workshop.core.data.transform;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.io.IOException;
import java.util.Objects;

public class SkinTransform implements ITransformf, ISkinTransform {

    public static final int BYTES = Vector3f.BYTES * 5 + Integer.BYTES;

    public static final SkinTransform IDENTITY = new SkinTransform();

    private Vector3f translate = Vector3f.ZERO;
    private Vector3f rotation = Vector3f.ZERO;
    private Vector3f scale = Vector3f.ONE;
    private Vector3f offset = Vector3f.ZERO;
    private Vector3f pivot = Vector3f.ZERO;

    public static SkinTransform create(Vector3f translate, Vector3f rotation, Vector3f scale) {
        return create(translate, rotation, scale, Vector3f.ZERO, Vector3f.ZERO);
    }

    public static SkinTransform create(Vector3f translate, Vector3f rotation, Vector3f scale, Vector3f pivot, Vector3f offset) {
        //
        if (translate.equals(Vector3f.ZERO) && rotation.equals(Vector3f.ZERO) && scale.equals(Vector3f.ONE) && pivot.equals(Vector3f.ZERO) && offset.equals(Vector3f.ZERO)) {
            return IDENTITY;
        }
        var transform = new SkinTransform();
        transform.translate = translate;
        transform.rotation = rotation;
        transform.scale = scale;
        transform.offset = offset;
        transform.pivot = pivot;
        return transform;
    }

    public static SkinTransform createRotationTransform(Vector3f rotation) {
        if (!rotation.equals(Vector3f.ZERO)) {
            var transform = new SkinTransform();
            transform.rotation = rotation;
            return transform;
        }
        return IDENTITY;
    }

    public static SkinTransform createScaleTransform(float sx, float sy, float sz) {
        if (sx != 1 || sy != 1 || sz != 1) {
            var transform = new SkinTransform();
            transform.scale = new Vector3f(sx, sy, sz);
            return transform;
        }
        return IDENTITY;
    }

    public static SkinTransform createScaleTransform(Vector3f scale) {
        if (!scale.equals(Vector3f.ONE)) {
            var transform = new SkinTransform();
            transform.scale = scale;
            return transform;
        }
        return IDENTITY;
    }

    public static SkinTransform createTranslateTransform(float tx, float ty, float tz) {
        if (tx != 0 || ty != 0 || tz != 0) {
            var transform = new SkinTransform();
            transform.translate = new Vector3f(tx, ty, tz);
            return transform;
        }
        return IDENTITY;
    }

    public static SkinTransform createTranslateTransform(Vector3f offset) {
        if (!offset.equals(Vector3f.ZERO)) {
            var transform = new SkinTransform();
            transform.translate = offset;
            return transform;
        }
        return IDENTITY;
    }

    @Override
    public void apply(IPoseStack poseStack) {
        if (this == IDENTITY) {
            return;
        }
        if (translate != Vector3f.ZERO) {
            poseStack.translate(translate.getX(), translate.getY(), translate.getZ());
        }
        if (rotation != Vector3f.ZERO) {
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(pivot.getX(), pivot.getY(), pivot.getZ());
            }
            poseStack.rotate(OpenQuaternionf.fromZYX(rotation, true));
            // poseStack.rotate(Vector3f.ZP.rotationDegrees(rotation.getZ()));
            // poseStack.rotate(Vector3f.YP.rotationDegrees(rotation.getY()));
            // poseStack.rotate(Vector3f.XP.rotationDegrees(rotation.getX()));
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
        if (scale != Vector3f.ONE) {
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
        }
        if (offset != Vector3f.ZERO) {
            poseStack.translate(offset.getX(), offset.getY(), offset.getZ());
        }
    }

    public void readFromStream(IInputStream stream) throws IOException {
        int flags = stream.readInt();
        translate = optimize(stream.readVector3f(), Vector3f.ZERO);
        rotation = optimize(stream.readVector3f(), Vector3f.ZERO);
        scale = optimize(stream.readVector3f(), Vector3f.ONE);
        offset = optimize(stream.readVector3f(), Vector3f.ZERO);
        pivot = optimize(stream.readVector3f(), Vector3f.ZERO);
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeInt(0);
        stream.writeVector3f(translate);
        stream.writeVector3f(rotation);
        stream.writeVector3f(scale);
        stream.writeVector3f(offset);
        stream.writeVector3f(pivot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinTransform that)) return false;
        return translate.equals(that.translate) && rotation.equals(that.rotation) && scale.equals(that.scale) && pivot.equals(that.pivot) && offset.equals(that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(translate, rotation, scale, pivot, offset);
    }

    @Override
    public boolean isIdentity() {
        return this == IDENTITY;
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

    @Override
    public Vector3f getOffset() {
        return offset;
    }

    @Override
    public Vector3f getPivot() {
        return pivot;
    }

    private <T> T optimize(T value, T targetValue) {
        if (value.equals(targetValue)) {
            return targetValue;
        }
        return value;
    }
}
