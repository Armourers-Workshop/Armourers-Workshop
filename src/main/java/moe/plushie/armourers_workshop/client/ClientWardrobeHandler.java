package moe.plushie.armourers_workshop.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.common.ArmourersConfig;
import moe.plushie.armourers_workshop.core.render.SkinRenderBuffer;
import moe.plushie.armourers_workshop.core.render.model.HeldItemModel;
import moe.plushie.armourers_workshop.core.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ClientWardrobeHandler {

    public final static HeldItemModel HELD_ITEM_MODEL = new HeldItemModel();


    public static void init() {
        ClientModelHandler.init();
    }


    public static void onRenderArmor(LivingEntity entity, Model model, int light, MatrixStack matrixStack, IRenderTypeBuffer renderType) {
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe == null) {
            return;
        }
        int t = (int) System.currentTimeMillis();
        float f = 1f / 16f;
        matrixStack.pushPose();
        matrixStack.scale(f, f, f);
        SkinRenderBuffer buffer = SkinRenderBuffer.getInstance();
        for (BakedSkin bakedSkin : wardrobe.getArmorSkins()) {
            SkinModelRenderer.renderSkin(bakedSkin, wardrobe.getDye(), entity, model, null, light, t, matrixStack, buffer);
        }
        buffer.endBatch();
        matrixStack.popPose();
    }

    public static void onRenderItem(LivingEntity entity, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, int light, MatrixStack matrixStack, IRenderTypeBuffer renderType, CallbackInfo callback) {
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe == null) {
            return;
        }
        int t = (int) System.currentTimeMillis();
        float f = 1f / 16f;
        matrixStack.pushPose();
        matrixStack.scale(f, f, f);
        matrixStack.scale(-1, -1, 1);

//        RenderUtils.drawBoundingBox(matrixStack, new Rectangle3D(0, 0, 0, 2, 2, 2), Color.MAGENTA);
//        RenderUtils.drawBoundingBox(matrixStack, new Rectangle3D(0, 0, 0, -2, -2, -2), Color.MAGENTA);

//        this.getParentModel().translateToHand(p_229135_4_, p_229135_5_);
//        p_229135_5_.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
//        p_229135_5_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
//        boolean flag = p_229135_4_ == HandSide.LEFT;
//        p_229135_5_.translate((double)((float)(flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);

//        matrixStack.translate(x, y, z); // 0 -2 2  // -1/1, 2, 10
//        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
//


//        this.applyItemArmTransform(p_228405_8_, handside, p_228405_7_);
//          int i = p_228406_2_ == HandSide.RIGHT ? 1 : -1;
//          p_228406_1_.translate((double)((float)i * 0.56F), (double)(-0.52F + p_228406_3_ * -0.6F), (double)-0.72F);

//        this.applyItemArmAttackTransform(p_228405_8_, handside, p_228405_5_);
//          int i = p_228399_2_ == HandSide.RIGHT ? 1 : -1;
//          float f = MathHelper.sin(p_228399_3_ * p_228399_3_ * (float)Math.PI);
//          p_228399_1_.mulPose(Vector3f.YP.rotationDegrees((float)i * (45.0F + f * -20.0F)));
//          float f1 = MathHelper.sin(MathHelper.sqrt(p_228399_3_) * (float)Math.PI);
//          p_228399_1_.mulPose(Vector3f.ZP.rotationDegrees((float)i * f1 * -20.0F));
//          p_228399_1_.mulPose(Vector3f.XP.rotationDegrees(f1 * -80.0F));
//          p_228399_1_.mulPose(Vector3f.YP.rotationDegrees((float)i * -45.0F));


        SkinRenderBuffer buffer = SkinRenderBuffer.getInstance();
        for (BakedSkin bakedSkin : wardrobe.getItemSkins(itemStack)) {
            SkinModelRenderer.renderSkin(bakedSkin, wardrobe.getDye(), entity, HELD_ITEM_MODEL, transformType, light, t, matrixStack, buffer);
            callback.cancel();
        }
        buffer.endBatch();

//        this.itemRenderer.renderStatic(p_228397_1_, p_228397_2_, p_228397_3_, p_228397_4_, p_228397_5_, p_228397_6_, p_228397_1_.level, p_228397_7_, OverlayTexture.NO_OVERLAY);
//        public void renderItem(LivingEntity p_228397_1_, ItemStack p_228397_2_, ItemCameraTransforms.TransformType p_228397_3_, boolean p_228397_4_, MatrixStack p_228397_5_, IRenderTypeBuffer p_228397_6_, int p_228397_7_) {

        matrixStack.popPose();
//        if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
//            matrixStack.pushPose();
//            ModelTransformer.apply(matrixStack, xq);
//            Minecraft.getInstance().getItemRenderer().renderStatic(entity, itemStack, transformType, true, matrixStack, renderType, entity.level, 0, OverlayTexture.NO_OVERLAY);
//            matrixStack.popPose();
//        }

        // this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, p_228381_3_)
    }

    public static void onRenderEntityInInventoryPre(LivingEntity entity, int x, int y, int scale, float mouseX, float mouseY) {
        if (!ArmourersConfig.enableEntityInInventoryClip) {
            return;
        }
        int left, top, width, height;
        switch (scale) {
            case 20: // in creative container screen
                width = 32;
                height = 43;
                left = x - width / 2 + 1;
                top = y - height + 4;
                break;

            case 30: // in survival container screen
                width = 49;
                height = 70;
                left = x - width / 2 - 1;
                top = y - height + 3;
                break;

            default:
                return;
        }
        RenderUtils.enableScissor(left, top, width, height);
    }

    public static void onRenderEntityInInventoryPost(LivingEntity entity) {
        if (!ArmourersConfig.enableEntityInInventoryClip) {
            return;
        }
        RenderUtils.disableScissor();
    }

    public static void onRenderEquipment(LivingEntity entity, EquipmentSlotType slotType, MatrixStack matrixStack, IRenderTypeBuffer renderType, CallbackInfo callback) {
        ItemStack itemStack = entity.getItemBySlot(slotType);
        if (itemStack.isEmpty()) {
            return;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null && wardrobe.hasOverriddenEquipment(slotType)) {
            callback.cancel();
        }
    }
}
