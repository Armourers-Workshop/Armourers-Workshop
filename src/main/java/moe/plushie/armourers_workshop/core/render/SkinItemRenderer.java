package moe.plushie.armourers_workshop.core.render;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.base.AWEntities;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderBuffer;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWContributors;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public final class SkinItemRenderer {

    private static ItemStackRenderer INSTANCE;

    private static ColorScheme entityDye = ColorScheme.EMPTY;

    public static ItemStackRenderer getItemStackRenderer() {
        if (INSTANCE == null) {
            INSTANCE = new ItemStackRenderer();
        }
        return INSTANCE;
    }


    public static void renderSkin(BakedSkin bakedSkin, @Nullable Vector3f rotation, Vector3f scale, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
        Entity entity = getItemStackRenderer().getMannequinEntity();
        BipedModel<?> model = getItemStackRenderer().getMannequinModel();
        if (entity == null || entity.level == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0.5f, 0.5f); // reset to center
        matrixStack.scale(-1, -1, 1);

        Rectangle3f rect = bakedSkin.getRenderBounds(entity, model, rotation);
        float newScale = Math.min(targetWidth / rect.getWidth(), targetHeight / rect.getHeight());
        newScale = Math.min(newScale, targetDepth / rect.getDepth());

        if (AWConfig.showDebugTargetBounds) {
            RenderUtils.drawBoundingBox(matrixStack, -targetWidth / 2, -targetHeight / 2, -targetDepth / 2, targetWidth / 2, targetHeight / 2, targetDepth / 2, Color.BLUE, renderTypeBuffer);
            if (AWConfig.showDebugTargetPosition) {
                RenderUtils.drawPoint(matrixStack, AWConstants.ZERO, 2, renderTypeBuffer);
            }
        }

        matrixStack.scale(newScale / scale.x(), newScale / scale.y(), newScale / scale.z());
        matrixStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        SkinRenderBuffer buffer1 = SkinRenderBuffer.getInstance();
        SkinModelRenderer.renderSkin(bakedSkin, entityDye, entity, model, ItemCameraTransforms.TransformType.NONE, light, partialTicks, matrixStack, buffer1);
        buffer1.endBatch();

        matrixStack.popPose();
    }

    public static void renderMannequin(PlayerTextureDescriptor descriptor, Vector3f rotation, Vector3f scale, float targetWidth, float targetHeight, float targetDepth, float partialTicks, int light, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
        MannequinEntity entity = getItemStackRenderer().getMannequinEntity();
        if (entity == null || entity.level == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0.5f, 0.5f); // reset to center
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));

        if (!descriptor.equals(entity.getTextureDescriptor())) {
            entity.setTextureDescriptor(descriptor);
        }

        Rectangle3f rect = new Rectangle3f(entity.getBoundingBox());

        if (AWConfig.showDebugTargetBounds) {
            RenderUtils.drawBoundingBox(matrixStack, -targetWidth / 2, -targetHeight / 2, -targetDepth / 2, targetWidth / 2, targetHeight / 2, targetDepth / 2, Color.BLUE, renderTypeBuffer);
            if (AWConfig.showDebugTargetPosition) {
                RenderUtils.drawPoint(matrixStack, AWConstants.ZERO, 2, renderTypeBuffer);
            }
        }

        Rectangle3f resolvedRect = rect.offset(rect.getMidX(), rect.getMidY(), rect.getMidZ());
        resolvedRect.mul(new Matrix4f(new Quaternion(rotation.x(), rotation.y(), rotation.z(), true)));
        float newScale = Math.min(targetWidth / resolvedRect.getWidth(), targetHeight / resolvedRect.getHeight());

        matrixStack.scale(newScale / scale.x(), newScale / scale.y(), newScale / scale.z());
        matrixStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ()); // to model center

        EntityRendererManager rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
        IRenderTypeBuffer.Impl renderTypeBufferImp = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> rendererManager.render(entity, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f, matrixStack, renderTypeBufferImp, light));
        renderTypeBufferImp.endBatch();

        matrixStack.popPose();
    }

    @SuppressWarnings({"deprecation", "NullableProblems"})
    @OnlyIn(Dist.CLIENT)
    public static class ItemStackRenderer extends ItemStackTileEntityRenderer {

        private ItemStack playerMannequinItem;

        private MannequinEntity entity;
        private BipedModel<MannequinEntity> model;

        @Override
        public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay) {
            if (itemStack.isEmpty()) {
                return;
            }
            Item item = itemStack.getItem();
            IBakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemStack);
            ItemTransformVec3f transform = bakedModel.getTransforms().getTransform(transformType);

            if (item == AWItems.SKIN) {
                BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(SkinDescriptor.of(itemStack));
                if (bakedSkin != null) {
                    Vector3f rotation = new Vector3f(-transform.rotation.x(), -transform.rotation.y(), transform.rotation.z());
                    renderSkin(bakedSkin, rotation, transform.scale, 1, 1, 1, 0, light, matrixStack, renderTypeBuffer);
                }
            }

            if (item == AWItems.MANNEQUIN) {
                PlayerTextureDescriptor descriptor = PlayerTextureDescriptor.of(itemStack);
                renderMannequin(descriptor, transform.rotation, transform.scale, 1, 1, 1, 0, light, matrixStack, renderTypeBuffer);
            }
        }

        public MannequinEntity getMannequinEntity() {
            ClientWorld level = Minecraft.getInstance().level;
            if (entity == null) {
                entity = new MannequinEntity(AWEntities.MANNEQUIN, level);
                entity.setExtraRenderer(false); // never magic cir
            }
            if (entity.level != level) {
                entity.level = level;
            }
            return entity;
        }

        public BipedModel<?> getMannequinModel() {
            MannequinEntity entity = getMannequinEntity();
            if (model == null && entity != null) {
                model = new BipedModel<>(0);
                model.young = false;
                model.crouching = false;
                model.riding = false;
                model.prepareMobModel(entity, 0, 0, 0);
                model.setupAnim(entity, 0, 0, 0, 0, 0);
            }
            return model;
        }

        public ItemStack getPlayerMannequinItem() {
            if (playerMannequinItem == null) {
                ClientPlayerEntity player = Minecraft.getInstance().player;
                if (player == null) {
                    return ItemStack.EMPTY;
                }
                GameProfile profile = player.getGameProfile();
                AWContributors.Contributor contributor = AWContributors.getCurrentContributor();
                if (contributor != null) {
                    profile = new GameProfile(contributor.uuid, contributor.username);
                }
                PlayerTextureDescriptor descriptor = new PlayerTextureDescriptor(profile);
                playerMannequinItem = new ItemStack(AWItems.MANNEQUIN);
                playerMannequinItem.getOrCreateTag().put(AWConstants.NBT.MANNEQUIN_TEXTURE, descriptor.serializeNBT());
            }
            return playerMannequinItem;
        }
    }
}