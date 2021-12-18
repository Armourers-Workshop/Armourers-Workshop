//package moe.plushie.armourers_workshop.core.model.skin;
//
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//@OnlyIn(Dist.CLIENT)
//public class ModelSkinChest extends ModelTypeHelper {
//
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
////        if (parts.size() > -1) {
////            // return;
////        }
////
////        if (entity != null && entity instanceof EntityPlayer) {
////            EntityPlayer player = (EntityPlayer) entity;
////            this.isSneak = player.isSneaking();
////            this.isRiding = player.isRiding();
////            /*
////             * this.heldItemRight = 0; if (player.getHeldItem() != null) {
////             * this.heldItemRight = 1; }
////             */
////        }
////        GlStateManager.pushAttrib();
////        // GlStateManager.enableColorMaterial();
////        RenderHelper.enableGUIStandardItemLighting();
////
////        if (skin.hasPaintData() & renderData.isShowSkinPaint() & ClientProxy.getTexturePaintType() == TexturePaintType.TEXTURE_REPLACE) {
////            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(skin, renderData.getSkinDye(), renderData.getExtraColours());
////            st.bindTexture();
////            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
////            GL11.glDisable(GL11.GL_CULL_FACE);
////            GL11.glEnable(GL11.GL_ALPHA_TEST);
////            bipedBody.render(SCALE);
////            bipedLeftArm.render(SCALE);
////            bipedRightArm.render(SCALE);
////            GL11.glPopAttrib();
////        }
////
////        boolean override = SkinProperty.MODEL_OVERRIDE_CHEST.getValue(skin.getProperties());
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
////            if (isSneak) {
////                GlStateManager.translate(0.0F, 0.2F, 0.0F);
////            }
////
////            if (part.getType().getPartName().equals("base")) {
////                renderChest(new SkinPartRenderData(part, renderData));
////            } else if (part.getType().getPartName().equals("leftArm")) {
////                renderLeftArm(new SkinPartRenderData(part, renderData), override);
////            } else if (part.getType().getPartName().equals("rightArm")) {
////                renderRightArm(new SkinPartRenderData(part, renderData), override);
////            }
////
////            GL11.glPopMatrix();
////        }
////        GlStateManager.popAttrib();
////        GlStateManager.color(1F, 1F, 1F, 1F);
////    }
////
////    private void renderChest(SkinPartRenderData skinPartRenderData) {
////        GL11.glPushMatrix();
////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
////        if (isSneak) {
////            GL11.glRotated(Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
////        }
////        GL11.glColor3f(1F, 1F, 1F);
////        renderPart(skinPartRenderData);
////        GL11.glPopMatrix();
////    }
////
////    private void renderLeftArm(SkinPartRenderData partRenderData, boolean override) {
////        GL11.glPushMatrix();
////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
////
////        GL11.glTranslatef(5.0F * partRenderData.getScale(), 0F, 0F);
////        GL11.glTranslatef(0F, 2.0F * partRenderData.getScale(), 0F);
////        if (slim & !override) {
////            GlStateManager.translate(0, partRenderData.getScale() * 0.5F, 0);
////        }
////
////        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleZ), 0, 0, 1);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleY), 0, 1, 0);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleX), 1, 0, 0);
////
////        if (slim & !override) {
////            GL11.glTranslatef(-0.25F * partRenderData.getScale(), 0F, 0F);
////            GL11.glScalef(0.75F, 1F, 1F);
////        }
////        renderPart(partRenderData);
////
////        GL11.glPopMatrix();
////    }
////
////    private void renderRightArm(SkinPartRenderData partRenderData, boolean override) {
////        GL11.glPushMatrix();
////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
////
////        GL11.glTranslatef(-5.0F * partRenderData.getScale(), 0F, 0F);
////        GL11.glTranslatef(0, 2.0F * partRenderData.getScale(), 0F);
////        if (slim & !override) {
////            GlStateManager.translate(0, partRenderData.getScale() * 0.5F, 0);
////        }
////
////        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);
////
////        if (slim & !override) {
////            GL11.glTranslatef(0.25F * partRenderData.getScale(), 0F, 0F);
////            GL11.glScalef(0.75F, 1F, 1F);
////        }
////
////        renderPart(partRenderData);
////        GL11.glPopMatrix();
////    }
//}
