package moe.plushie.armourers_workshop.common.skin.entity;

import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererDummy;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SkinnableEntity implements ISkinnableEntity {
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addRenderLayer(RenderManager renderManager) {
        Render<EntityLivingBase> renderer = renderManager.getEntityClassRenderObject(getEntityClass());
        if (renderer != null && renderer instanceof RenderLivingBase) {
            LayerRenderer<? extends EntityLivingBase> layerRenderer = getLayerRenderer((RenderLivingBase) renderer);
            if (layerRenderer != null) {
                ((RenderLivingBase<?>) renderer).addLayer(layerRenderer);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public LayerRenderer<? extends EntityLivingBase> getLayerRenderer(RenderLivingBase renderLivingBase) {
        return new SkinLayerRendererDummy(renderLivingBase);
    }
    
    @Override
    public boolean canUseWandOfStyle(EntityPlayer user) {
        return true;
    }

    @Override
    public boolean canUseSkinsOnEntity() {
        return false;
    }
}
