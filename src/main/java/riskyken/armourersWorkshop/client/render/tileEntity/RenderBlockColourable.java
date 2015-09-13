package riskyken.armourersWorkshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.plushieWrapper.client.IRenderBuffer;
import riskyken.plushieWrapper.client.RenderBridge;

public class RenderBlockColourable extends TileEntitySpecialRenderer {
    
    private static final ResourceLocation MARKERS = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/tileEntities/markers.png");
    private IRenderBuffer renderer;
    
    public RenderBlockColourable() {
        renderer = RenderBridge.INSTANCE;
    }
    
    public void renderTileEntityAt(TileEntityColourable tileEntity, double x, double y, double z, float partialTickTime) {
        ICubeColour cubeColour = tileEntity.getColour();
        for (int i = 0; i < 6; i++) {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            int paintType = cubeColour.getPaintType(i) & 0xFF;
            if (paintType == 1) {
                ModRenderHelper.disableLighting();
                
                GL11.glDisable(GL11.GL_LIGHTING);
                bindTexture(MARKERS);
                GL11.glColor3f(0.77F, 0.77F, 0.77F);
                renderFaceWithMarker(x, y, z, dir, 0);
                GL11.glColor3f(1F, 1F, 1F);
                ModRenderHelper.enableLighting();
            }
        }
    }
    
    private void renderFaceWithMarker(double x, double y, double z, ForgeDirection face, int marker) {
        float offset = 0.001F;
        switch (face) {
        case DOWN:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x, y - offset, z, 0F, 1F);
            renderer.addVertexWithUV(x + 1F, y - offset, z, 1F, 1F);
            renderer.addVertexWithUV(x + 1F, y - offset, z + 1F, 1F, 0F);
            renderer.addVertexWithUV(x, y - offset, z + 1F, 0F, 0F);
            renderer.draw();
            break;
        case UP:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x, y + 1F + offset, z + 1F, 0F, 1F);
            renderer.addVertexWithUV(x + 1F, y + 1F + offset, z + 1F, 1F, 1F);
            renderer.addVertexWithUV(x + 1F, y + 1F + offset, z, 1F, 0F);
            renderer.addVertexWithUV(x, y + 1F + offset, z, 0F, 0F);
            renderer.draw();
            break;
        case NORTH:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x, y, z + 1F + offset, 0F, 1F);
            renderer.addVertexWithUV(x + 1F, y, z + 1F + offset, 1F, 1F);
            renderer.addVertexWithUV(x + 1F, y + 1F, z + 1F + offset, 1F, 0F);
            renderer.addVertexWithUV(x, y + 1F, z + 1F + offset, 0F, 0F);
            renderer.draw();
        case SOUTH:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x + 1F, y, z - offset, 0F, 1F);
            renderer.addVertexWithUV(x, y, z - offset, 1F, 1F);
            renderer.addVertexWithUV(x, y + 1F, z - offset, 1F, 0F);
            renderer.addVertexWithUV(x + 1F, y + 1F, z - offset, 0F, 0F);
            renderer.draw();   
            break;
        case WEST:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x + 1 + offset, y, z + 1F, 0F, 1F);
            renderer.addVertexWithUV(x + 1 + offset, y, z, 1F, 1F);
            renderer.addVertexWithUV(x + 1 + offset, y + 1F, z, 1F, 0F);
            renderer.addVertexWithUV(x + 1 + offset, y + 1F, z + 1F, 0F, 0F);
            renderer.draw();
            break;
        case EAST:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x - offset, y, z , 0F, 1F);
            renderer.addVertexWithUV(x - offset, y, z + 1F, 1F, 1F);
            renderer.addVertexWithUV(x - offset, y + 1F, z + 1F, 1F, 0F);
            renderer.addVertexWithUV(x - offset, y + 1F, z, 0F, 0F);
            renderer.draw();
            break;
        default:
            break;
        }
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        renderTileEntityAt((TileEntityColourable)tileEntity, x, y, z, partialTickTime);
    }
}
