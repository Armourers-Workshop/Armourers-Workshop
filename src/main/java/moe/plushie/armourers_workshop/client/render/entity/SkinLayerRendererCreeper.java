package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinLayerRendererCreeper extends SkinLayerRenderer<EntityCreeper, RenderCreeper> {

    public SkinLayerRendererCreeper(RenderCreeper renderCreeper) {
        super(renderCreeper);
    }
    
    @Override
    protected void setRotTranForPartType(EntityCreeper entitylivingbaseIn, ISkinType skinType, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        
        /*if (entity.deathTime > 0) {
            float angle = ((float)entity.deathTime + ModClientFMLEventHandler.renderTickTime - 1.0F) / 20.0F * 1.6F;
            angle = MathHelper.sqrt(angle);
            if (angle > 1.0F) {
                angle = 1.0F;
            }
            GL11.glRotatef(angle * 90F, 0.0F, 0.0F, 1.0F);
        }*/
        GL11.glTranslated(0, 6.2F * scale, 0);
        
        GL11.glRotated(netHeadYaw, 0, 1, 0);
        GL11.glRotatef(headPitch, 1, 0, 0);
        
        float headScale = 1.001F;
        GL11.glScalef(headScale, headScale, headScale);
    }
}
