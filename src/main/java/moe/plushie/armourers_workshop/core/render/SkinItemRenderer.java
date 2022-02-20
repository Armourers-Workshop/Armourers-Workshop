package moe.plushie.armourers_workshop.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.entity.SkinDummyEntity;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderBuffer;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.data.SkinPalette;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public final class SkinItemRenderer {

    public static int mp1 = 0;
    private static int lastI = 0;

    private static ItemCameraTransforms.TransformType lastTransformType = ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;
    private static SkinDummyEntity entity2;
    private static SkinPalette entityDye = SkinPalette.EMPTY;


    public static void renderSkinAsItem(MatrixStack matrixStack, BakedSkin bakedSkin, int light, boolean showSkinPaint, boolean doLodLoading, int targetWidth, int targetHeight, SkinRenderBuffer buffer) {
        if (bakedSkin == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.scale(1, -1, -1);
//
//        if (playerModel == null) {
//            playerModel = new BipedModel(0);
//            Minecraft minecraft = Minecraft.getInstance();
//            ClientPlayerEntity player = minecraft.player;
////            VillagerEntity
//           ZombieEntity entity = new ZombieEntity(minecraft.player.level);
//            playerModel.prepareMobModel(entity, 0, 0, 0);
//            playerModel.setupAnim(entity, 0, 0, 0, 0, 0);
////            playerModel = new ClientPlayerEntity(minecraft, minecraft.level, player.connection, player.getStats(), player.getRecipeBook(), false, false);
//
//        }

//        Minecraft p_i232461_1_, ClientWorld p_i232461_2_, ClientPlayNetHandler p_i232461_3_, StatisticsManager
//        p_i232461_4_, ClientRecipeBook p_i232461_5_, boolean p_i232461_6_, boolean p_i232461_7_


//
//        float blockScale = 16F;
//        float mcScale = 1F / blockScale;
//        matrixStack.scale(mcScale, mcScale, mcScale);

//        Rectangle3D maxBounds = new Rectangle3D(bakedSkin.getSkin().getRenderShape(playerModel).bounds());
//        float newScaleW = (float)targetWidth / Math.max((float)bounds.getSize(), (float)bounds.getZsize());
//        float newScaleH = (float)targetHeight / (float)bounds.getYsize();
//        float newScale = Math.min(newScaleW, newScaleH);
//        matrixStack.scale(newScale, newScale, newScale);
//        matrixStack.translate(-maxBounds.getMidX(), -maxBounds.getMidY(), -maxBounds.getMidZ());

        if (entity2 == null) {
            entity2 = new SkinDummyEntity();
        }

        if (lastI != mp1) {
            BipedModel<SkinDummyEntity> playerModel = entity2.getModel();
            playerModel.young = (mp1 == 1);
            playerModel.crouching = (mp1 == 2);
            playerModel.riding = (mp1 == 3);
            playerModel.setupAnim(entity2, 0, 0, 0, 0, 0);
            lastI = mp1;
        }

        SkinModelRenderer.renderSkin(bakedSkin, entityDye, entity2, entity2.getModel(), lastTransformType, light, 0, matrixStack, buffer);

        matrixStack.popPose();
    }

    public static void renderSkin(BakedSkin bakedSkin, int light, int partialTicks, int targetWidth, int targetHeight, ItemCameraTransforms.TransformType transformType, Vector3f rotation, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        SkinDummyEntity entity = SkinDummyEntity.shared();
        Rectangle3f rect = bakedSkin.getRenderBounds(entity, entity.getModel(), rotation);

        float width = rect.getWidth();
        float height = rect.getHeight();

        // with non gui render, we need make sure has enough area.
        if (transformType != ItemCameraTransforms.TransformType.GUI && transformType != ItemCameraTransforms.TransformType.FIXED) {
            double d = Math.sqrt(rect.getWidth() * rect.getWidth() + rect.getHeight() * rect.getHeight() + rect.getDepth() * rect.getDepth());
            width = (float) d;
            height = (float) d;
        }
        matrixStack.pushPose();

        float scale = Math.min(targetWidth / width, targetHeight / height);
        matrixStack.scale(-scale, -scale, scale);
        matrixStack.translate(-rect.getMidX(), -rect.getMidY(), -rect.getMidZ());

        SkinRenderBuffer buffer1 = SkinRenderBuffer.getInstance();
        SkinModelRenderer.renderSkin(bakedSkin, entityDye, entity, entity.getModel(), ItemCameraTransforms.TransformType.NONE, light, partialTicks, matrixStack, buffer1);
        buffer1.endBatch();

        matrixStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public static class ItemStackRenderer extends ItemStackTileEntityRenderer {

        @Override
        @SuppressWarnings({"deprecation", "NullableProblems"})
        public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay) {
            IBakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemStack);
            ItemTransformVec3f transform = bakedModel.getTransforms().getTransform(transformType);
            BakedSkin skin = AWCore.bakery.loadSkin(SkinDescriptor.of(itemStack));
            if (skin == null) {
                return;
            }
            matrixStack.pushPose();
            matrixStack.translate(0.5F, 0.5F, 0.5F); // reset to center

            SkinItemRenderer.renderSkin(skin, light, 0, 1, 1, transformType, transform.rotation, matrixStack, renderTypeBuffer);

            matrixStack.popPose();
        }
    }
}