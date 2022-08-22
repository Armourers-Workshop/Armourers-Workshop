package moe.plushie.armourers_workshop.core.client.other;

import com.google.common.collect.Iterators;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ext.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;

@Environment(value = EnvType.CLIENT)
public class SkinRenderContext {

    private static final Iterator<SkinRenderContext> QUEUES = createInstances();

    public int light;
    public float partialTicks;
    public PoseStack poseStack;
    public final FixedPoseStack openPoseStack = new FixedPoseStack(null);
    public MultiBufferSource buffers;
    public ItemTransforms.TransformType transformType;

    public static Iterator<SkinRenderContext> createInstances() {
        ArrayList<SkinRenderContext> contexts = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            contexts.add(new SkinRenderContext());
        }
        return Iterators.cycle(contexts);
    }

    public static SkinRenderContext getInstance() {
        return QUEUES.next();
    }

    public void setup(int light, float partialTick, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffers) {
        this.light = light;
        this.partialTicks = partialTick;
        this.poseStack = poseStack;
        this.openPoseStack.matrixStack = poseStack;
        this.buffers = buffers;
        this.transformType = transformType;
    }

    public void setup(int light, float partialTick, PoseStack poseStack, MultiBufferSource buffers) {
        this.light = light;
        this.partialTicks = partialTick;
        this.poseStack = poseStack;
        this.openPoseStack.matrixStack = poseStack;
        this.buffers = buffers;
        this.transformType = ItemTransforms.TransformType.NONE;
    }

    public SkinRenderObjectBuilder getBuffer(@Nonnull Skin skin) {
        SkinVertexBufferBuilder bufferBuilder = SkinVertexBufferBuilder.getBuffer(buffers);
        return bufferBuilder.getBuffer(skin);
    }

    public static class FixedPoseStack extends OpenPoseStack {

        public PoseStack matrixStack;

        public FixedPoseStack(PoseStack matrixStack) {
            this.matrixStack = matrixStack;
        }

        @Override
        public void pushPose() {
            matrixStack.pushPose();
        }

        @Override
        public void popPose() {
            matrixStack.popPose();
        }

        @Override
        public void translate(float x, float y, float z) {
            matrixStack.translate(x, y, z);
        }

        @Override
        public void scale(float x, float y, float z) {
            matrixStack.scale(x, y, z);
        }

        @Override
        public void mulPose(Quaternion quaternion) {
            matrixStack.mulPose(quaternion);
        }

        @Override
        public void mulPose(Matrix4f matrix) {
        }

        @Override
        public void applyPose(Vector4f vector) {
            vector.transform(matrixStack.last().pose());
        }

        @Override
        public void applyNormal(Vector3f vector) {
            vector.transform(matrixStack.last().normal());
        }
    }

    public static OpenPoseStack createSystemPoseStack() {
        //#if MC >= 11800
        //# return new FixedPoseStack(RenderSystem.getModelViewStack());
        //#else
        return new OpenPoseStack() {
        public void pushPose() { RenderSystem.pushMatrix(); }
        public void popPose() { RenderSystem.popMatrix(); }
        public void translate(float x, float y, float z) { RenderSystem.translatef(x, y, z); }
        public void scale(float x, float y, float z) { RenderSystem.scalef(x, y, z); }
        public void mulPose(Quaternion quaternion) { mulPose(new Matrix4f(quaternion)); }
        public void mulPose(Matrix4f matrix) { RenderSystem.multMatrix(matrix); }
        public void applyPose(Vector4f vector) { }
        public void applyNormal(Vector3f vector) {}
        };
        //#endif
    }
}
