package moe.plushie.armourers_workshop.compatibility.extensions.com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import org.joml.Matrix4fStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.21, )")
@Extension
public class ModelView {

    public static IPoseStack modelViewStack = new Proxy(RenderSystem.getModelViewStack());

    public static IPoseStack getExtendedModelViewStack(@ThisClass Class<?> clazz) {
        return modelViewStack;
    }

    public static class Proxy extends OpenPoseStack {

        private final Matrix4fStack stack;
        private final Pose entry = new Pose();

        public Proxy(Matrix4fStack stack) {
            this.stack = stack;
        }

        @Override
        public void pushPose() {
            stack.pushMatrix();
        }

        @Override
        public void popPose() {
            stack.popMatrix();
        }

        @Override
        public void translate(float x, float y, float z) {
            stack.translate(x, y, z);
        }

        @Override
        public void rotate(IQuaternionf quaternion) {
            stack.rotate(AbstractPoseStack.convertQuaternion(quaternion));
        }

        @Override
        public void scale(float x, float y, float z) {
            stack.scale(x, y, z);
        }

        @Override
        public void setIdentity() {
            stack.identity();
        }

        @Override
        public Pose last() {
            entry.pose().set(AbstractPoseStack.convertMatrix(stack));
            return entry;
        }
    }
}
