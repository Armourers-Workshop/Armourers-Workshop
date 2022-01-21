package moe.plushie.armourers_workshop.core.entity;//package moe.plushie.armourers_workshop.core.skin.entity;
//
//import moe.plushie.armourers_workshop.core.api.common.skin.entity.ISkinnableEntity;
//import net.minecraft.entity.player.PlayerEntity;
//
//public abstract class SkinnableEntity implements ISkinnableEntity {
//
//    // TODO: IMP
////    @Override
////    public void addRenderLayer(RenderManager renderManager) {
////        Render<EntityLivingBase> renderer = renderManager.getEntityClassRenderObject(getEntityClass());
////        if (renderer != null && renderer instanceof RenderLivingBase) {
////            LayerRenderer<? extends EntityLivingBase> layerRenderer = getLayerRenderer((RenderLivingBase) renderer);
////            if (layerRenderer != null) {
////                ((RenderLivingBase<?>) renderer).addLayer(layerRenderer);
////            }
////        }
////    }
////
////    public LayerRenderer<? extends EntityLivingBase> getLayerRenderer(RenderLivingBase renderLivingBase) {
////        return new SkinLayerRendererDummy(renderLivingBase);
////    }
//
//    @Override
//    public boolean canUseWandOfStyle(PlayerEntity user) {
//        return true;
//    }
//
//    @Override
//    public boolean canUseSkinsOnEntity() {
//        return false;
//    }
//}
