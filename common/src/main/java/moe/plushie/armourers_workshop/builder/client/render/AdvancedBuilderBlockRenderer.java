package moe.plushie.armourers_workshop.builder.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.guide.IGuideDataProvider;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.guide.GuideRendererManager;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderBufferSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
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
public class AdvancedBuilderBlockRenderer<T extends AdvancedBuilderBlockEntity> extends AbstractBlockEntityRenderer<T> {

    public static ArrayList<Vector3f> OUTPUTS = new ArrayList<>();
    public static HashSet<BakedSkinPart> RESULTS = new HashSet<>();

    private final GuideRendererManager rendererManager = new GuideRendererManager();


    public static AdvancedBuilderBlockRenderer<AdvancedBuilderBlockEntity> TEST;


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

    public AdvancedBuilderBlockRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (TEST != null) {
            TEST.render(entity, partialTicks, poseStack, buffers, light, overlay);
            return;
        }

//        ArmourerBlockEntityRenderer.RenderData renderData = ArmourerBlockEntityRenderer.RenderData.of(entity);
        IGuideDataProvider renderData = property -> true;

        poseStack.pushPose();
        poseStack.translate(entity.offset.getX(), entity.offset.getY(), entity.offset.getZ());
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(entity.carmeScale.getX(), entity.carmeScale.getY(), entity.carmeScale.getZ());

        poseStack.scale(-MathUtils.SCALE, -MathUtils.SCALE, MathUtils.SCALE);

        ShapeTesselator.vector(Vector3f.ZERO, 16, poseStack, buffers);
//        IGuideRenderer guideRenderer = rendererManager.getRenderer(SkinPartTypes.BIPPED_HEAD);
//        if (guideRenderer != null) {
//            poseStack.pushPose();
////            poseStack.translate(0, -rect2.getMinY(), 0);
//            poseStack.scale(16, 16, 16);
//            guideRenderer.render(poseStack, renderData, 0xf000f0, OverlayTexture.NO_OVERLAY, buffers);
//            poseStack.popPose();
//        }

        renderNode(entity.getDocument().getRoot(), 0, poseStack, buffers, light, overlay);

        poseStack.popPose();

        if (ModDebugger.advancedBuilder) {
            BlockState blockState = entity.getBlockState();
            BlockPos pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            ShapeTesselator.stroke(entity.getCustomRenderBoundingBox(blockState), UIColor.RED, poseStack, buffers);
            Vector3f origin = entity.getRenderOrigin();
            poseStack.translate(origin.getX(), origin.getY(), origin.getZ());
            ShapeTesselator.vector(Vector3f.ZERO, 1, poseStack, buffers);
            poseStack.translate(entity.carmeOffset.getX(), entity.carmeOffset.getY(), entity.carmeOffset.getZ());
//            poseStack.mulPose(new OpenQuaternionf(-entity.carmeRot.getX(), entity.carmeRot.getY(), entity.carmeRot.getZ(), true));
            ShapeTesselator.vector(Vector3f.ZERO, 1, poseStack, buffers);

            poseStack.popPose();
        }

//        renderOutput(entity, partialTicks, poseStack, buffers, light, overlay);
    }


    private void renderNode(SkinDocumentNode node, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        // when the node is disabled, it does not to rendering.
        if (!node.isEnabled()) {
            return;
        }
        poseStack.pushPose();

        Vector3f translate = node.getLocation();
        Vector3f rotation = node.getRotation();
        Vector3f scale = node.getScale();

        if (translate != Vector3f.ZERO) {
            poseStack.translate(translate.getX() * 16, translate.getY() * 16, translate.getZ() * 16);
        }
        if (rotation != Vector3f.ZERO) {
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation.getZ()));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation.getY()));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(rotation.getX()));
        }
        if (scale != Vector3f.ONE) {
            poseStack.mulPoseMatrix(OpenMatrix4f.createScaleMatrix(scale.getX(), scale.getY(), scale.getZ()));
            poseStack.mulNormalMatrix(OpenMatrix3f.createScaleMatrix(scale.getX(), scale.getY(), scale.getZ()));
        }
        if (node.isMirror()) {
            poseStack.mulPoseMatrix(OpenMatrix4f.createScaleMatrix(-1, 1, 1));
            poseStack.mulNormalMatrix(OpenMatrix3f.createScaleMatrix(-1, 1, 1));
        }

        SkinDescriptor descriptor = node.getSkin();
        SkinRenderTesselator tesselator = SkinRenderTesselator.create(descriptor, Tickets.RENDERER);
        if (tesselator != null) {
            tesselator.setLightmap(0xf000f0);
            tesselator.setPartialTicks(partialTicks);
            tesselator.setPartialTicks(0);
            tesselator.draw(poseStack, buffers);
        }
        for (SkinDocumentNode child : node.children()) {
            renderNode(child, partialTicks, poseStack, buffers, light, overlay);
        }
        poseStack.popPose();
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
//            RenderSystem.drawLine(poseStack, pt1.getX(), pt1.getY(), pt1.getZ(), pt2.getX(), pt2.getY(), pt2.getZ(), UIColor.YELLOW, buffers);
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
        public int addPart(BakedSkinPart bakedPart, BakedSkin bakedSkin, ColorScheme scheme, boolean shouldRender, SkinRenderContext context) {
            int total = 0;
            // note we will rebuild a new cache by overlay,
            // because we can't mix colors in the shader (1.16 + rendertype_entity_shadow).
            if (RESULTS.contains(bakedPart)) {
                context.setOverlay(0x38ffffff);
                total = builder.addPart(bakedPart, bakedSkin, scheme, shouldRender, context);
                context.setOverlay(0);
            }
            // when we rendered the highlighted version,
            // so we don't need to render original version,
            // but we still keep the original cache to next render.
            // and a special case when the highlighted version cache not compiled yet,
            // we still need to display the original cache.
            if (total != 0) {
                shouldRender = false;
            }
            return builder.addPart(bakedPart, bakedSkin, scheme, shouldRender, context);
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
        public void addShape(IJointTransform[] transforms, SkinRenderContext context) {
            builder.addShape(transforms, context);
        }
    }
}
