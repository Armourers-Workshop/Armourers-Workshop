package moe.plushie.armourers_workshop.core.entity;//package moe.plushie.armourers_workshop.core.skin.entity;
//
//import java.util.ArrayList;
//
//import moe.plushie.armourers_workshop.core.skin.type.ISkinType;
//import moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererGhast;
//import moe.plushie.armourers_workshop.core.skin.type.SkinTypeRegistry;
//import net.minecraft.client.renderer.entity.RenderGhast;
//import net.minecraft.client.renderer.entity.RenderLivingBase;
//import net.minecraft.client.renderer.entity.layers.LayerRenderer;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.monster.EntityGhast;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//public class SkinnableEntityGhast extends SkinnableEntity {
//
//    @Override
//    public Class<? extends EntityLivingBase> getEntityClass() {
//        return EntityGhast.class;
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public LayerRenderer<? extends EntityLivingBase> getLayerRenderer(RenderLivingBase renderLivingBase) {
//        if (renderLivingBase instanceof RenderGhast) {
//            return new SkinLayerRendererGhast((RenderGhast) renderLivingBase);
//        }
//        return null;
//    }
//
//    @Override
//    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
//        skinTypes.add(SkinTypes.BIPED_HEAD);
//    }
//
//    @Override
//    public int getSlotsForSkinType(ISkinType skinType) {
//        return 1;
//    }
//}
