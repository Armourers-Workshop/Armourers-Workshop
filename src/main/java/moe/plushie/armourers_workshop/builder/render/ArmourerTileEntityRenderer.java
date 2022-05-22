package moe.plushie.armourers_workshop.builder.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.api.client.render.IGuideProvider;
import moe.plushie.armourers_workshop.api.client.render.IGuideRenderer;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.builder.render.guide.*;
import moe.plushie.armourers_workshop.builder.tileentity.ArmourerTileEntity;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.render.other.SkinDynamicTexture;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.utils.Rectangle3f;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TextureUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class ArmourerTileEntityRenderer<T extends ArmourerTileEntity> extends TileEntityRenderer<T> {

    private final Rectangle3f originBox = new Rectangle3f(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f);

    private final ImmutableMap<ISkinPartType, IGuideRenderer> guideRenderers = ImmutableMap.<ISkinPartType, IGuideRenderer>builder()
            .put(SkinPartTypes.BIPED_HEAD, HeadGuideRenderer.getInstance())
            .put(SkinPartTypes.BIPED_CHEST, ChestGuideRenderer.getInstance())
            .put(SkinPartTypes.BIPED_LEFT_ARM, ChestGuideRenderer.getInstance()::renderLeftArm)
            .put(SkinPartTypes.BIPED_RIGHT_ARM, ChestGuideRenderer.getInstance()::renderRightArm)
            .put(SkinPartTypes.BIPED_SKIRT, FeetGuideRenderer.getInstance())
            .put(SkinPartTypes.BIPED_LEFT_LEG, FeetGuideRenderer.getInstance()::renderLeftLeg)
            .put(SkinPartTypes.BIPED_RIGHT_LEG, FeetGuideRenderer.getInstance()::renderRightLeg)
            .put(SkinPartTypes.BIPED_LEFT_FOOT, FeetGuideRenderer.getInstance()::renderLeftLeg)
            .put(SkinPartTypes.BIPED_RIGHT_FOOT, FeetGuideRenderer.getInstance()::renderRightLeg)
            .put(SkinPartTypes.BIPED_LEFT_WING, WingsGuideRenderer.getInstance())
            .put(SkinPartTypes.TOOL_AXE, HeldItemGuideRenderer.getInstance())
            .put(SkinPartTypes.TOOL_HOE, HeldItemGuideRenderer.getInstance())
            .put(SkinPartTypes.TOOL_PICKAXE, HeldItemGuideRenderer.getInstance())
            .put(SkinPartTypes.TOOL_SHOVEL, HeldItemGuideRenderer.getInstance())
            .put(SkinPartTypes.ITEM_SHIELD, HeldItemGuideRenderer.getInstance())
            .put(SkinPartTypes.ITEM_SWORD, HeldItemGuideRenderer.getInstance())
            .put(SkinPartTypes.ITEM_BOW1, HeldItemGuideRenderer.getInstance())
            .put(SkinPartTypes.ITEM_BOW2, HeldItemGuideRenderer.getInstance())
            .put(SkinPartTypes.ITEM_BOW3, HeldItemGuideRenderer.getInstance())
            .put(SkinPartTypes.ITEM, HeldItemGuideRenderer.getInstance())
            .build();

    public ArmourerTileEntityRenderer(TileEntityRendererDispatcher rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light, int overlay) {
        ISkinType skinType = entity.getSkinType();
        ISkinProperties skinProperties = entity.getSkinProperties();

        RenderData renderData = RenderData.of(entity);
        renderData.tick();

        // when the player has some special texture, we must override to renderer.
        ResourceLocation playerTexture = renderData.displayTextureLocation;
        if (playerTexture != null) {
            buffers = new PlayerTextureOverride(playerTexture, buffers);
        }

        boolean isMultiBlocks = skinProperties.get(SkinProperty.BLOCK_MULTIBLOCK);
        boolean isShowGuides = entity.isShowGuides();
        boolean isShowHelper = entity.isShowHelper();
        boolean isUsesHelper = entity.usesHelper();

        matrixStack.pushPose();
        matrixStack.translate(0, 1, 0); // apply height offset
        matrixStack.scale(-1, -1, 1);

        float polygonOffset = 0f;
        for (ISkinPartType partType : skinType.getParts()) {
            Vector3i origin = partType.getOffset();
            Rectangle3i rect = partType.getBuildingSpace();
            Rectangle3i rect2 = partType.getGuideSpace();

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

            boolean isModelOverridden = partType.isModelOverridden(skinProperties);
            boolean isOverlayOverridden = partType.isOverlayOverridden(skinProperties);
            if (isUsesHelper) {
                isModelOverridden = !isShowHelper;
                // don't display overlay layers when helpers are actived.
                isOverlayOverridden = true;
            }

            matrixStack.pushPose();
            matrixStack.translate(origin.getX(), origin.getY() + rect.getMinY(), origin.getZ());
            matrixStack.translate(polygonOffset, polygonOffset, polygonOffset);

            // render guide model
            if (!isModelOverridden) {
                IGuideRenderer guideRenderer = guideRenderers.get(partType);
                if (guideRenderer != null) {
                    matrixStack.pushPose();
                    matrixStack.translate(0, -rect2.getMinY(), 0);
                    matrixStack.scale(16, 16, 16);
                    renderData.shouldRenderOverlay = !isOverlayOverridden;
                    guideRenderer.render(matrixStack, renderData, 0xf000f0, OverlayTexture.NO_OVERLAY, buffers);
                    matrixStack.popPose();
                }
            }

            matrixStack.scale(-1, -1, 1);

            if (isShowGuides) {
                // render building grid
                RenderUtils.drawCube(matrixStack, rect, r, g, b, a, buffers);
                RenderUtils.drawCube(matrixStack, originBox, 0, 1, 0, 0.5f, buffers);
                // render guide grid
                if (isModelOverridden) {
                    RenderUtils.drawCube(matrixStack, rect2, 0, 0, 1, 0.25f, buffers);
                }
            }

            matrixStack.popPose();
            polygonOffset += 0.001f;
        }
        matrixStack.popPose();
    }


    @Override
    public boolean shouldRenderOffScreen(T entity) {
        return true;
    }

    public static class PlayerTextureOverride implements IRenderTypeBuffer {

        protected final IRenderTypeBuffer buffers;
        protected final HashMap<RenderType, LazyValue<RenderType>> overrides = new HashMap<>();

        public PlayerTextureOverride(ResourceLocation texture, IRenderTypeBuffer buffers) {
            this.buffers = buffers;
            this.overrides.put(SkinRenderType.PLAYER_CUTOUT_NO_CULL, new LazyValue<>(() -> SkinRenderType.entityCutoutNoCull(texture)));
            this.overrides.put(SkinRenderType.PLAYER_CUTOUT, new LazyValue<>(() -> SkinRenderType.entityCutout(texture)));
            this.overrides.put(SkinRenderType.PLAYER_TRANSLUCENT, new LazyValue<>(() -> SkinRenderType.entityTranslucentCull(texture)));
        }

        @Override
        public IVertexBuilder getBuffer(RenderType renderType) {
            LazyValue<RenderType> overrideRenderType = overrides.get(renderType);
            if (overrideRenderType != null) {
                renderType = overrideRenderType.get();
            }
            return buffers.getBuffer(renderType);
        }
    }

    public static class RenderData implements IGuideProvider {

        protected int lastVersion;
        protected boolean shouldRenderOverlay = false;

        protected final ArmourerTileEntity tileEntity;

        protected final SkinDynamicTexture displayTexture;
        protected final ResourceLocation displayTextureLocation;

        public RenderData(ArmourerTileEntity tileEntity) {
            this.tileEntity = tileEntity;
            this.displayTexture = new SkinDynamicTexture();
            this.displayTextureLocation = Minecraft.getInstance().getTextureManager().register(identifier(tileEntity), displayTexture);
        }

        public static RenderData of(ArmourerTileEntity tileEntity) {
            Object renderData = tileEntity.getRenderData();
            if (renderData instanceof RenderData) {
                return (RenderData) renderData;
            }
            RenderData renderData1 = new RenderData(tileEntity);
            tileEntity.setRenderData(renderData1);
            return renderData1;
        }

        public static String identifier(ArmourerTileEntity tileEntity) {
            BlockPos pos = tileEntity.getBlockPos();
            return String.format("aw-armourer-%d-%d-%d", pos.getX(), pos.getY(), pos.getZ());
        }

        @Override
        protected void finalize() throws Throwable {
            Minecraft.getInstance().getTextureManager().release(displayTextureLocation);
            super.finalize();
        }

        public void tick() {
            this.displayTexture.setRefer(TextureUtils.getPlayerTextureLocation(tileEntity.getTextureDescriptor()));
            this.displayTexture.setPaintData(tileEntity.getPaintData());
        }

        @Override
        public boolean shouldRenderOverlay() {
            return shouldRenderOverlay;
        }
    }
}
