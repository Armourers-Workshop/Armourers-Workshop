package riskyken.armourersWorkshop.client.render.block;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.armourer.ModelBlockArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMiniArmourer extends TileEntitySpecialRenderer {
    
    private static final ModelBlockArmourer modelArmourer = new ModelBlockArmourer();
    
    public void renderTileEntityAt(TileEntityMiniArmourer tileEntity, double x, double y, double z, float tickTime) {
        float scale = 0.0625F;
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);
        GL11.glScalef(-1, -1, 1);
        modelArmourer.render(tileEntity, tickTime, scale);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        renderTileEntityAt((TileEntityMiniArmourer)tileEntity, x, y, z, tickTime);
    }
}
