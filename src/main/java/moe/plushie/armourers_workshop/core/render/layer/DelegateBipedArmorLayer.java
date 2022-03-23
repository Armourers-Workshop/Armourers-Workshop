package moe.plushie.armourers_workshop.core.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DelegateBipedArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends BipedArmorLayer<T, M, A> {

    private final BipedArmorLayer<T, M, A> armorLayer;
    private SkinWardrobeLayer<T, ?> wardrobeLayer;

    public DelegateBipedArmorLayer(IEntityRenderer<T, M> renderer, BipedArmorLayer<T, M, A> armorLayer) {
        super(renderer, armorLayer.innerModel, armorLayer.outerModel);
        this.armorLayer = armorLayer;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffers, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        armorLayer.render(matrixStack, buffers, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
//        renderArmorSkin(matrixStack, buffers, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    protected void renderArmorSkin(MatrixStack matrixStack, IRenderTypeBuffer buffers, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (wardrobeLayer != null || entity.isInvisible()) {
            return;
        }
        matrixStack.pushPose();

//        entity.getArmorSlots().forEach();

        RenderUtils.drawPoint(matrixStack, buffers);
//        this.renderArmorPiece(p_225628_1_, p_225628_2_, p_225628_4_, EquipmentSlotType.CHEST, p_225628_3_, this.getArmorModel(EquipmentSlotType.CHEST));
//        this.renderArmorPiece(p_225628_1_, p_225628_2_, p_225628_4_, EquipmentSlotType.LEGS, p_225628_3_, this.getArmorModel(EquipmentSlotType.LEGS));
//        this.renderArmorPiece(p_225628_1_, p_225628_2_, p_225628_4_, EquipmentSlotType.FEET, p_225628_3_, this.getArmorModel(EquipmentSlotType.FEET));
//        this.renderArmorPiece(p_225628_1_, p_225628_2_, p_225628_4_, EquipmentSlotType.HEAD, p_225628_3_, this.getArmorModel(EquipmentSlotType.HEAD));

//        // apply the model baby scale.
//        EntityModel<?> entityModel = getParentModel();
//        if (entityModel.young && entityModel instanceof BipedModel<?>) {
//            BipedModel<?> bipedModel = (BipedModel<?>) entityModel;
//            float scale = 1.0f / bipedModel.babyBodyScale;
//            matrixStack.scale(scale, scale, scale);
//            matrixStack.translate(0.0f, bipedModel.bodyYOffset / 16.0f, 0.0f);
//        }
//        ClientWardrobeHandler.onRenderArmor(entity, entityModel, packedLightIn, matrixStack, renderTypeBuffer);

        matrixStack.popPose();
    }


    public SkinWardrobeLayer<T, ?> getWardrobeLayer() {
        return wardrobeLayer;
    }

    public void setWardrobeLayer(SkinWardrobeLayer<T, ?> wardrobeLayer) {
        this.wardrobeLayer = wardrobeLayer;
    }

    @Override
    public M getParentModel() {
        return armorLayer.getParentModel();
    }
}
