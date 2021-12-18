package moe.plushie.armourers_workshop.core.skin.entity;//package moe.plushie.armourers_workshop.core.skin.entity;
//
//import java.util.ArrayList;
//
//import moe.plushie.armourers_workshop.core.skin.type.ISkinType;
//import moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererZombie;
//import moe.plushie.armourers_workshop.core.skin.type.SkinTypeRegistry;
//import net.minecraft.client.renderer.entity.RenderLivingBase;
//import net.minecraft.client.renderer.entity.RenderZombie;
//import net.minecraft.client.renderer.entity.layers.LayerRenderer;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.monster.EntityZombie;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//public class SkinnableEntityZombie extends SkinnableEntity {
//
//    @Override
//    public Class<? extends EntityLivingBase> getEntityClass() {
//        return EntityZombie.class;
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public LayerRenderer<? extends EntityLivingBase> getLayerRenderer(RenderLivingBase renderLivingBase) {
//        if (renderLivingBase instanceof RenderZombie) {
//            return new SkinLayerRendererZombie((RenderZombie) renderLivingBase);
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
