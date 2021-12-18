package moe.plushie.armourers_workshop.core.model.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.api.client.render.IBakedSkin;
import moe.plushie.armourers_workshop.core.api.client.render.SkinBipedModel;
import moe.plushie.armourers_workshop.core.api.common.IExtraColours;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkin;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
import moe.plushie.armourers_workshop.core.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.render.other.SkinPartRenderData;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.UtilColour;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ModelSkinItem extends ModelTypeHelper {
//    @Override
//    public void render(Entity entity, ISkin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
//        render(entity, (Skin) skin, new SkinRenderData(skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null), matrixStack, renderer);
//    }

    public void render(Entity entity, Skin skin, SkinRenderData renderData, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
        if (skin == null) {
            return;
        }
        List<SkinPart> parts = skin.getParts();

//        if (entity != null && entity instanceof EntityPlayer) {
//            EntityPlayer player = (EntityPlayer) entity;
//            this.isSneak = player.isSneaking();
//            this.isRiding = player.isRiding();
//            /*this.heldItemRight = 0;
//            if (player.getHeldItem() != null) {
//                this.heldItemRight = 1;
//            }*/
//        }

//        GlStateManager.pushAttrib();
//        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < parts.size(); i++) {
            SkinPart part = parts.get(i);

//            GL11.glPushMatrix();
//            if (isChild) {
//                float f6 = 2.0F;
//                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
//                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
//            }

//            GlStateManager.enablePolygonOffset();
//            GlStateManager.doPolygonOffset(-3F * SCALE, -3F * SCALE);

//            if (part.getType().getPartName().equals("base")) {
                renderRightArm( new SkinPartRenderData(part, renderData), matrixStack, renderer);
//            }

//            GlStateManager.doPolygonOffset(0F, 0F);
//            GlStateManager.disablePolygonOffset();

//            GL11.glPopMatrix();

        }
//        GlStateManager.popAttrib();
//        GlStateManager.color(1F, 1F, 1F, 1F);
    }

//    @Override
//    public void render(Entity entity, Skin skin, SkinRenderData renderData) {
//        if (skin == null) {
//            return;
//        }
//
//        ArrayList<SkinPart> parts = skin.getParts();
//
//        if (entity != null && entity instanceof EntityPlayer) {
//            EntityPlayer player = (EntityPlayer) entity;
//            this.isSneak = player.isSneaking();
//            this.isRiding = player.isRiding();
//            /*this.heldItemRight = 0;
//            if (player.getHeldItem() != null) {
//                this.heldItemRight = 1;
//            }*/
//        }
//
//        GlStateManager.pushAttrib();
//        RenderHelper.enableGUIStandardItemLighting();
//
//        for (int i = 0; i < parts.size(); i++) {
//            SkinPart part = parts.get(i);
//
//            GL11.glPushMatrix();
//            if (isChild) {
//                float f6 = 2.0F;
//                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
//                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
//            }
//
//            GlStateManager.enablePolygonOffset();
//            GlStateManager.doPolygonOffset(-3F * SCALE, -3F * SCALE);
//
//            if (part.getType().getPartName().equals("base")) {
//                renderRightArm(new SkinPartRenderData(part, renderData));
//            }
//
//            GlStateManager.doPolygonOffset(0F, 0F);
//            GlStateManager.disablePolygonOffset();
//
//            GL11.glPopMatrix();
//
//        }
//        GlStateManager.popAttrib();
//        GlStateManager.color(1F, 1F, 1F, 1F);
//    }
//
    private void renderRightArm(SkinPartRenderData partRenderData, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
        matrixStack.pushPose();

//        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
//        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);

        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);

        //GL11.glTranslatef(-5.0F * scale, 0F, 0F);
        //GL11.glTranslatef(0F, 2.0F * scale, 0F);

        //GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);

        renderPart(partRenderData, matrixStack, renderer);
        matrixStack.popPose();
    }

    private SkinBipedModel<?> skinBipedModel = new SkinBipedModel<>();

    @Override
    public void render(Entity entity, IBakedSkin skin, Model model, boolean itemRender, double distance, boolean doLodLoading, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
        BakedSkin bakedSkin = (BakedSkin)skin;
        List<SkinPart> skinParts = bakedSkin.getSkin().getParts();
        SkinRenderData renderData = new SkinRenderData(skin.getSkinDye(), null, distance, doLodLoading, false, itemRender, null);
//        skinBipedModel.prepareModel(bakedSkin, model);
//        skinBipedModel.renderToBuffer(bakedSkin, renderData, matrixStack, renderer);
        int i = 0;
        for (SkinPart skinPart : skinParts) {
            matrixStack.pushPose();;
            matrixStack.scale(-1, -1, 1);
//            matrixStack.translate(3, 3, 0);
//            if (model instanceof BipedModel) {
//                BipedModel<?> bipedModel = (BipedModel<?>)model;
//                ModelRenderer modelRenderer = bipedModel.leftArm;
//                matrixStack.translate(modelRenderer.x, 0, 0);
//                matrixStack.mulPose(Vector3f.ZP.rotation(modelRenderer.zRot));
//                matrixStack.mulPose(Vector3f.YP.rotation(modelRenderer.yRot));
//                matrixStack.mulPose(Vector3f.XP.rotation(modelRenderer.xRot));
//                matrixStack.translate(1.0F, 10.0F, 0);
//                matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
//                matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
//            }
            SkinPartRenderer.INSTANCE.renderPart(new SkinPartRenderData(skinPart, renderData), matrixStack, renderer);
            RenderUtils.drawBoundingBox(matrixStack, skinPart.getRenderShape(), UtilColour.getPaletteColor(i++));
            matrixStack.popPose();
        }
    }
}
