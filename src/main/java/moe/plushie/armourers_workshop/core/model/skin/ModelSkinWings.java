//package moe.plushie.armourers_workshop.core.model.skin;
//
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//@OnlyIn(Dist.CLIENT)
//public class ModelSkinWings extends ModelTypeHelper  {
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
////
////        ArrayList<SkinPart> parts = skin.getParts();
////
////        if (entity != null && entity instanceof EntityPlayer) {
////            EntityPlayer player = (EntityPlayer) entity;
////            this.isSneak = player.isSneaking();
////            this.isRiding = player.isRiding();
////            /*this.heldItemRight = 0;
////            if (player.getHeldItem() != null) {
////                this.heldItemRight = 1;
////            }*/
////        }
////
////        GlStateManager.pushAttrib();
////        RenderHelper.enableGUIStandardItemLighting();
////
////        for (int i = 0; i < parts.size(); i++) {
////            SkinPart part = parts.get(i);
////
////            GL11.glPushMatrix();
////            if (isSneak) {
////                GlStateManager.translate(0.0F, 0.2F, 0.0F);
////                GlStateManager.rotate((float) Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
////            }
////            GL11.glTranslated(0, 0, SCALE * 2);
////            if (isChild) {
////                float f6 = 2.0F;
////                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
////                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
////            }
////
////            double angle = 45D;
////
////            angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
////
////            if (part.getType().getPartName().equals("leftWing")) {
////                renderLeftWing(new SkinPartRenderData(part, renderData), angle);
////            }
////            if (part.getType().getPartName().equals("rightWing")) {
////                renderRightWing(new SkinPartRenderData(part, renderData), -angle);
////            }
////            GL11.glPopMatrix();
////        }
////        GlStateManager.popAttrib();
////        GlStateManager.color(1F, 1F, 1F, 1F);
////    }
////
////    private void renderLeftWing(SkinPartRenderData partRenderData, double angle) {
////        GL11.glPushMatrix();
////
////        Vector3i point = new Vector3i(0, 0, 0);
////        EnumFacing axis = EnumFacing.DOWN;
////
////        if (partRenderData.getSkinPart().getMarkerCount() > 0) {
////            point = partRenderData.getSkinPart().getMarker(0);
////            axis = partRenderData.getSkinPart().getMarkerSide(0);
////        }
////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
////        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
////
////
////        GL11.glTranslated(SCALE * 0.5F, SCALE * 0.5F, SCALE * 0.5F);
////        GL11.glTranslated(SCALE * point.getX(), SCALE * point.getY(), SCALE * point.getZ());
////
////        switch (axis) {
////        case UP:
////            GL11.glRotated(angle, 0, 1, 0);
////            break;
////        case DOWN:
////            GL11.glRotated(angle, 0, -1, 0);
////            break;
////        case SOUTH:
////            GL11.glRotated(angle, 0, 0, -1);
////            break;
////        case NORTH:
////            GL11.glRotated(angle, 0, 0, 1);
////            break;
////        case EAST:
////            GL11.glRotated(angle, 1, 0, 0);
////            break;
////        case WEST:
////            GL11.glRotated(angle, -1, 0, 0);
////            break;
////        }
////
////        GL11.glTranslated(SCALE * -point.getX(), SCALE * -point.getY(), SCALE * -point.getZ());
////        GL11.glTranslated(SCALE * -0.5F, SCALE * -0.5F, SCALE * -0.5F);
////
////        renderPart(partRenderData);
////        GL11.glPopMatrix();
////    }
////
////    private void renderRightWing(SkinPartRenderData partRenderData, double angle) {
////        GL11.glPushMatrix();
////        Vector3i point = new Vector3i(0, 0, 0);
////        EnumFacing axis = EnumFacing.DOWN;
////
////        if (partRenderData.getSkinPart().getMarkerCount() > 0) {
////            point = partRenderData.getSkinPart().getMarker(0);
////            axis = partRenderData.getSkinPart().getMarkerSide(0);
////        }
////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
////        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
////        //GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
////
////        GL11.glTranslated(SCALE * 0.5F, SCALE * 0.5F, SCALE * 0.5F);
////        GL11.glTranslated(SCALE * point.getX(), SCALE * point.getY(), SCALE * point.getZ());
////
////        switch (axis) {
////        case UP:
////            GL11.glRotated(angle, 0, 1, 0);
////            break;
////        case DOWN:
////            GL11.glRotated(angle, 0, -1, 0);
////            break;
////        case SOUTH:
////            GL11.glRotated(angle, 0, 0, -1);
////            break;
////        case NORTH:
////            GL11.glRotated(angle, 0, 0, 1);
////            break;
////        case EAST:
////            GL11.glRotated(angle, 1, 0, 0);
////            break;
////        case WEST:
////            GL11.glRotated(angle, -1, 0, 0);
////            break;
////        }
////
////        GL11.glTranslated(SCALE * -point.getX(), SCALE * -point.getY(), SCALE * -point.getZ());
////        GL11.glTranslated(SCALE * -0.5F, SCALE * -0.5F, SCALE * -0.5F);
////
////        renderPart(partRenderData);
////        GL11.glPopMatrix();
////    }
//}
