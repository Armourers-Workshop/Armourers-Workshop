package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.MathUtils;

import java.util.Stack;

@SuppressWarnings("unused")
public class OpenPoseStack implements IPoseStack {

    private Pose entry = new Pose();
    private Stack<Pose> stack;

    public OpenPoseStack() {
    }

    public OpenPoseStack(IPoseStack poseStack) {
        entry.set(poseStack.last());
    }

    @Override
    public void pushPose() {
        if (stack == null) {
            stack = new Stack<>();
        }
        stack.push(entry);
        entry = new Pose(entry);
    }

    @Override
    public void popPose() {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        entry = stack.pop();
    }

    @Override
    public void setIdentity() {
        entry.pose.setIdentity();
        entry.normal.setIdentity();
        entry.properties = 0;
    }

    @Override
    public void translate(float x, float y, float z) {
        entry.pose.multiply(OpenMatrix4f.createTranslateMatrix(x, y, z));
    }

    @Override
    public void scale(float x, float y, float z) {
        // https://web.archive.org/web/20240125142900/http://www.songho.ca/opengl/gl_normaltransform.html
        entry.pose.scale(x, y, z);
        if (Math.abs(x) == Math.abs(y) && Math.abs(y) == Math.abs(z)) {
            if (x < 0.0f || y < 0.0f || z < 0.0f) {
                entry.normal.scale(Math.signum(x), Math.signum(y), Math.signum(z));
            }
        } else {
            entry.normal.scale(1.0f / x, 1.0f / y, 1.0f / z);
            entry.properties |= 0x02;
        }
    }

    @Override
    public void rotate(IQuaternionf quaternion) {
        entry.pose.rotate(quaternion);
        entry.normal.rotate(quaternion);
    }

    @Override
    public void multiply(IMatrix3f matrix) {
        entry.normal.multiply(OpenMatrix3f.of(matrix));
    }

    @Override
    public void multiply(IMatrix4f matrix) {
        entry.pose.multiply(OpenMatrix4f.of(matrix));
        //        if (!MatrixUtil.isTranslation(matrix)) {
//            if (MatrixUtil.isOrthonormal(matrix)) {
//                entry.normalMatrix.mul(new Matrix3f(matrix));
//            } else {
//                entry.computeNormal();
//            }
//        }
    }

    @Override
    public Pose last() {
        return entry;
    }

    public static class Pose implements IPoseStack.Pose {

        private final OpenMatrix4f pose;
        private final OpenMatrix3f normal;
        private int properties;

        public Pose() {
            this.pose = OpenMatrix4f.createScaleMatrix(1, 1, 1);
            this.normal = OpenMatrix3f.createScaleMatrix(1, 1, 1);
        }

        public Pose(IPoseStack.Pose entry) {
            this.pose = new OpenMatrix4f(entry.pose());
            this.normal = new OpenMatrix3f(entry.normal());
            this.properties = entry.properties();
        }

        //void computeNormal() {
        //    normal.set(pose);
        //    normal.invert();
        //    normal.transpose();
        //    properties |= 0x02;
        //}

        @Override
        public void transformPose(float[] values) {
            pose.multiply(values);
        }

        @Override
        public void transformNormal(float[] values) {
            normal.multiply(values);
            if ((properties & 0x02) != 0) {
                MathUtils.normalize(values);
            }
        }

        @Override
        public void set(IPoseStack.Pose entry) {
            pose.set(entry.pose());
            normal.set(entry.normal());
            properties = entry.properties();
        }

        @Override
        public OpenMatrix4f pose() {
            return pose;
        }

        @Override
        public OpenMatrix3f normal() {
            return normal;
        }

        public void setProperties(int properties) {
            this.properties = properties;
        }

        @Override
        public int properties() {
            return properties;
        }
    }
}
