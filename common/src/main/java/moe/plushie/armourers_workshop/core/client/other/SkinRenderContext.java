package moe.plushie.armourers_workshop.core.client.other;

import com.google.common.collect.Iterators;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.skin.Skin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

@Environment(value = EnvType.CLIENT)
public class SkinRenderContext {

    private static final Iterator<SkinRenderContext> QUEUES = createInstances();

    public int light;
    public float partialTicks;
    public IPoseStack poseStack;
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

    public void setup(int light, float partialTick, ItemTransforms.TransformType transformType, IPoseStack poseStack, MultiBufferSource buffers) {
        this.light = light;
        this.partialTicks = partialTick;
        this.poseStack = poseStack;
        this.buffers = buffers;
        this.transformType = transformType;
    }

    public void setup(int light, float partialTick, IPoseStack poseStack, MultiBufferSource buffers) {
        this.light = light;
        this.partialTicks = partialTick;
        this.poseStack = poseStack;
        this.buffers = buffers;
        this.transformType = ItemTransforms.TransformType.NONE;
    }

    public void clean() {
    }

    public SkinRenderObjectBuilder getBuffer(@NotNull Skin skin) {
        SkinVertexBufferBuilder bufferBuilder = SkinVertexBufferBuilder.getBuffer(buffers);
        return bufferBuilder.getBuffer(skin);
    }

//    public static class FixedPoseStack extends IPoseStack {
//
//        public PoseStack matrixStack;
//
//        public FixedPoseStack(PoseStack matrixStack) {
//            this.matrixStack = matrixStack;
//        }
//
//        @Override
//        public void pushPose() {
//            matrixStack.pushPose();
//        }
//
//        @Override
//        public void popPose() {
//            matrixStack.popPose();
//        }
//
//        @Override
//        public void translate(float x, float y, float z) {
//            matrixStack.translate(x, y, z);
//        }
//
//        @Override
//        public void scale(float x, float y, float z) {
//            matrixStack.scale(x, y, z);
//        }
//
//        @Override
//        public void mulPose(OpenQuaternionf quaternion) {
//            matrixStack.mulPose(quaternion);
//        }
//
//        @Override
//        public void mulPose(OpenMatrix4f matrix) {
//        }
//
//        @Override
//        public void applyPose(Vector4f vector) {
//            vector.transform(matrixStack.last().pose());
//        }
//
//        @Override
//        public void applyNormal(Vector3f vector) {
//            vector.transform(matrixStack.last().normal());
//        }
//    }
}
