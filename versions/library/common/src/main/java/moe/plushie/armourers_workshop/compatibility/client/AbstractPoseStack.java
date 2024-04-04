package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class AbstractPoseStack extends AbstractPoseStackImpl implements IPoseStack {

    private final PoseStack stack;

    public AbstractPoseStack() {
        this(new PoseStack());
    }

    public AbstractPoseStack(PoseStack poseStack) {
        this.stack = poseStack;
    }

    public static IPoseStack wrap(PoseStack poseStack) {
        return IAssociatedObjectProvider.of(poseStack, AbstractPoseStack::new);
    }

    public static PoseStack unwrap(IPoseStack poseStack) {
        if (poseStack instanceof AbstractPoseStack) {
            return ((AbstractPoseStack) poseStack).stack;
        }
        PoseStack poseStack1 = new PoseStack();
        IPoseStack poseStack2 = wrap(poseStack1);
        poseStack2.last().set(poseStack.last());
        return poseStack1;
    }

    public void pushPose() {
        stack.pushPose();
    }

    public void popPose() {
        stack.popPose();
    }

    public void translate(float x, float y, float z) {
        stack.translate(x, y, z);
    }

    public void scale(float x, float y, float z) {
        // https://web.archive.org/web/20240125142900/http://www.songho.ca/opengl/gl_normaltransform.html
        auto entry = last();
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

    public void rotate(IQuaternionf quaternion) {
        stack.mulPose(quaternion);
    }

    @Override
    public void multiply(IMatrix3f matrix) {
        auto entry = last();
        entry.normal.multiply(matrix);
    }

    @Override
    public void multiply(IMatrix4f matrix) {
        auto entry = last();
        entry.pose.multiply(matrix);
//        if (!MatrixUtil.isTranslation(matrix)) {
//            if (MatrixUtil.isOrthonormal(matrix)) {
//                entry.normalMatrix.mul(new Matrix3f(matrix));
//            } else {
//                entry.computeNormal();
//            }
//        }
    }

    @Override
    public void setIdentity() {
        stack.setIdentity();
    }

    @Override
    public Pose last() {
        return IAssociatedObjectProvider.of(stack.last(), Pose::new);
    }

    public static class Pose implements IPoseStack.Pose {

        private final AbstractMatrix4f pose;
        private final AbstractMatrix3f normal;
        private int properties;

        public Pose(PoseStack.Pose pose) {
            this.pose = new AbstractMatrix4f(pose.pose());
            this.normal = new AbstractMatrix3f(pose.normal());
            this.properties = 0;
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
        public IMatrix4f pose() {
            return pose;
        }

        @Override
        public IMatrix3f normal() {
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
