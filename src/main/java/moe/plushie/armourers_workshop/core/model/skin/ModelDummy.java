//package moe.plushie.armourers_workshop.core.model.skin;
//
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//@OnlyIn(Dist.CLIENT)
//public class ModelDummy extends ModelTypeHelper {
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
////        GlStateManager.pushAttrib();
////        RenderHelper.enableGUIStandardItemLighting();
////        GlStateManager.enableCull();
////        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
////        GlStateManager.enableBlend();
////        GlStateManager.enableRescaleNormal();
////        for (int i = 0; i < skin.getParts().size(); i++) {
////            GL11.glPushMatrix();
////            SkinPart skinPart = skin.getParts().get(i);
////            Vector3i offset = skinPart.getType().getOffset();
////            GL11.glTranslated(offset.getX() * SCALE, (offset.getY() + 1) * SCALE, offset.getZ() * SCALE);
////            SkinPartRenderer.INSTANCE.renderPart(new SkinPartRenderData(skinPart, renderData));
////            GL11.glPopMatrix();
////        }
////        GlStateManager.disableRescaleNormal();
////        // GlStateManager.disableBlend();
////        GlStateManager.disableCull();
////        GlStateManager.popAttrib();
////        GlStateManager.color(1F, 1F, 1F, 1F);
////    }
//}
