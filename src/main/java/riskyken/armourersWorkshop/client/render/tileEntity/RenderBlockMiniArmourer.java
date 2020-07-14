package riskyken.armourersWorkshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.block.ModelBlockArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;

@SideOnly(Side.CLIENT)
public class RenderBlockMiniArmourer extends TileEntitySpecialRenderer {
    
    private static final ModelBlockArmourer modelArmourer = new ModelBlockArmourer();
    
    public void renderTileEntityAt(TileEntityMiniArmourer tileEntity, double x, double y, double z, float tickTime) {
    	Minecraft mc = Minecraft.getMinecraft();
    	mc.mcProfiler.startSection("armourersMiniArmourer");
    	float scale = 0.0625F;
        
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);
        GL11.glScalef(-1, -1, 1);
        
        modelArmourer.render(tileEntity, tickTime, scale);
        
        GL11.glTranslated(0D, -0.5D, 0D);
        
        ISkinType skinType = tileEntity.getSkinType();
        if (skinType != null) {
            float rotation = (float)((double)System.currentTimeMillis() / 25 % 360);
            GL11.glRotatef(rotation, 0F, 1F, 0F);
            bindTexture(Minecraft.getMinecraft().thePlayer.getLocationSkin());
            //SkinRenderHelper.renderBuildingGuide(skinType, scale, true, false);
            
            //skinType.renderBuildingGrid(scale);
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        mc.mcProfiler.endSection();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        renderTileEntityAt((TileEntityMiniArmourer)tileEntity, x, y, z, tickTime);
    }
}
