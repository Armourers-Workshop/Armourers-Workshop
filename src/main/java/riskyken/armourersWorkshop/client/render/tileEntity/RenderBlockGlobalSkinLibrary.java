package riskyken.armourersWorkshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.client.model.block.ModelBlockGlobalSkinLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;

public class RenderBlockGlobalSkinLibrary extends TileEntitySpecialRenderer {

    private static final ModelBlockGlobalSkinLibrary GLOBE_MODEL = new ModelBlockGlobalSkinLibrary();
    private static final float SCALE = 0.0625F;
    
    public void renderTileEntityAt(TileEntityGlobalSkinLibrary tileEntity, double x, double y, double z, float partialTickTime) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        GL11.glScalef(-1, -1, 1);
        GLOBE_MODEL.render(tileEntity, partialTickTime, SCALE);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        renderTileEntityAt((TileEntityGlobalSkinLibrary)tileEntity, x, y, z, partialTickTime);
    }
}
