package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinLayerRendererZombie extends SkinLayerRendererBibed {
    
    public SkinLayerRendererZombie(RenderZombie renderer) {
        super(renderer);
    }
    
    @Override
    protected void setRotTranForPartType(EntityLivingBase entitylivingbaseIn, ISkinType skinType, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (renderer instanceof RenderZombie) {
            RenderZombie rz = (RenderZombie) renderer;
            boolean isZombieVillager = false;
            isZombieVillager = rz.getMainModel() instanceof ModelZombieVillager;
            
            if (isZombieVillager & skinType == SkinTypeRegistry.skinHead) {
                GL11.glTranslated(0, -2.0F * scale, 0);
            }
            //GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            //GL11.glPolygonOffset(-1F, -1F);
            //model.render(entity, rz.getMainModel(), skin, false, skinPointer.getSkinDye(), null, false, 0, false);
            //GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
    }
}
