package moe.plushie.armourers_workshop.builder.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedAbstractGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedBackpackGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedBlockGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedBoatGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedHorseGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedHumanGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedItemGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedMinecartGuideRenderer;
import moe.plushie.armourers_workshop.compatibility.client.AbstractModelViewStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.model.SkinItemModelManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentType;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransforms;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class AdvancedBuilderBlockRenderer<T extends AdvancedBuilderBlockEntity> extends AbstractBlockEntityRenderer<T> {

    public static final float SCALE = 0.0625f; // 1 / 16f;

    private static final Map<SkinDocumentType, AdvancedAbstractGuideRenderer> GUIDES = Collections.immutableMap(it -> {
        it.put(SkinDocumentTypes.GENERAL_ARMOR_HEAD, new AdvancedHumanGuideRenderer());
        it.put(SkinDocumentTypes.GENERAL_ARMOR_CHEST, new AdvancedHumanGuideRenderer());
        it.put(SkinDocumentTypes.GENERAL_ARMOR_FEET, new AdvancedHumanGuideRenderer());
        it.put(SkinDocumentTypes.GENERAL_ARMOR_LEGS, new AdvancedHumanGuideRenderer());
        it.put(SkinDocumentTypes.GENERAL_ARMOR_WINGS, new AdvancedHumanGuideRenderer());
        it.put(SkinDocumentTypes.GENERAL_ARMOR_OUTFIT, new AdvancedHumanGuideRenderer());

        it.put(SkinDocumentTypes.ITEM, new AdvancedItemGuideRenderer());
        it.put(SkinDocumentTypes.ITEM_AXE, new AdvancedItemGuideRenderer());
        it.put(SkinDocumentTypes.ITEM_HOE, new AdvancedItemGuideRenderer());
        it.put(SkinDocumentTypes.ITEM_SHOVEL, new AdvancedItemGuideRenderer());
        it.put(SkinDocumentTypes.ITEM_PICKAXE, new AdvancedItemGuideRenderer());

        it.put(SkinDocumentTypes.ITEM_SWORD, new AdvancedItemGuideRenderer());
        it.put(SkinDocumentTypes.ITEM_SHIELD, new AdvancedItemGuideRenderer());
        it.put(SkinDocumentTypes.ITEM_BOW, new AdvancedItemGuideRenderer());
        it.put(SkinDocumentTypes.ITEM_TRIDENT, new AdvancedItemGuideRenderer());

        it.put(SkinDocumentTypes.ITEM_BACKPACK, new AdvancedBackpackGuideRenderer());

        it.put(SkinDocumentTypes.ENTITY_BOAT, new AdvancedBoatGuideRenderer());
        it.put(SkinDocumentTypes.ENTITY_MINECART, new AdvancedMinecartGuideRenderer());

        it.put(SkinDocumentTypes.ENTITY_HORSE, new AdvancedHorseGuideRenderer());

        it.put(SkinDocumentTypes.BLOCK, new AdvancedBlockGuideRenderer());
    });


    private static final Set<SkinType> USE_ITEM_TRANSFORMERS = Collections.immutableSet(it -> {
        it.add(SkinTypes.ITEM);
        it.add(SkinTypes.ITEM_AXE);
        it.add(SkinTypes.ITEM_HOE);
        it.add(SkinTypes.ITEM_SHOVEL);
        it.add(SkinTypes.ITEM_PICKAXE);
        it.add(SkinTypes.ITEM_SWORD);
        it.add(SkinTypes.ITEM_SHIELD);
        it.add(SkinTypes.ITEM_BOW);
        it.add(SkinTypes.ITEM_TRIDENT);
    });

    public static ArrayList<OpenVector3f> OUTPUTS = new ArrayList<>();
    public static HashSet<BakedSkinPart> RESULTS = new HashSet<>();

    public static void setOutput(int i, OpenVector3f pt) {
        while (i >= OUTPUTS.size()) {
            OUTPUTS.add(OpenVector3f.ZERO);
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
        poseStack.translate(entity.offset.x(), entity.offset.y(), entity.offset.z());
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(entity.carmeScale.x(), entity.carmeScale.y(), entity.carmeScale.z());

        poseStack.scale(-SCALE, -SCALE, SCALE);

        var document = entity.document();
        var settings = document.settings();

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
            ShapeTesselator.vector(OpenVector3f.ZERO, 16, poseStack, bufferSource);
            poseStack.scale(-1, -1, 1);
        }

        if (settings.showsHelperModel()) {
            var guideRenderer = GUIDES.get(document.type());
            if (guideRenderer != null) {
                guideRenderer.render(document, poseStack, light, overlay, bufferSource);
            }
        }

        if (settings.showsHelperModel() && document.get(SkinProperty.OVERRIDE_ENTITY_SIZE)) {
            var width = (float) (document.get(SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH) * 16.0);
            var height = (float) (document.get(SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT) * 16.0);
            var eyeHeight = (float) (document.get(SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT) * 16.0);
            var radius = (width / 2);
            ShapeTesselator.stroke(-radius, (24 - height), -radius, radius, 24, radius, UIColor.WHITE, poseStack, bufferSource);
            ShapeTesselator.stroke(-radius, (24 - eyeHeight), -radius, radius, (24 - eyeHeight), radius, UIColor.RED, poseStack, bufferSource);
        }

        // only item
        if (USE_ITEM_TRANSFORMERS.contains(document.type().skinType())) {
            applyTransform(poseStack, document.type().skinType(), document.itemTransforms());
        }


        var armature = BakedArmature.defaultBy(document.type().skinType());
        renderNode(document, document.root(), armature, 0, poseStack, bufferSource, light, overlay);

        poseStack.popPose();

        if (ModDebugger.advancedBuilder) {
            var blockState = entity.getBlockState();
            var pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            ShapeTesselator.stroke(entity.getVisibleBox(blockState), UIColor.RED, poseStack, bufferSource);
            var origin = entity.getRenderOrigin();
            poseStack.translate(origin.x(), origin.y(), origin.z());
            ShapeTesselator.vector(OpenVector3f.ZERO, 1, poseStack, bufferSource);
            poseStack.translate(entity.carmeOffset.x(), entity.carmeOffset.y(), entity.carmeOffset.z());
//            poseStack.mulPose(new OpenQuaternionf(-entity.carmeRot.getX(), entity.carmeRot.getY(), entity.carmeRot.getZ(), true));
            ShapeTesselator.vector(OpenVector3f.ZERO, 1, poseStack, bufferSource);

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
            var transform = armature.transformByType(node.type());
            if (transform != null) {
                transform.apply(poseStack);
            }
        }

        // apply node transform.
        node.transform().apply(poseStack);

        if (node.isLocator()) {
            poseStack.scale(-1, -1, 1);
            ShapeTesselator.vector(OpenVector3f.ZERO, 16, poseStack, bufferSource);
            poseStack.scale(-1, -1, 1);
        }

        var descriptor = node.skin();
        var tesselator = SkinRenderTesselator.create(descriptor, Tickets.RENDERER);
        if (tesselator != null) {
            tesselator.setLightmap(0xf000f0);
            tesselator.setPartialTicks(partialTicks);
            tesselator.setAnimationTicks(0);

            tesselator.setPoseStack(poseStack);
            tesselator.setBufferSource(bufferSource);
            tesselator.setModelViewStack(AbstractModelViewStack.getInstance());

            tesselator.draw();
        }
        for (var child : node.children()) {
            renderNode(document, child, armature, partialTicks, poseStack, bufferSource, light, overlay);
        }
        poseStack.popPose();
    }


    public void renderOutput(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        var pos = entity.getBlockPos();
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

    protected void applyTransform(IPoseStack poseStack, SkinType skinType, OpenItemTransforms itemTransforms) {
        var displayContext = OpenItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        if (itemTransforms != null) {
            var itemTransform = itemTransforms.get(displayContext);
            if (itemTransform != null) {
                poseStack.translate(0, -2, -2);
                itemTransform.apply(poseStack);
            }
        } else {
            poseStack.translate(0, -2, -2);
            //var entity = PlaceholderManager.MANNEQUIN.get();
            var model = SkinItemModelManager.getInstance().getModel(skinType);
            model.getTransform(displayContext).apply(false, poseStack);
        }
    }

    @Override
    public int getViewDistance() {
        return 272;
    }

    @Override
    public boolean shouldRenderOffScreen(T entity) {
        return true;
    }
}
