package moe.plushie.armourers_workshop.core.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.render.SkinRenderData;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.skin.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.utils.AWContributors;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.common.AWEntities;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
//        renderArmorSkin(entity, innerModel, packedLightIn, matrixStack, buffers);
    }

//    protected void renderArmorSkin(T entity, A model, int packedLightIn, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
//        if (wardrobeLayer != null || entity.isInvisible()) {
//            return;
//        }
//        float f = 1 / 16f;
//        SkinRenderData renderData = SkinRenderData.of(entity);
//        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(EntityType.ZOMBIE); // bip
//        if (renderData == null || renderer == null) {
//            return;
//        }
//        matrixStack.pushPose();
//        matrixStack.scale(f, f, f);
//
//        ClientWardrobeHandler.onRenderArmorPre(entity, model, packedLightIn, matrixStack, buffers);
//
//        float partialTicks = System.currentTimeMillis() % 100000000;
//        for (BakedSkin bakedSkin : renderData.getArmorSkins()) {
//            renderer.render(entity, model, bakedSkin, renderData.getColorScheme(), null, packedLightIn, partialTicks, matrixStack, buffers);
//        }
//
//        matrixStack.popPose();
//    }


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
