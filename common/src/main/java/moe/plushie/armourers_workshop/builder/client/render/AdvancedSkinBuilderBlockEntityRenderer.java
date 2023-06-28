package moe.plushie.armourers_workshop.builder.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedSkinBuilderBlockEntity;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderBufferSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Environment(EnvType.CLIENT)
public class AdvancedSkinBuilderBlockEntityRenderer<T extends AdvancedSkinBuilderBlockEntity> extends AbstractBlockEntityRenderer<T> {

    public static ArrayList<Vector3f> OUTPUTS = new ArrayList<>();
    public static HashSet<BakedSkinPart> RESULTS = new HashSet<>();

    public static void setOutput(int i, Vector3f pt) {
        while (i >= OUTPUTS.size()) {
            OUTPUTS.add(Vector3f.ZERO);
        }
        OUTPUTS.set(i, pt);
    }

    public static void setResult(Collection<BakedSkinPart> results) {
        RESULTS.clear();
        RESULTS.addAll(results);
    }

    public AdvancedSkinBuilderBlockEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        SkinRenderTesselator tesselator = SkinRenderTesselator.create(entity.descriptor, Tickets.RENDERER);
        if (tesselator == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(entity.offset.getX(), entity.offset.getY(), entity.offset.getZ());
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(entity.scale, entity.scale, entity.scale);

        poseStack.scale(-MathUtils.SCALE, -MathUtils.SCALE, MathUtils.SCALE);

        tesselator.setLightmap(0xf000f0);
        //tesselator.setPartialTicks(TickUtils.ticks());
        tesselator.setPartialTicks(0);
        tesselator.setOutlineBuffers(OutlineObjectBuilder.immediate(buffers));

        tesselator.draw(poseStack, buffers);

        poseStack.popPose();

        if (ModDebugger.advancedSkinBuilderBlock) {
            BlockState blockState = entity.getBlockState();
            BlockPos pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            RenderSystem.drawBoundingBox(poseStack, entity.getCustomRenderBoundingBox(blockState), UIColor.RED, buffers);
            RenderSystem.drawPoint(poseStack, entity.getRenderOrigin(), 1, buffers);

            poseStack.translate(entity.carmeOffset.getX(), entity.carmeOffset.getY(), entity.carmeOffset.getZ());
//            poseStack.mulPose(new OpenQuaternionf(-entity.carmeRot.getX(), entity.carmeRot.getY(), entity.carmeRot.getZ(), true));
//            RenderSystem.drawPoint(poseStack, Vector3f.ZERO, 1, buffers);

            poseStack.popPose();
        }

        renderOutput(entity, partialTicks, poseStack, buffers, light, overlay);
    }

    public void renderOutput(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        BlockPos pos = entity.getBlockPos();
        poseStack.pushPose();
        poseStack.translate((float) (-pos.getX()), (float) (-pos.getY()), (float) (-pos.getZ()));
//        for (Vector3f v : OUTPUTS) {
//            RenderSystem.drawPoint(poseStack, v, 1.0F, buffers);
//        }
        if (OUTPUTS.size() >= 2) {
            Vector3f pt1 = OUTPUTS.get(0);
            Vector3f pt2 = OUTPUTS.get(1);
//            Vector3f pt3 = OUTPUTS.get(2);
            RenderSystem.drawLine(poseStack, pt1.getX(), pt1.getY(), pt1.getZ(), pt2.getX(), pt2.getY(), pt2.getZ(), UIColor.YELLOW, buffers);
//            drawLine(pose, pt2.getX(), pt2.getY(), pt2.getZ(), pt3.getX(), pt3.getY(), pt3.getZ(), UIColor.MAGENTA, builder);
        }

        poseStack.popPose();

    }



    @Override
    public int getViewDistance() {
        return 272;
    }

    public static class OutlineObjectBuilder implements SkinRenderBufferSource.ObjectBuilder {

        private final SkinRenderBufferSource.ObjectBuilder builder;

        public OutlineObjectBuilder(SkinRenderBufferSource.ObjectBuilder builder) {
            this.builder = builder;
        }

        public static SkinRenderBufferSource immediate(MultiBufferSource buffers) {
            SkinRenderBufferSource source = SkinRenderBufferSource.immediate(buffers);
            return skin -> new OutlineObjectBuilder(source.getBuffer(skin));
        }

        @Override
        public void addPart(BakedSkinPart bakedPart, BakedSkin bakedSkin, ColorScheme scheme, boolean shouldRender, SkinRenderContext context) {
            builder.addPart(bakedPart, bakedSkin, scheme, shouldRender, context);

            if (RESULTS.contains(bakedPart)) {
                builder.addShape(bakedPart.getRenderShape(), UIColor.GREEN, context);
            } else {
                builder.addShape(bakedPart.getRenderShape(), UIColor.ORANGE, context);
            }
        }

        @Override
        public void addShape(Vector3f origin, SkinRenderContext context) {
            builder.addShape(origin, context);
        }

        @Override
        public void addShape(OpenVoxelShape shape, UIColor color, SkinRenderContext context) {
            builder.addShape(shape, color, context);
        }

        @Override
        public void addArmatureShape(ITransformf[] transforms, SkinRenderContext context) {
            builder.addArmatureShape(transforms, context);
        }
    }
}
