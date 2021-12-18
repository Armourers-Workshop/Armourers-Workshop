//package moe.plushie.armourers_workshop.core.model.skin;
//
//import com.mojang.blaze3d.matrix.MatrixStack;
//import moe.plushie.armourers_workshop.core.api.common.IExtraColours;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkin;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
//import moe.plushie.armourers_workshop.core.render.SkinPartRenderData;
//import moe.plushie.armourers_workshop.core.render.SkinPartRenderer;
//import moe.plushie.armourers_workshop.core.render.SkinRenderData;
//import moe.plushie.armourers_workshop.core.skin.data.Skin;
//import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
//import net.minecraft.client.renderer.IRenderTypeBuffer;
//import net.minecraft.entity.Entity;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//import java.util.ArrayList;
//
//@OnlyIn(Dist.CLIENT)
//public class ModelSkinLegs extends ModelTypeHelper {
//
//
//
//    @Override
//    public void render(Entity entity, ISkin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, MatrixStack matrix, IRenderTypeBuffer renderer) {
////        render(entity, (Skin) skin, new SkinRenderData(SCALE, skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null), matrix, renderer);
//        render(entity, (Skin) skin, new SkinRenderData(skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null), matrix, renderer);
//    }
//
//    //    @Override
////    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
////        render(entity, skin, new SkinRenderData(SCALE, skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null));
////    }
//
////    @Override
//    public void render(Entity entity, Skin skin, SkinRenderData renderData, MatrixStack matrix, IRenderTypeBuffer renderer) {
//        if (skin == null) {
//            return;
//        }
//        renderData.renderer = renderer;
//        renderData.matrix = matrix;
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
////        GlStateManager.pushAttrib();
////        RenderHelper.enableGUIStandardItemLighting();
//
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
//
//        boolean isAdvanced = false;
//
//        for (int i = 0; i < parts.size(); i++) {
//            SkinPart part = parts.get(i);
//
////            GL11.glPushMatrix();
////            if (isChild) {
////                float f6 = 2.0F;
////                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
////                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
////            }
////            if (isSneak) {
////                GlStateManager.translate(0.0F, 0.2F, 0.0F);
////                GL11.glTranslated(0, -3 * SCALE, 4 * SCALE);
////            }
//
//            if (part.getType().getPartName().equals("leftLeg")) {
//                renderLeftLeg(matrix, new SkinPartRenderData(part, renderData));
//            } else if (part.getType().getPartName().equals("rightLeg")) {
//                renderRightLeg(matrix, new SkinPartRenderData(part, renderData));
//            } else if (part.getType().getPartName().equals("skirt")) {
//                renderSkirt(matrix, new SkinPartRenderData(part, renderData));
//            } else if (part.getType().getPartName().equals("advanced_part")) {
//                isAdvanced = true;
//            }
//
////            GL11.glPopMatrix();
//        }
//
////        if (isAdvanced) {
////            AdvancedData advancedData = new AdvancedData();
////
////            int partCount = 4;
////
////            AdvancedPart base = new AdvancedPart(0, "base");
////
////            AdvancedPart advParts1[] = new AdvancedPart[partCount];
////            AdvancedPart advParts2[] = new AdvancedPart[partCount];
////            AdvancedPart advParts3[] = new AdvancedPart[partCount];
////
////            for (int i = 0; i < partCount; i++) {
////                advParts1[i] = new AdvancedPart(0, String.valueOf(i));
////                advParts1[i].pos = new Vec3d(0D, 0D, 8D);
////
////                advParts2[i] = new AdvancedPart(0, String.valueOf(i));
////                advParts2[i].pos = new Vec3d(0D, 0D, 8D);
////
////                advParts3[i] = new AdvancedPart(0, String.valueOf(i));
////                advParts3[i].pos = new Vec3d(0D, 0D, 8D);
////            }
////
////            for (int i = 0; i < partCount - 1; i++) {
////                advParts1[i].getChildren().add(advParts1[i + 1]);
////
////                advParts2[i].getChildren().add(advParts2[i + 1]);
////
////                advParts3[i].getChildren().add(advParts3[i + 1]);
////            }
////
////            base.getChildren().add(advParts1[0]);
////            base.getChildren().add(advParts2[0]);
////            base.getChildren().add(advParts3[0]);
////
////            base.rotationAngle = new Vec3d(-30, 0, 0);
////
////            advParts1[0].rotationAngle = new Vec3d(10, 0, 0);
////            advParts1[1].rotationAngle = new Vec3d(10, 0, 0);
////            advParts1[2].rotationAngle = new Vec3d(10, 0, 0);
////
////            advParts2[0].rotationAngle = new Vec3d(10, 10, 0);
////            advParts2[1].rotationAngle = new Vec3d(10, 0, 0);
////            advParts2[2].rotationAngle = new Vec3d(10, 0, 0);
////
////            advParts3[0].rotationAngle = new Vec3d(10, -10, 0);
////            advParts3[1].rotationAngle = new Vec3d(10, 0, 0);
////            advParts3[2].rotationAngle = new Vec3d(10, 0, 0);
////
////            GlStateManager.pushMatrix();
////            if (isChild) {
////                float f6 = 2.0F;
////                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
////                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
////            }
////            if (isSneak) {
////                GlStateManager.translate(0.0F, 0.2F, 0.0F);
////                GL11.glTranslated(0, -3 * SCALE, 4 * SCALE);
////            }
////            if (!renderData.isItemRender()) {
////                GlStateManager.translate(0F, 12F * renderData.getScale(), 0F);
////            }
////            AdvancedPartRenderer.renderAdvancedSkin(skin, renderData, entity, advancedData, base);
////            GlStateManager.popMatrix();
////        }
////
////        GlStateManager.popAttrib();
////        GlStateManager.color(1F, 1F, 1F, 1F);
//    }
//
//    private void renderLeftLeg(MatrixStack matrixStack, SkinPartRenderData partRenderData) {
//        matrixStack.pushPose();
////        GL11.glPushMatrix();
////        GL11.glColor3f(1F, 1F, 1F);
//        matrixStack.translate(0, 12, 0);
//        matrixStack.translate(2, 0, 0);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);
//        renderPart(partRenderData);
//        matrixStack.popPose();
//    }
//
//    private void renderRightLeg(MatrixStack matrixStack, SkinPartRenderData partRenderData) {
//        matrixStack.pushPose();
////        GL11.glColor3f(1F, 1F, 1F);
//        matrixStack.translate(0, 12, 0);
//        matrixStack.translate(-2, 0, 0);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);
//        renderPart(partRenderData);
//        matrixStack.popPose();
//    }
//
//    private void renderSkirt(MatrixStack matrixStack, SkinPartRenderData partRenderData) {
//        matrixStack.pushPose();;
////        GL11.glPushMatrix();
////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
////        GL11.glColor3f(1F, 1F, 1F);
//        matrixStack.translate(0, 12, 0);
////        if (isRiding) {
////            GL11.glRotated(-70, 1F, 0F, 0F);
////        }
//
//        renderPart(partRenderData);
//        matrixStack.popPose();
//    }
//}
