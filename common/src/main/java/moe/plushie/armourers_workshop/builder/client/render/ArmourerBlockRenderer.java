package moe.plushie.armourers_workshop.builder.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.guide.GuideRendererManager;
import moe.plushie.armourers_workshop.builder.client.render.state.ArmourerRenderState;
import moe.plushie.armourers_workshop.builder.other.CubeTransform;
import moe.plushie.armourers_workshop.compat.client.renderer.blockentity.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.element.ModelPartElement;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

import java.util.HashMap;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ArmourerBlockRenderer<T extends ArmourerBlockEntity, S extends ArmourerRenderState> extends AbstractBlockEntityRenderer<T, S> {

    private final TextureResolvedContext textureResolvedContext = new TextureResolvedContext();
    private final OpenRectangle3f originBox = new OpenRectangle3f(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f);
    private final GuideRendererManager rendererManager = new GuideRendererManager();

    public ArmourerBlockRenderer(Context context) {
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
        var textureRenderState = renderState.textureRenderState();
        if (textureRenderState == null) {
            return;
        }
        var type = renderState.skinType();
        var properties = renderState.skinProperties();
        var textureModel = renderState.textureDescriptor().model();

        // when the player has some special texture, we must textureResolvedContext to renderer.
        var playerTexture = textureRenderState.location();
        if (playerTexture != null) {
            textureResolvedContext.setTexture(playerTexture);
            textureResolvedContext.setContext(context);
            context = textureResolvedContext;
        }

        var isMultiBlocks = properties.get(SkinProperty.BLOCK_MULTIBLOCK);
        var isShowGuides = renderState.isShowGuides();
        var isShowModelGuides = renderState.isShowModelGuides();
        var isShowHelper = renderState.isShowHelper();
        var isUseHelper = renderState.isUseHelper();

        // don't display overlay layers when helpers are actived.
        textureRenderState.setRenderOverlay(!isUseHelper);

        context.saveGraphicsState();

        context.translateCTM(0, 1, 0); // apply height offset
        context.rotateCTM(CubeTransform.getFacingRotation(renderState.facing())); // apply facing rotation
        context.scaleCTM(-1, -1, 1);

        var polygonOffset = 0f;
        for (var partType : type.parts()) {
            var origin = partType.offset();
            var rect = partType.buildingSpace();
            var rect2 = partType.guideSpaceByModel(textureModel);

            if (partType == SkinPartTypes.BLOCK_MULTI && !isMultiBlocks) {
                continue;
            }

            var color = 0x3f7f7f7f;
            if (partType == SkinPartTypes.BLOCK && isMultiBlocks) {
                color = 0x33ffff00;
            }

            var isModelOverridden = renderState.isModelOverridden(partType);
            if (isUseHelper) {
                isModelOverridden = !isShowHelper;
            }

            context.saveGraphicsState();
            context.translateCTM(origin.x(), origin.y() + rect.minY(), origin.z());
            context.translateCTM(polygonOffset, polygonOffset, polygonOffset);

            // render guide model
            if (!isModelOverridden) {
                var guideRenderer = rendererManager.getRenderer(textureModel, partType);
                if (guideRenderer != null) {
                    context.saveGraphicsState();
                    context.translateCTM(0, -rect2.minY(), 0);
                    context.scaleCTM(16, 16, 16);
                    guideRenderer.render(textureRenderState, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, context);
                    context.restoreGraphicsState();
                }
            }

            context.scaleCTM(-1, -1, 1);

            // render building grid
            if (isShowGuides) {
                context.draw(ShapeElement.guide(rect, color));
                context.draw(ShapeElement.guide(originBox, 0x7f00ff00));
            }
            // render guide grid
            if (isShowModelGuides && isModelOverridden) {
                context.draw(ShapeElement.guide(rect2, 0x3f0000ff));
            }

            context.restoreGraphicsState();
            polygonOffset += 0.001f;
        }
        context.restoreGraphicsState();
        textureResolvedContext.setContext(null);
    }

    private static class TextureResolvedContext implements IGraphicsContext {

        private static final HashMap<String, IRenderType> REUSABLE_TYPES = new HashMap<>();

        protected final HashMap<IRenderType, Supplier<IRenderType>> overrides = new HashMap<>();

        protected OpenResourceLocation texture;
        protected IGraphicsContext context;

        @Override
        public void draw(IGraphicsElement element) {
            context.draw(resolve(element));
        }

        @Override
        public IPoseStack ctm() {
            return context.ctm();
        }

        public void setTexture(OpenResourceLocation texture) {
            if (Objects.equals(this.texture, texture)) {
                return;
            }
            this.texture = texture;
            this.overrides.clear();
            this.overrides.put(SkinRenderType.PLAYER_CUTOUT_NO_CULL, () -> entityCutoutNoCull(texture));
            this.overrides.put(SkinRenderType.PLAYER_CUTOUT, () -> entityCutoutNoCull(texture));
            this.overrides.put(SkinRenderType.PLAYER_TRANSLUCENT, () -> entityTranslucentCull(texture));
        }

        public void setContext(IGraphicsContext context) {
            this.context = context;
        }

        protected IGraphicsElement resolve(IGraphicsElement element) {
            var modelPart = Objects.safeCast(element, ModelPartElement.class);
            if (modelPart != null) {
                var renderType = overrides.get(modelPart.renderType());
                if (renderType != null) {
                    modelPart.setRenderType(renderType.get());
                }
            }
            return element;
        }

        protected IRenderType entityCutoutNoCull(OpenResourceLocation texture) {
            return REUSABLE_TYPES.computeIfAbsent("entity-cutout-no-cull-" + texture, it -> SkinRenderType.entityCutoutNoCull(texture));
        }

        protected IRenderType entityTranslucentCull(OpenResourceLocation texture) {
            return REUSABLE_TYPES.computeIfAbsent("entity-translucent-cull-" + texture, it -> SkinRenderType.entityTranslucentCull(texture));
        }
    }
}
