package moe.plushie.armourers_workshop.core.entity;//package moe.plushie.armourers_workshop.core.skin.entity;
//
//import java.util.ArrayList;
//
//import moe.plushie.armourers_workshop.core.skin.type.ISkinType;
//import moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererBibed;
//import moe.plushie.armourers_workshop.core.skin.type.SkinTypeRegistry;
//import net.minecraft.client.renderer.entity.RenderLivingBase;
//import net.minecraft.client.renderer.entity.RenderSkeleton;
//import net.minecraft.client.renderer.entity.layers.LayerRenderer;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.monster.EntitySkeleton;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//public class SkinnableEntitySkeleton extends SkinnableEntity {
//
//    @Override
//    public Class<? extends EntityLivingBase> getEntityClass() {
//        return EntitySkeleton.class;
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public LayerRenderer<? extends EntityLivingBase> getLayerRenderer(RenderLivingBase renderLivingBase) {
//        if (renderLivingBase instanceof RenderSkeleton) {
//            return new SkinLayerRendererBibed((RenderSkeleton) renderLivingBase);
//        }
//        return null;
//    }
//
//    @Override
//    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
//        skinTypes.add(SkinTypes.BIPED_OUTFIT);
//        skinTypes.add(SkinTypes.BIPED_HEAD);
//        skinTypes.add(SkinTypes.BIPED_CHEST);
//        skinTypes.add(SkinTypes.BIPED_LEGS);
//        skinTypes.add(SkinTypes.BIPED_FEET);
//        skinTypes.add(SkinTypes.BIPED_WINGS);
//    }
//
//    @Override
//    public int getSlotsForSkinType(ISkinType skinType) {
//        return 2;
//    }
//}
