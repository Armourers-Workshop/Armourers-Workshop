package moe.plushie.armourers_workshop.core.model.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.api.common.IExtraColours;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkin;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
import moe.plushie.armourers_workshop.core.render.other.SkinPartRenderData;
import moe.plushie.armourers_workshop.core.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ModelTypeHelper implements IEquipmentModel {

    public Skin npcSkinData = null;
    public ISkinDye npcDyeData = null;
    protected boolean slim;

//    protected static float SCALE = 0.0625F;
//    protected void setRotation(ModelRenderer model, float x, float y, float z) {
//        model.rotateAngleX = x;
//        model.rotateAngleY = y;
//        model.rotateAngleZ = z;
//    }
//
//    protected void setRotation(ModelRenderer targetModel, ModelRenderer sourceModel) {
//        targetModel.rotateAngleX = sourceModel.rotateAngleX;
//        targetModel.rotateAngleY = sourceModel.rotateAngleY;
//        targetModel.rotateAngleZ = sourceModel.rotateAngleZ;
//    }
//
//    @Override
//    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
//        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
//        this.isRiding = false;
//        this.isSneak = false;
//        // this.aimedBow = false;
//        this.isChild = false;
//        this.slim = false;
//        // this.heldItemRight = 0;
//        bipedLeftLeg.rotateAngleZ = 0F;
//        bipedRightLeg.rotateAngleZ = 0F;
//        bipedHead.rotateAngleZ = 0F;
//        bipedHeadwear.rotateAngleZ = 0F;
//    }
//

//    @Override
//    public void render(Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
//        if (npcSkinData != null) {
//            this.isRiding = false;
//            this.isSneak = false;
//            // this.aimedBow = false;
//            this.isChild = false;
//            this.slim = false;
//            // this.heldItemRight = 0;
//            if (entity instanceof EntityLivingBase) {
//                /*
//                 * if (((EntityLivingBase)entity).getHeldItem() != null) { this.heldItemRight =
//                 * 1; }
//                 */
//                if (((EntityLivingBase) entity).isRiding()) {
//                    this.isRiding = true;
//                }
//                if (((EntityLivingBase) entity).isSneaking()) {
//                    this.isSneak = true;
//                }
//                if (((EntityLivingBase) entity).isChild()) {
//                    this.isChild = true;
//                }
//            }
//            bipedLeftLeg.rotateAngleZ = 0F;
//            bipedRightLeg.rotateAngleZ = 0F;
//            bipedHead.rotateAngleZ = 0F;
//            bipedHeadwear.rotateAngleZ = 0F;
//
//            super.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, entity);
//
//            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//            GL11.glEnable(GL11.GL_CULL_FACE);
//            ModRenderHelper.enableAlphaBlend();
//            render(entity, npcSkinData, false, npcDyeData, null, false, 0, true);
//            ModRenderHelper.disableAlphaBlend();
//            GL11.glPopAttrib();
//
//            npcSkinData = null;
//            npcDyeData = null;
//        }
//    }

//    @Override
//    public void render(Entity entity, ISkin skin, BipedModel modelBiped, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
////        setRotationFromModelBiped(modelBiped);
////        render(entity, skin, showSkinPaint, skinDye, extraColours, itemRender, distance, doLodLoading, matrixStack, renderer);
//    }

//    @Override
//    public void render(Entity entity, Skin skin, ModelBiped modelBiped, SkinRenderData renderData) {
//        setRotationFromModelBiped(modelBiped);
//        render(entity, skin, renderData);
//    }
//
//    @Override
//    public void render(Entity entity, Skin skin, float limb1, float limb2, float limb3, float headY, float headX) {
//        setRotationAngles(limb1, limb2, limb3, headY, headX, SCALE, entity);
//        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        ModRenderHelper.enableAlphaBlend();
//        render(entity, skin, false, null, null, false, 0, true);
//        ModRenderHelper.disableAlphaBlend();
//        GL11.glPopAttrib();
//    }
//
//    public abstract void render(Entity entity, ISkin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, MatrixStack matrix, IRenderTypeBuffer renderer);
//    public void render(Entity entity, ISkin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
//
//    }

//
//    public abstract void render(Entity entity, Skin skin, SkinRenderData renderData);
//
//    protected void setRotationFromModelBiped(ModelBiped modelBiped) {
//        this.isRiding = false;
//        this.isSneak = false;
//        // this.aimedBow = false;
//        // this.heldItemRight = 0;
//        this.slim = false;
//        if (modelBiped == null) {
//            setRotation(bipedHead, 0F, 0F, 0F);
//            setRotation(bipedBody, 0F, 0F, 0F);
//            setRotation(bipedLeftArm, 0F, (float) Math.toRadians(-1F), (float) Math.toRadians(-5F));
//            setRotation(bipedRightArm, 0F, (float) Math.toRadians(1F), (float) Math.toRadians(5F));
//            setRotation(bipedLeftLeg, 0F, 0F, 0F);
//            setRotation(bipedRightLeg, 0F, 0F, 0F);
//            isChild = false;
//        } else {
//            if (modelBiped instanceof ModelPlayer) {
//                ModelPlayer modelPlayer = (ModelPlayer) modelBiped;
//                this.slim = modelPlayer.bipedLeftArm.rotationPointY == 2.5F;
//            }
//            setRotation(bipedHead, modelBiped.bipedHead);
//            setRotation(bipedBody, modelBiped.bipedBody);
//            setRotation(bipedLeftArm, modelBiped.bipedLeftArm);
//            setRotation(bipedRightArm, modelBiped.bipedRightArm);
//            setRotation(bipedLeftLeg, modelBiped.bipedLeftLeg);
//            setRotation(bipedRightLeg, modelBiped.bipedRightLeg);
//            isChild = modelBiped.isChild;
//            if (modelBiped instanceof ModelMannequin) {
//                this.slim = ((ModelMannequin) modelBiped).isSlim();
//            }
//        }
//    }
//
    protected void renderPart(SkinPartRenderData partRenderData, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
        SkinPartRenderer.INSTANCE.renderPart(partRenderData, matrixStack, renderer);
//        SkinPartRenderer.INSTANCE.renderPart(partRenderData);
    }

//    protected boolean skinHasHead(Skin skin) {
//        if (skin.getType() == SkinTypes.BIPED_HEAD) {
//            return true;
//        }
//        if (skin.getType() == SkinTypes.BIPED_OUTFIT) {
//            ISkinPartType headPart = SkinTypes.BIPED_HEAD.getSkinParts().get(0);
//            for (int i = 0; i < skin.getPartCount(); i++) {
//                if (skin.getSubParts().get(i).getType() == headPart) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}
