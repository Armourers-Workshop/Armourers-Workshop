package moe.plushie.armourers_workshop.core.skin.transform;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataInputStream;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataOutputStream;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.io.IOException;
import java.util.Objects;

public class SkinTransform implements ISkinTransform {

    public static final SkinTransform IDENTIFIER = new SkinTransform();

    private Vector3f postTranslate = Vector3f.ZERO;
    private Vector3f postRotation = Vector3f.ZERO;
    private Vector3f postScale = Vector3f.ONE;

    private Vector3f preTranslate = Vector3f.ZERO;
    private Vector3f preRotation = Vector3f.ZERO;
    private Vector3f preScale = Vector3f.ONE;

    public static SkinTransform createRotationTransform(Vector3f rotation) {
        if (!rotation.equals(Vector3f.ZERO)) {
            SkinTransform transform = new SkinTransform();
            transform.postRotation = rotation;
            return transform;
        }
        return IDENTIFIER;
    }

    public static SkinTransform createTranslateTransform(Vector3f offset) {
        if (!offset.equals(Vector3f.ZERO)) {
            SkinTransform transform = new SkinTransform();
            transform.postTranslate = offset;
            return transform;
        }
        return IDENTIFIER;
    }

    @Override
    public void pre(IPoseStack poseStack) {
        if (this == IDENTIFIER) {
            return;
        }
        commit(poseStack, preTranslate, preRotation, preScale);
    }

    @Override
    public void post(IPoseStack poseStack) {
        if (this == IDENTIFIER) {
            return;
        }
        commit(poseStack, postTranslate, postRotation, postScale);
    }

    public void readFromStream(IDataInputStream stream) throws IOException {
        postTranslate = optimize(stream.readVector3f(), Vector3f.ZERO);
        postRotation = optimize(stream.readVector3f(), Vector3f.ZERO);
        postScale = optimize(stream.readVector3f(), Vector3f.ONE);
        preTranslate = optimize(stream.readVector3f(), Vector3f.ZERO);
        preRotation = optimize(stream.readVector3f(), Vector3f.ZERO);
        preScale = optimize(stream.readVector3f(), Vector3f.ONE);
    }

    public void writeToStream(IDataOutputStream stream) throws IOException {
        stream.writeVector3f(postTranslate);
        stream.writeVector3f(postRotation);
        stream.writeVector3f(postScale);
        stream.writeVector3f(preTranslate);
        stream.writeVector3f(preRotation);
        stream.writeVector3f(preScale);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkinTransform that = (SkinTransform) o;
        return postTranslate.equals(that.postTranslate) && postRotation.equals(that.postRotation) && postScale.equals(that.postScale) && preTranslate.equals(that.preTranslate) && preRotation.equals(that.preRotation) && preScale.equals(that.preScale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postTranslate, postRotation, postScale, preTranslate, preRotation, preScale);
    }

    private void commit(IPoseStack poseStack, Vector3f preTranslate, Vector3f preRotation, Vector3f preScale) {
        if (preTranslate != Vector3f.ZERO) {
            poseStack.translate(preTranslate.getX(), preTranslate.getY(), preTranslate.getZ());
        }
        if (preRotation != Vector3f.ZERO) {
            poseStack.rotate(new OpenQuaternionf(preRotation.getX(), preRotation.getY(), preRotation.getZ(), true));
        }
        if (preScale != Vector3f.ONE) {
            poseStack.scale(preScale.getX(), preScale.getY(), preScale.getZ());
        }
    }

    private <T> T optimize(T value, T targetValue) {
        if (value.equals(targetValue)) {
            return targetValue;
        }
        return value;
    }
}
