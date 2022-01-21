//package moe.plushie.armourers_workshop.core.model.skin;
//
//import com.mojang.blaze3d.matrix.MatrixStack;
//import moe.plushie.armourers_workshop.core.api.client.render.IBakedSkin;
//import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
//import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
//import moe.plushie.armourers_workshop.core.skin.data.Skin;
//import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
//import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
//import net.minecraft.client.renderer.IRenderTypeBuffer;
//import net.minecraft.client.renderer.model.Model;
//import net.minecraft.entity.Entity;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//import java.util.List;
//
//@OnlyIn(Dist.CLIENT)
//public class ModelSkinOutfit extends ModelTypeHelper {
//
////    private SkinBipedModel<?> skinBipedModel = new SkinBipedModel<>();
////    @Override
////    public void render(Entity entity, ISkin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
////        render(entity, (Skin) skin, null, new SkinRenderData(skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null), matrixStack, renderer);
////    }
//
//    @Override
//    public void render(Entity entity, IBakedSkin skin, Model model, boolean itemRender, double distance, boolean doLodLoading, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
//        BakedSkin bakedSkin = (BakedSkin)skin;
//        SkinRenderData renderData = new SkinRenderData(skin.getSkinDye(), null, distance, doLodLoading, false, itemRender, null);
//        //skinBipedModel.prepareModel(bakedSkin, model);
////        skinBipedModel.renderToBuffer(bakedSkin, model, renderData, matrixStack, renderer);
//    }
//
//    public void render(Entity entity, Skin skin, SkinRenderData renderData, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
//        if (skin == null) {
//            return;
//        }
//        List<SkinPart> parts = skin.getParts();
////        if (entity != null && entity instanceof EntityPlayer) {
////            EntityPlayer player = (EntityPlayer) entity;
////            this.isSneak = player.isSneaking();
////            this.isRiding = player.isRiding();
////        }
//
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
////            bipedHead.render(SCALE);
////            bipedBody.render(SCALE);
////            bipedLeftArm.render(SCALE);
////            bipedRightArm.render(SCALE);
////
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
//
//
//        boolean overrideChest = skin.getProperties().get(SkinProperty.MODEL_OVERRIDE_CHEST);
//
////        double angle = 45D;
////
////        for (int i = 0; i < parts.size(); i++) {
////            SkinPart part = parts.get(i);
////
////            matrixStack.pushPose();
//////            matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
//////            GL11.glPushMatrix();
//////            if (isChild) {
//////                float f6 = 2.0F;
//////                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
//////                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
//////            }
////
////            if (part.getType().getRegistryName().equals("armourers:wings.leftWing")) {
//////                angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
//////                renderLeftWing(matrix,new SkinPartRenderData(part, renderData), angle);
////            }
////            if (part.getType().getRegistryName().equals("armourers:wings.rightWing")) {
//////                angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
//////                renderRightWing(matrix,new SkinPartRenderData(part, renderData), -angle);
////            }
////
////
////            part.applyTransform(matrixStack, modelBiped, skin, i);
////
////            RenderUtils.drawBoundingBox(matrixStack, part.getRenderShape(modelBiped), UtilColour.getPaletteColor(i));
////
////            renderPart(new SkinPartRenderData(part, renderData), matrixStack, renderer);
////
////            matrixStack.popPose();
////        }
////        GlStateManager.popAttrib();
////        GlStateManager.color(1F, 1F, 1F, 1F);
//    }
////
////    @Override
////    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading) {
////        render(entity, skin, new SkinRenderData(SCALE, skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null));
////    }
////
////    @Override
////    public void render(Entity entity, Skin skin, SkinRenderData renderData) {
////        if (skin == null) {
////            return;
////        }
////        ArrayList<SkinPart> parts = skin.getParts();
////
////        if (entity != null && entity instanceof EntityPlayer) {
////            EntityPlayer player = (EntityPlayer) entity;
////            this.isSneak = player.isSneaking();
////            this.isRiding = player.isRiding();
////        }
////
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
////            bipedHead.render(SCALE);
////            bipedBody.render(SCALE);
////            bipedLeftArm.render(SCALE);
////            bipedRightArm.render(SCALE);
////
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
////        boolean overrideChest = SkinProperty.MODEL_OVERRIDE_CHEST.getValue(skin.getProperties());
////
////        double angle = 45D;
////
////        for (int i = 0; i < parts.size(); i++) {
////            SkinPart part = parts.get(i);
////
////            GL11.glPushMatrix();
////            if (isChild) {
////                float f6 = 2.0F;
////                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
////                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
////            }
////
////
////            if (part.getType().getRegistryName().equals("armourers:wings.leftWing")) {
////                angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
////                renderLeftWing(new SkinPartRenderData(part, renderData), angle);
////            }
////            if (part.getType().getRegistryName().equals("armourers:wings.rightWing")) {
////                angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
////                renderRightWing(new SkinPartRenderData(part, renderData), -angle);
////            }
////
////            GL11.glPopMatrix();
////        }
////        GlStateManager.popAttrib();
////        GlStateManager.color(1F, 1F, 1F, 1F);
////    }
////
//////     renderSkin(IBakedSkin model, BlockState stateIn, BlockPos posIn, MatrixStack matrixIn, IVertexBuilder buffer)
//
////
////    private void renderLeftWing(MatrixStack matrixStack, SkinPartRenderData partRenderData, double angle) {
//////        matrixStack.pushPose();
//////        if (isSneak) {
//////            GlStateManager.translate(0.0F, 0.2F, 0.0F);
//////            GlStateManager.rotate((float) Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
//////        }
//////        matrixStack.translate(0, 0, 2 * partRenderData.getScale());
//////
//////        Vector3i point = new Vector3i(0, 0, 0);
//////        EnumFacing axis = EnumFacing.DOWN;
//////
//////        if (partRenderData.getSkinPart().getMarkerCount() > 0) {
//////            point = partRenderData.getSkinPart().getMarker(0);
//////            axis = partRenderData.getSkinPart().getMarkerSide(0);
//////        }
//////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateA ngleZ), 0, 0, 1);
//////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
//////        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
//////
//////        GL11.glTranslated(0.5F, 0.5F, 0.5F);
//////        GL11.glTranslated(point.getX(), point.getY(), point.getZ());
//////
//////        switch (axis) {
//////        case UP:
//////            GL11.glRotated(angle, 0, 1, 0);
//////            break;
//////        case DOWN:
//////            GL11.glRotated(angle, 0, -1, 0);
//////            break;
//////        case SOUTH:
//////            GL11.glRotated(angle, 0, 0, -1);
//////            break;
//////        case NORTH:
//////            GL11.glRotated(angle, 0, 0, 1);
//////            break;
//////        case EAST:
//////            GL11.glRotated(angle, 1, 0, 0);
//////            break;
//////        case WEST:
//////            GL11.glRotated(angle, -1, 0, 0);
//////            break;
//////        }
//////
//////        GL11.glTranslated(-point.getX(),-point.getY(), -point.getZ());
//////        GL11.glTranslated(-0.5F, -0.5F, -0.5F);
////        renderPart(partRenderData);
//////        matrixStack.popPose();
////    }
////
////    private void renderRightWing(MatrixStack matrixStack, SkinPartRenderData partRenderData, double angle) {
//////        matrixStack.pushPose();
//////        GL11.glPushMatrix();
//////        if (isSneak) {
//////            GlStateManager.translate(0.0F, 0.2F, 0.0F);
//////            GlStateManager.rotate((float) Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
//////        }
//////        matrixStack.translate(0, 0, 2 * partRenderData.getScale());
//////        Vector3i point = new Vector3i(0, 0, 0);
//////        EnumFacing axis = EnumFacing.DOWN;
//////
//////        if (partRenderData.getSkinPart().getMarkerCount() > 0) {
//////            point = partRenderData.getSkinPart().getMarker(0);
//////            axis = partRenderData.getSkinPart().getMarkerSide(0);
//////        }
//////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
//////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
//////        // GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
//////
//////        GL11.glTranslated(partRenderData.getScale() * 0.5F, partRenderData.getScale() * 0.5F, partRenderData.getScale() * 0.5F);
//////        GL11.glTranslated(partRenderData.getScale() * point.getX(), partRenderData.getScale() * point.getY(), partRenderData.getScale() * point.getZ());
//////
//////        switch (axis) {
//////        case UP:
//////            GL11.glRotated(angle, 0, 1, 0);
//////            break;
//////        case DOWN:
//////            GL11.glRotated(angle, 0, -1, 0);
//////            break;
//////        case SOUTH:
//////            GL11.glRotated(angle, 0, 0, -1);
//////            break;
//////        case NORTH:
//////            GL11.glRotated(angle, 0, 0, 1);
//////            break;
//////        case EAST:
//////            GL11.glRotated(angle, 1, 0, 0);
//////            break;
//////        case WEST:
//////            GL11.glRotated(angle, -1, 0, 0);
//////            break;
//////        }
//////
//////        GL11.glTranslated(partRenderData.getScale() * -point.getX(), partRenderData.getScale() * -point.getY(), partRenderData.getScale() * -point.getZ());
//////        GL11.glTranslated(partRenderData.getScale() * -0.5F, partRenderData.getScale() * -0.5F, partRenderData.getScale() * -0.5F);
////        renderPart(partRenderData);
//////        matrixStack.popPose();
////    }
//}
