package moe.plushie.armourers_workshop.core.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderDispatcher;
import moe.plushie.armourers_workshop.core.render.entity.SkinDummyEntity;
import moe.plushie.armourers_workshop.core.render.model.SkinModelRenderer;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.utils.Rectangle3D;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public final class SkinItemRenderer extends ItemStackTileEntityRenderer {

    public static final SkinItemRenderer INSTANCE = new SkinItemRenderer();

    public static int mp1 = 0;
    private static int lastI = 0;
    //    public static void renderSkinAsItem(ItemStack stack, boolean showSkinPaint, int targetWidth, int targetHeight) {
//        if (SkinNBTHelper.stackHasSkinData(stack)) {
//            SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
//            renderSkinAsItem(skinPointer, showSkinPaint, false, targetWidth, targetHeight);
//        }
//    }
    private static ItemCameraTransforms.TransformType lastTransformType = null;
    private final SkinDummyEntity entity2 = new SkinDummyEntity();

    private SkinItemRenderer() {
    }

    public void renderSkinAsItem(ISkinDescriptor skinPointer, boolean showSkinPaint, boolean doLodLoading, int targetWidth, int targetHeight) {
//        renderSkinAsItem(ClientSkinCache.INSTANCE.getSkin(skinPointer), skinPointer, showSkinPaint, doLodLoading, targetWidth, targetHeight);
    }

    public void renderSkinAsItem(MatrixStack matrixStack, IRenderTypeBuffer renderer, BakedSkin bakedSkin, int light, boolean showSkinPaint, boolean doLodLoading, int targetWidth, int targetHeight) {
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

        if (lastI != mp1) {
            BipedModel<SkinDummyEntity> playerModel = entity2.getModel();

            playerModel.young = (mp1 == 1);
            playerModel.crouching = (mp1 == 2);
            playerModel.riding = (mp1 == 3);
            playerModel.setupAnim(entity2, 0, 0, 0, 0, 0);
            lastI = mp1;
        }

        SkinModelRenderer.INSTANCE.renderSkin(entity2, bakedSkin, entity2.getModel(), lastTransformType, light, 0, matrixStack, renderer);

        matrixStack.popPose();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int partialTicks) {
        SkinDummyEntity entity = SkinDummyEntity.SHARED;
        BipedModel<?> model = entity.getModel();
        BakedSkin bakedSkin = ArmourersWorkshop.outfit;
        //        Item item = itemStack.getItem();

        float f = 1 / 16.0f;
        matrixStack.pushPose();
        matrixStack.scale(f, f, f);
        matrixStack.scale(-1, -1, 1);

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
        matrixStack.translate(8, -8, 0);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(30));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(45));

        //matrixStack.scale(0.6f, 0.6f, 0.6f);

        float targetWidth = 16.0f;
        float targetHeight = 16.0f;

        Rectangle3D maxBounds = new Rectangle3D(bakedSkin.getSkin().getRenderShape(model, transformType).bounds());
        float newScaleW = targetWidth / Math.max(maxBounds.getWidth(), maxBounds.getDepth());
        float newScaleH = targetHeight / maxBounds.getHeight();
        float newScale = Math.min(newScaleW, newScaleH);
        matrixStack.scale(newScale, newScale, newScale);
        matrixStack.translate(-maxBounds.getMidX(), -maxBounds.getMidY(), -maxBounds.getMidZ());

        SkinRenderDispatcher.startBatch();
        SkinModelRenderer.INSTANCE.renderSkin(entity, bakedSkin, model, transformType, light, partialTicks, matrixStack, renderTypeBuffer);
        SkinRenderDispatcher.endBatch();

        matrixStack.popPose();

//        GL11.glPushMatrix();
//        GlStateManager.translate(8 * 0.0625F, 8 * 0.0625F, 0);
//        renderLoadingIcon(descriptor);
//        GL11.glPopMatrix();
    }


//    public static void renderSkinWithHelper(Skin skin, ISkinDescriptor skinPointer, boolean showSkinPaint, boolean doLodLoading, MatrixStack matrixStack, IVertexBuilder builder) {
//        ISkinType skinType = skinPointer.getIdentifier().getType();
//        if (skinType == null) {
//            skinType = skin.getType();
//        }
//        skinType = skin.getType();
//
//        IEquipmentModel targetModel = SkinModelRenderer.INSTANCE.getTypeHelperForModel(SkinModelRenderer.ModelType.MODEL_BIPED, skinType);
////        targetModel.render(null, skin, matrix, renderer);
//        targetModel.render(null, skin, null, showSkinPaint, skinPointer.getSkinDye(), null, true, 0, doLodLoading, matrixStack, builder);
//
//    }
//
//    public static void renderSkinWithoutHelper(ISkinDescriptor skinPointer, boolean doLodLoading) {
//        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
//        if (skin == null) {
//            return;
//        }
//        SkinModelRenderHelper.INSTANCE.modelHelperDummy.render(null, skin, null, true, skinPointer.getSkinDye(), null, true, 0, doLodLoading);
//    }
}