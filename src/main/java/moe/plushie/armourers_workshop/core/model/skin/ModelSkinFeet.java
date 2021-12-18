//package moe.plushie.armourers_workshop.core.model.skin;
//
//import com.mojang.blaze3d.matrix.MatrixStack;
//import moe.plushie.armourers_workshop.core.api.common.IExtraColours;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkin;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
//import moe.plushie.armourers_workshop.core.render.SkinPartRenderData;
//import moe.plushie.armourers_workshop.core.render.part.SkinPartRenderer;
//import moe.plushie.armourers_workshop.core.render.SkinRenderData;
//import moe.plushie.armourers_workshop.core.skin.data.Skin;
//import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
//import net.minecraft.client.renderer.IRenderTypeBuffer;
//import net.minecraft.entity.Entity;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import org.lwjgl.opengl.GL11;
//
//import java.util.ArrayList;
//
//@OnlyIn(Dist.CLIENT)
//public class ModelSkinFeet extends ModelTypeHelper {
//
//    public ModelSkinFeet() {
//    }
//
//    @Override
//    public void render(Entity entity, ISkin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, MatrixStack matrix, IRenderTypeBuffer renderer) {
//        render(entity, (Skin) skin, new SkinRenderData(skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null), matrix, renderer);
//    }
//
//
//    public void render(Entity entity, Skin skin, SkinRenderData renderData, MatrixStack matrix, IRenderTypeBuffer renderer) {
//        if (skin == null) {
//            return;
//        }
//        ArrayList<SkinPart> parts = skin.getParts();
//
////        if (entity != null && entity instanceof PlayerEntity) {
////            PlayerEntity player = (PlayerEntity) entity;
////            this.isSneak = player.isSneaking();
////            this.isRiding = player.isRiding();
////            /*
////             * this.heldItemRight = 0; if (player.getHeldItem() != null) {
////             * this.heldItemRight = 1; }
////             */
////        }
//
//        GL11.glPushAttrib(8256);
////        GlStateManager.pushAttrib();
////        RenderHelper.enableGUIStandardItemLighting();
////
////        if (skin.hasPaintData() & renderData.isShowSkinPaint() & ClientProxy.getTexturePaintType() == TexturePaintType.TEXTURE_REPLACE) {
////            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(skin, renderData.getSkinDye(), renderData.getExtraColours());
////            st.bindTexture();
////            GL11.glPushMatrix();
////            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
////            GL11.glDisable(GL11.GL_CULL_FACE);
////            GL11.glEnable(GL11.GL_ALPHA_TEST);
////            if (!renderData.isItemRender()) {
////                GlStateManager.enablePolygonOffset();
////                GlStateManager.doPolygonOffset(-2, 1);
////                //GL11.glTranslated(0, -12 * SCALE, 0);
////            }
////            GL11.glTranslated(0, 0 , 0.005F);
////            GL11.glTranslated(0.02F, 0 , 0);
////            bipedLeftLeg.render(SCALE);
////            GL11.glTranslated(-0.02F, 0 , 0);
////            bipedRightLeg.render(SCALE);
////            GL11.glTranslated(0, 0 , -0.005F);
////            if (!renderData.isItemRender()) {
////                GlStateManager.doPolygonOffset(0F, 0F);
////                GlStateManager.disablePolygonOffset();
////            }
////            GL11.glPopAttrib();
////            GL11.glPopMatrix();
////        }
////
//        for (int i = 0; i < parts.size(); i++) {
//            SkinPart part = parts.get(i);
//
//            matrix.pushPose();
////            if (isChild) {
////                float f6 = 2.0F;
////                matrix.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
////                matrix.translate(0.0F, 24.0F * SCALE, 0.0F);
////            }
////            if (isSneak) {
////                GlStateManager.translate(0.0F, 0.2F, 0.0F);
////                GL11.glTranslated(0, -3 * SCALE, 4 * SCALE);
////            }
//
//            if (part.getType().getPartName().equals("leftFoot")) {
////                renderLeftFoot(new SkinPartRenderData(part, renderData));
//                SkinPartRenderer.INSTANCE.renderPart(new SkinPartRenderData(part, renderData), matrix, renderer);
//
//            } else if (part.getType().getPartName().equals("rightFoot")) {
////                renderRightFoot(new SkinPartRenderData(part, renderData));
//                SkinPartRenderer.INSTANCE.renderPart(new SkinPartRenderData(part, renderData), matrix, renderer);
//            }
//            matrix.popPose();
//        }
//
//        GL11.glPopAttrib();
////        GlStateManager.popAttrib();
////        GlStateManager.color(1F, 1F, 1F, 1F);
//    }
////
////    private void renderLeftFoot(SkinPartRenderData partRenderData, MatrixStack matrix, IRenderTypeBuffer renderer) {
////        GL11.glPushMatrix();
////        GL11.glColor3f(1F, 1F, 1F);
////        // if (!itemRender) {
////        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
////        // }
////        GL11.glTranslated(2 * partRenderData.getScale(), 0, 0);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);
////
////        renderPart(partRenderData);
////        GL11.glPopMatrix();
////    }
////
////    private void renderRightFoot(SkinPartRenderData partRenderData, MatrixStack matrix, IRenderTypeBuffer renderer) {
////        GL11.glPushMatrix();
////        GL11.glColor3f(1F, 1F, 1F);
////        // if (!itemRender) {
////        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
////        // }
////        GL11.glTranslated(-2 * partRenderData.getScale(), 0, 0);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);
////
////        renderPart(partRenderData);
////        GL11.glPopMatrix();
////    }
//}
