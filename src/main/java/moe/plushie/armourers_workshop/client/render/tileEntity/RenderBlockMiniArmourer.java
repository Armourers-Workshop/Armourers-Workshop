package moe.plushie.armourers_workshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.block.ModelBlockArmourer;
import moe.plushie.armourers_workshop.client.render.SkinRenderHelper;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMiniArmourer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMiniArmourer extends TileEntitySpecialRenderer<TileEntityMiniArmourer> {
    
    private static final ModelBlockArmourer modelArmourer = new ModelBlockArmourer();
    
    @Override
    public void render(TileEntityMiniArmourer te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    	Minecraft mc = Minecraft.getMinecraft();
    	mc.profiler.startSection("armourersMiniArmourer");
    	float scale = 0.0625F;
        
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);
        GL11.glScalef(-1, -1, 1);
        
        modelArmourer.render(te, partialTicks, scale);
        
        GL11.glTranslated(0D, -0.5D, 0D);
        
        ISkinType skinType = te.getSkinType();
        if (skinType != null) {
            float rotation = (float)((double)System.currentTimeMillis() / 25 % 360);
            GL11.glRotatef(rotation, 0F, 1F, 0F);
            bindTexture(Minecraft.getMinecraft().player.getLocationSkin());
            SkinRenderHelper.renderBuildingGuide(skinType, scale, true, false);
            
            //skinType.renderBuildingGrid(scale);
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        mc.profiler.endSection();
    }
}
