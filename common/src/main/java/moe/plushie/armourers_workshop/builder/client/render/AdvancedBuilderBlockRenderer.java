package moe.plushie.armourers_workshop.builder.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedAbstractGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedBackpackGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedBlockGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedBoatGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedHorseGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedHumanGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedItemGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide.AdvancedMinecartGuideRenderer;
import moe.plushie.armourers_workshop.builder.client.render.state.AdvancedBuilderRenderState;
import moe.plushie.armourers_workshop.builder.other.CubeTransform;
import moe.plushie.armourers_workshop.compat.client.renderer.blockentity.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.EntitySlot;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.render.model.SkinItemModelManager;
import moe.plushie.armourers_workshop.core.client.render.state.SkinRenderState;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.data.ticket.TicketManager;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentType;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransforms;
import moe.plushie.armourers_workshop.init.ModDebugger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class AdvancedBuilderBlockRenderer<T extends AdvancedBuilderBlockEntity, S extends AdvancedBuilderRenderState> extends AbstractBlockEntityRenderer<T, S> {

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
    protected int abi$getViewDistance() {
        return 272;
    }

    @Override
    protected boolean abi$shouldRenderOffScreen() {
        return true;
    }

    @Override
    protected void abi$render(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        var offset = renderState.offset();
        var carmeOffset = renderState.carmeOffset();
        var carmeRot = renderState.carmeRot();
        var carmeScale = renderState.carmeScale();

        context.saveGraphicsState();
        context.translateCTM(offset);
        context.translateCTM(0.5f, 0.5f, 0.5f);
        context.rotateCTM(CubeTransform.getFacingRotation(renderState.facing())); // apply facing rotation
        context.scaleCTM(carmeScale);
        context.scaleCTM(-0.0625f, -0.0625f, 0.0625f);

        var document = renderState.document();
        var settings = document.settings();

        if (settings.showsOrigin()) {
            context.scaleCTM(-1, -1, 1);
            context.draw(ShapeElement.arrow(OpenVector3f.ZERO, 16));
            context.scaleCTM(-1, -1, 1);
        }

        if (settings.showsHelperModel()) {
            var guideRenderer = GUIDES.get(document.type());
            if (guideRenderer != null) {
                guideRenderer.render(document, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, context);
            }
        }

        if (settings.showsHelperModel() && document.get(SkinProperty.OVERRIDE_ENTITY_SIZE)) {
            var width = (float) (document.get(SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH) * 16.0);
            var height = (float) (document.get(SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT) * 16.0);
            var eyeHeight = (float) (document.get(SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT) * 16.0);
            var radius = (width / 2);
            context.draw(ShapeElement.stroke(-radius, (24 - height), -radius, width, height, width, Colors.WHITE));
            context.draw(ShapeElement.stroke(-radius, (24 - eyeHeight), -radius, width, 0, width, Colors.RED));
        }

        // only item
        if (USE_ITEM_TRANSFORMERS.contains(document.type().skinType())) {
            applyTransform(document.type().skinType(), document.itemTransforms(), context);
        }

        var armature = BakedArmature.defaultBy(document.type().skinType());
        renderNode(document.root(), armature, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, document, context);

        context.restoreGraphicsState();

        if (ModDebugger.advancedBuilder) {
            var pos = renderState.blockPos();
            context.saveGraphicsState();
            context.translateCTM(-pos.getX(), -pos.getY(), -pos.getZ());
            context.draw(ShapeElement.stroke(renderState.visibleBox(), Colors.RED));
            var origin = renderState.renderOrigin();
            context.translateCTM(origin);
            context.draw(ShapeElement.arrow(OpenVector3f.ZERO, 1));
            context.translateCTM(carmeOffset);
            //context.rotateCTM(new OpenQuaternionf(-carmeRot.x(), carmeRot.y(), carmeRot.z(), true));
            context.draw(ShapeElement.arrow(OpenVector3f.ZERO, 1));
            context.restoreGraphicsState();
        }

        renderOutput(renderState, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, context);
    }


    protected void renderNode(SkinDocumentNode node, BakedArmature armature, int lightmap, int overlay, SkinDocument document, IGraphicsContext context) {
        // when the node is disabled, it does not to rendering.
        if (!node.isEnabled()) {
            return;
        }
        context.saveGraphicsState();

        // apply joint transform.
        if (armature != null && node.isLocked()) {
            var transform = armature.transformByType(node.type());
            if (transform != null) {
                transform.apply(context.ctm());
            }
        }

        // apply node transform.
        node.transform().apply(context.ctm());

        if (node.isLocator()) {
            context.scaleCTM(-1, -1, 1);
            context.draw(ShapeElement.arrow(OpenVector3f.ZERO, 16));
            context.scaleCTM(-1, -1, 1);
        }

        renderNodeSkin(node, lightmap, overlay, context);
        for (var child : node.children()) {
            renderNode(child, armature, lightmap, overlay, document, context);
        }
        context.restoreGraphicsState();
    }

    protected void renderNodeSkin(SkinDocumentNode node, int lightmap, int overlay, IGraphicsContext context) {
        var model = loadSkin(node.skin());
        if (model.isEmpty()) {
            return;
        }
        model.setPartialTick(1.0f);
        model.setAnimationTick(0.0);
        model.setAnimationManager(AnimationManager.NONE);
        model.setOutlineColor(0);

        model.render(null, null, lightmap, overlay, context);
    }

    public void renderOutput(S renderState, int light, int overlay, IGraphicsContext context) {
//        var pos = renderState.blockPos();
//        context.saveGraphicsState();
//        context.translateCTM((float) (-pos.getX()), (float) (-pos.getY()), (float) (-pos.getZ()));
//        for (Vector3f v : OUTPUTS) {
//            RenderSystem.drawPoint(poseStack, v, 1.0F, buffers);
//        }
//        if (OUTPUTS.size() >= 2) {
//            Vector3f pt1 = OUTPUTS.get(0);
//            Vector3f pt2 = OUTPUTS.get(1);
//            Vector3f pt3 = OUTPUTS.get(2);
//            RenderSystem.drawLine(poseStack, pt1.getX(), pt1.getY(), pt1.getZ(), pt2.getX(), pt2.getY(), pt2.getZ(), UIColor.YELLOW, buffers);
//            drawLine(pose, pt2.getX(), pt2.getY(), pt2.getZ(), pt3.getX(), pt3.getY(), pt3.getZ(), UIColor.MAGENTA, builder);
//        }
//        context.restoreGraphicsState();
    }

    protected void applyTransform(SkinType skinType, OpenItemTransforms itemTransforms, IGraphicsContext context) {
        var displayContext = OpenItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        if (itemTransforms != null) {
            var itemTransform = itemTransforms.get(displayContext);
            if (itemTransform != null) {
                context.translateCTM(0, -2, -2);
                itemTransform.apply(context.ctm());
            }
        } else {
            context.translateCTM(0, -2, -2);
            //var entity = PlaceholderManager.MANNEQUIN.get();
            var model = SkinItemModelManager.getInstance().getModel(skinType);
            model.getTransform(displayContext).apply(false, context.ctm());
        }
    }

    private SkinRenderState loadSkin(SkinDescriptor descriptor) {
        var slots = new SkinRenderState();
        var skin = SkinBakery.getInstance().loadSkin(TicketManager.RENDERER.get(descriptor));
        if (skin != null) {
            var slot = new EntitySlot(skin, SkinPaintScheme.EMPTY, descriptor.sharedItemStack(), descriptor);
            slots.prepare(Collections.newList(slot));
        }
        return slots;
    }
}
