package moe.plushie.armourers_workshop.builder.client.render;

import com.apple.library.uikit.UIColor;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AbstractAdvancedGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedBlockGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedBoatGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedHorseGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedHumanGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedItemGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedMinecartGuideRenderer;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinModelManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderBufferSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentSettings;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentType;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class AdvancedBuilderBlockRenderer<T extends AdvancedBuilderBlockEntity> extends AbstractBlockEntityRenderer<T> {

    private static final ImmutableMap<SkinDocumentType, AbstractAdvancedGuideRenderer> GUIDES = ImmutableMap.<SkinDocumentType, AbstractAdvancedGuideRenderer>builder()

            .put(SkinDocumentTypes.GENERAL_ARMOR_HEAD, new AdvancedHumanGuideRenderer())
            .put(SkinDocumentTypes.GENERAL_ARMOR_CHEST, new AdvancedHumanGuideRenderer())
            .put(SkinDocumentTypes.GENERAL_ARMOR_FEET, new AdvancedHumanGuideRenderer())
            .put(SkinDocumentTypes.GENERAL_ARMOR_LEGS, new AdvancedHumanGuideRenderer())
            .put(SkinDocumentTypes.GENERAL_ARMOR_WINGS, new AdvancedHumanGuideRenderer())
            .put(SkinDocumentTypes.GENERAL_ARMOR_OUTFIT, new AdvancedHumanGuideRenderer())

            .put(SkinDocumentTypes.ITEM, new AdvancedItemGuideRenderer())
            .put(SkinDocumentTypes.ITEM_AXE, new AdvancedItemGuideRenderer())
            .put(SkinDocumentTypes.ITEM_HOE, new AdvancedItemGuideRenderer())
            .put(SkinDocumentTypes.ITEM_SHOVEL, new AdvancedItemGuideRenderer())
            .put(SkinDocumentTypes.ITEM_PICKAXE, new AdvancedItemGuideRenderer())

            .put(SkinDocumentTypes.ITEM_SWORD, new AdvancedItemGuideRenderer())
            .put(SkinDocumentTypes.ITEM_SHIELD, new AdvancedItemGuideRenderer())
            .put(SkinDocumentTypes.ITEM_BOW, new AdvancedItemGuideRenderer())
            .put(SkinDocumentTypes.ITEM_TRIDENT, new AdvancedItemGuideRenderer())

            .put(SkinDocumentTypes.ITEM_BOAT, new AdvancedBoatGuideRenderer())
            .put(SkinDocumentTypes.ITEM_MINECART, new AdvancedMinecartGuideRenderer())

            .put(SkinDocumentTypes.ENTITY_HORSE, new AdvancedHorseGuideRenderer())

            .put(SkinDocumentTypes.BLOCK, new AdvancedBlockGuideRenderer())

            .build();


    ImmutableSet<ISkinPartType> USE_ITEM_TRANSFORMERS = ImmutableSet.<ISkinPartType>builder()
            .add(SkinPartTypes.ITEM)
            .add(SkinPartTypes.ITEM_AXE)
            .add(SkinPartTypes.ITEM_HOE)
            .add(SkinPartTypes.ITEM_SHOVEL)
            .add(SkinPartTypes.ITEM_PICKAXE)
            .add(SkinPartTypes.ITEM_SWORD)
            .add(SkinPartTypes.ITEM_SHIELD)
            .add(SkinPartTypes.ITEM_BOW0)
            .add(SkinPartTypes.ITEM_BOW1)
            .add(SkinPartTypes.ITEM_BOW2)
            .add(SkinPartTypes.ITEM_BOW3)
            .add(SkinPartTypes.ITEM_TRIDENT)
            .build();

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

    public AdvancedBuilderBlockRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        poseStack.pushPose();
        poseStack.translate(entity.offset.getX(), entity.offset.getY(), entity.offset.getZ());
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(entity.carmeScale.getX(), entity.carmeScale.getY(), entity.carmeScale.getZ());

        poseStack.scale(-MathUtils.SCALE, -MathUtils.SCALE, MathUtils.SCALE);

        SkinDocument document = entity.getDocument();
        SkinDocumentSettings settings = document.getSettings();

//        IGuideRenderer guideRenderer = rendererManager.getRenderer(SkinPartTypes.BIPPED_HEAD);
//        if (guideRenderer != null) {
//            poseStack.pushPose();
////            poseStack.translate(0, -rect2.getMinY(), 0);
//            poseStack.scale(16, 16, 16);
//            guideRenderer.render(poseStack, renderData, 0xf000f0, OverlayTexture.NO_OVERLAY, buffers);
//            poseStack.popPose();
//        }

        if (settings.showsOrigin()) {
            poseStack.scale(-1, -1, 1);
            ShapeTesselator.vector(Vector3f.ZERO, 16, poseStack, bufferSource);
            poseStack.scale(-1, -1, 1);
        }

        if (settings.showsHelperModel()) {
            AbstractAdvancedGuideRenderer guideRenderer = GUIDES.get(document.getType());
            if (guideRenderer != null) {
                guideRenderer.render(document, poseStack, light, overlay, bufferSource);
            }
        }

        BakedArmature armature = BakedArmature.defaultBy(document.getType().getSkinType());
        renderNode(document, document.getRoot(), armature, 0, poseStack, bufferSource, light, overlay);

        poseStack.popPose();

        if (ModDebugger.advancedBuilder) {
            BlockState blockState = entity.getBlockState();
            BlockPos pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            ShapeTesselator.stroke(entity.getRenderBoundingBox(blockState), UIColor.RED, poseStack, bufferSource);
            Vector3f origin = entity.getRenderOrigin();
            poseStack.translate(origin.getX(), origin.getY(), origin.getZ());
            ShapeTesselator.vector(Vector3f.ZERO, 1, poseStack, bufferSource);
            poseStack.translate(entity.carmeOffset.getX(), entity.carmeOffset.getY(), entity.carmeOffset.getZ());
//            poseStack.mulPose(new OpenQuaternionf(-entity.carmeRot.getX(), entity.carmeRot.getY(), entity.carmeRot.getZ(), true));
            ShapeTesselator.vector(Vector3f.ZERO, 1, poseStack, bufferSource);

            poseStack.popPose();
        }

//        renderOutput(entity, partialTicks, poseStack, buffers, light, overlay);
    }


    protected void renderNode(SkinDocument document, SkinDocumentNode node, BakedArmature armature, float partialTicks, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        // when the node is disabled, it does not to rendering.
        if (!node.isEnabled()) {
            return;
        }
        poseStack.pushPose();

        // apply joint transform.
        if (armature != null && node.isLocked()) {
            IJointTransform transform = armature.getTransform(node.getType());
            if (transform != null) {
                transform.apply(poseStack);
            }
        }

        // only item
        if (node.isLocked() && USE_ITEM_TRANSFORMERS.contains(node.getType())) {
            applyTransform(poseStack, document.getRoot(), document.getItemTransforms());
        }

        // apply node transform.
        node.getTransform().apply(poseStack);

        if (node.isLocator()) {
            poseStack.scale(-1, -1, 1);
            ShapeTesselator.vector(Vector3f.ZERO, 16, poseStack, bufferSource);
            poseStack.scale(-1, -1, 1);
        }

        if (node.isMirror()) {
            poseStack.scale(-1, 1, 1);
        }

        SkinDescriptor descriptor = node.getSkin();
        SkinRenderTesselator tesselator = SkinRenderTesselator.create(descriptor, Tickets.RENDERER);
        if (tesselator != null) {
            tesselator.setLightmap(0xf000f0);
            tesselator.setPartialTicks(partialTicks);
            tesselator.setAnimationTicks(0);
            tesselator.draw(poseStack, bufferSource);
        }
        for (SkinDocumentNode child : node.children()) {
            renderNode(document, child, armature, partialTicks, poseStack, bufferSource, light, overlay);
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
//            Vector3f pt1 = OUTPUTS.get(0);
//            Vector3f pt2 = OUTPUTS.get(1);
//            Vector3f pt3 = OUTPUTS.get(2);
//            RenderSystem.drawLine(poseStack, pt1.getX(), pt1.getY(), pt1.getZ(), pt2.getX(), pt2.getY(), pt2.getZ(), UIColor.YELLOW, buffers);
//            drawLine(pose, pt2.getX(), pt2.getY(), pt2.getZ(), pt3.getX(), pt3.getY(), pt3.getZ(), UIColor.MAGENTA, builder);
        }

        poseStack.popPose();
    }

    protected void applyTransform(IPoseStack poseStack, SkinDocumentNode node, SkinItemTransforms itemTransforms) {
        if (itemTransforms != null) {
            ITransformf itemTransform = itemTransforms.get(AbstractItemTransformType.THIRD_PERSON_RIGHT_HAND);
            if (itemTransform != null) {
                poseStack.translate(0, -2, -2);
                itemTransform.apply(poseStack);
            }
        } else {
            poseStack.translate(0, -2, -2);
            auto entity = PlaceholderManager.MANNEQUIN.get();
            auto model = SkinModelManager.getInstance().getModel(node.getType(), null, ItemStack.EMPTY, entity);
            float f1 = 16f;
            float f2 = 1 / 16f;
            poseStack.scale(f1, f1, f1);
            model.applyTransform(poseStack, false, AbstractItemTransformType.THIRD_PERSON_RIGHT_HAND);
            poseStack.scale(f2, f2, f2);
        }
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
        public void addShape(BakedArmature armature, SkinRenderContext context) {
            builder.addShape(armature, context);
        }
    }
}
