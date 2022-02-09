package moe.plushie.armourers_workshop.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.entity.SkinDummyEntity;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class SkinItemRenderer {

    private static final Vector3f OFFSET = new Vector3f(30, 45, 0);
    public static int mp1 = 0;
    private static int lastI = 0;
    //    public static void renderSkinAsItem(ItemStack stack, boolean showSkinPaint, int targetWidth, int targetHeight) {
//        if (SkinNBTHelper.stackHasSkinData(stack)) {
//            SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
//            renderSkinAsItem(skinPointer, showSkinPaint, false, targetWidth, targetHeight);
//        }
//    }
    private static ItemCameraTransforms.TransformType lastTransformType = ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;
    private static SkinDummyEntity entity2;
    private static SkinDye entityDye;


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
            entityDye = new SkinDye();
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
}