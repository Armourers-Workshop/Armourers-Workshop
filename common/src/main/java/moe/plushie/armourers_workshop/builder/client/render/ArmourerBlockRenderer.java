package moe.plushie.armourers_workshop.builder.client.render;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.client.guide.IGuideDataProvider;
import moe.plushie.armourers_workshop.api.client.guide.IGuideRenderer;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.guide.GuideRendererManager;
import moe.plushie.armourers_workshop.builder.other.CubeTransform;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinDynamicTexture;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.TextureUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ArmourerBlockRenderer<T extends ArmourerBlockEntity> extends AbstractBlockEntityRenderer<T> {

    private final PlayerTextureOverride override = new PlayerTextureOverride();
    private final Rectangle3f originBox = new Rectangle3f(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f);
    private final GuideRendererManager rendererManager = new GuideRendererManager();

    public ArmourerBlockRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        ISkinType skinType = entity.getSkinType();
        ISkinProperties skinProperties = entity.getSkinProperties();

        RenderData renderData = RenderData.of(entity);
        renderData.tick();

        // when the player has some special texture, we must override to renderer.
        ResourceLocation playerTexture = renderData.displayTextureLocation;
        if (playerTexture != null) {
            override.setTexture(playerTexture);
            override.setBuffers(bufferSource);
            bufferSource = override;
        }

        boolean isMultiBlocks = skinProperties.get(SkinProperty.BLOCK_MULTIBLOCK);
        boolean isShowGuides = entity.isShowGuides();
        boolean isShowModelGuides = entity.isShowModelGuides();
        boolean isShowHelper = entity.isShowHelper();
        boolean isUseHelper = entity.isUseHelper();

        // don't display overlay layers when helpers are actived.
        renderData.shouldRenderOverlay = !isUseHelper;
        renderData.skinProperties = skinProperties;

        poseStack.pushPose();
        transform(poseStack, entity);
        poseStack.scale(-1, -1, 1);

        float polygonOffset = 0f;
        for (ISkinPartType partType : skinType.getParts()) {
            IVector3i origin = partType.getOffset();
            IRectangle3i rect = partType.getBuildingSpace();
            IRectangle3i rect2 = partType.getGuideSpace();

            float r = 0.5f;
            float g = 0.5f;
            float b = 0.5f;
            float a = 0.25f;

            if (partType == SkinPartTypes.BLOCK_MULTI && !isMultiBlocks) {
                continue;
            }

            if (partType == SkinPartTypes.BLOCK && isMultiBlocks) {
                r = 1;
                g = 1;
                b = 0;
                a = 0.2f;
            }

            boolean isModelOverridden = entity.isModelOverridden(partType);
            if (isUseHelper) {
                isModelOverridden = !isShowHelper;
            }

            poseStack.pushPose();
            poseStack.translate(origin.getX(), origin.getY() + rect.getMinY(), origin.getZ());
            poseStack.translate(polygonOffset, polygonOffset, polygonOffset);

            // render guide model
            if (!isModelOverridden) {
                IGuideRenderer guideRenderer = rendererManager.getRenderer(partType);
                if (guideRenderer != null) {
                    poseStack.pushPose();
                    poseStack.translate(0, -rect2.getMinY(), 0);
                    poseStack.scale(16, 16, 16);
                    guideRenderer.render(poseStack, renderData, 0xf000f0, OverlayTexture.NO_OVERLAY, bufferSource);
                    poseStack.popPose();
                }
            }

            poseStack.scale(-1, -1, 1);

            // render building grid
            if (isShowGuides) {
                ShapeTesselator.cube(rect, r, g, b, a, poseStack, bufferSource);
                ShapeTesselator.cube(originBox, 0, 1, 0, 0.5f, poseStack, bufferSource);
            }
            // render guide grid
            if (isShowModelGuides && isModelOverridden) {
                ShapeTesselator.cube(rect2, 0, 0, 1, 0.25f, poseStack, bufferSource);
            }

            poseStack.popPose();
            polygonOffset += 0.001f;
        }
        poseStack.popPose();
        override.setBuffers(null);
    }

    public void transform(IPoseStack poseStack, T entity) {
        poseStack.translate(0, 1, 0); // apply height offset
        poseStack.rotate(CubeTransform.getRotationDegrees(entity.getFacing())); // apply facing rotation
    }

    @Override
    public int getViewDistance() {
        return 272;
    }

    @Override
    public boolean shouldRenderOffScreen(T entity) {
        return true;
    }

    public static class PlayerTextureOverride implements IBufferSource {

        protected final HashMap<RenderType, Supplier<RenderType>> overrides = new HashMap<>();
        protected ResourceLocation texture;
        protected IBufferSource bufferSource;

        public void setBuffers(IBufferSource bufferSource) {
            this.bufferSource = bufferSource;
        }

        public void setTexture(ResourceLocation texture) {
            if (Objects.equals(this.texture, texture)) {
                return;
            }
            this.overrides.clear();
            this.overrides.put(SkinRenderType.PLAYER_CUTOUT_NO_CULL, () -> SkinRenderType.entityCutoutNoCull(texture));
            this.overrides.put(SkinRenderType.PLAYER_CUTOUT, () -> SkinRenderType.entityCutoutNoCull(texture));
            this.overrides.put(SkinRenderType.PLAYER_TRANSLUCENT, () -> SkinRenderType.entityTranslucentCull(texture));
        }

        @Override
        public IVertexConsumer getBuffer(RenderType renderType) {
            Supplier<RenderType> overrideRenderType = overrides.get(renderType);
            if (overrideRenderType != null) {
                renderType = overrideRenderType.get();
            }
            return bufferSource.getBuffer(renderType);
        }

        @Override
        public void endBatch() {
            bufferSource.endBatch();
        }
    }

    public static class RenderData implements IGuideDataProvider {

        protected final ArmourerBlockEntity blockEntity;
        protected final SkinDynamicTexture displayTexture;
        protected final ResourceLocation displayTextureLocation;
        protected int lastVersion;
        protected boolean shouldRenderOverlay = false;
        protected ISkinProperties skinProperties;

        public RenderData(ArmourerBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
            this.displayTexture = new SkinDynamicTexture();
            this.displayTextureLocation = Minecraft.getInstance().getTextureManager().register(identifier(blockEntity), displayTexture);
        }

        public static RenderData of(ArmourerBlockEntity blockEntity) {
            Object renderData = blockEntity.getRenderData();
            if (renderData instanceof RenderData) {
                return (RenderData) renderData;
            }
            RenderData renderData1 = new RenderData(blockEntity);
            blockEntity.setRenderData(renderData1);
            return renderData1;
        }

        public static String identifier(ArmourerBlockEntity blockEntity) {
            BlockPos pos = blockEntity.getBlockPos();
            return String.format("aw-armourer-%d-%d-%d", pos.getX(), pos.getY(), pos.getZ());
        }

        // TODO: @SAGESSE replace to new impl.
//        @Override
//        protected void finalize() throws Throwable {
//            Minecraft.getInstance().getTextureManager().release(displayTextureLocation);
//            super.finalize();
//        }

        public void tick() {
            this.displayTexture.setRefer(TextureUtils.getPlayerTextureLocation(blockEntity.getTextureDescriptor()));
            this.displayTexture.setPaintData(blockEntity.getPaintData());
        }

        @Override
        public boolean shouldRenderOverlay(ISkinProperty<Boolean> property) {
            //  must check after the enable rendering.
            if (shouldRenderOverlay) {
                return !skinProperties.get(property);
            }
            return false;
        }
    }
}
