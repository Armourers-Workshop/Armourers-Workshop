package riskyken.armourersWorkshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.painting.PaintType;
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
            if (paintType != 255) {
                ModRenderHelper.disableLighting();
                
                GL11.glDisable(GL11.GL_LIGHTING);
                bindTexture(MARKERS);
                GL11.glColor3f(0.77F, 0.77F, 0.77F);
                PaintType pt = PaintType.getPaintTypeFromUKey(paintType);
                renderFaceWithMarker(x, y, z, dir, pt.ordinal());
                GL11.glColor3f(1F, 1F, 1F);
                ModRenderHelper.enableLighting();
                RenderHelper.enableStandardItemLighting();
            }
        }
    }
    
    private void renderFaceWithMarker(double x, double y, double z, ForgeDirection face, int marker) {
        float tileScale = 0.25F;
        float ySrc = (float) Math.floor((double)marker / 4F);
        float xSrc = marker - (ySrc * 4);
        float xStart = tileScale * xSrc;
        float yStart = tileScale * ySrc;
        float xEnd = xStart + tileScale * 1;
        float yEnd = yStart + tileScale * 1;
        float offset = 0.001F;
        switch (face) {
        case DOWN:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x, y - offset, z, xStart, yEnd);
            renderer.addVertexWithUV(x + 1F, y - offset, z, xEnd, yEnd);
            renderer.addVertexWithUV(x + 1F, y - offset, z + 1F, xEnd, yStart);
            renderer.addVertexWithUV(x, y - offset, z + 1F, xStart, yStart);
            renderer.draw();
            break;
        case UP:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x, y + 1F + offset, z + 1F, xStart, yEnd);
            renderer.addVertexWithUV(x + 1F, y + 1F + offset, z + 1F, xEnd, yEnd);
            renderer.addVertexWithUV(x + 1F, y + 1F + offset, z, xEnd, yStart);
            renderer.addVertexWithUV(x, y + 1F + offset, z, xStart, yStart);
            renderer.draw();
            break;
        case NORTH:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x + 1F, y, z - offset, xStart, yEnd);
            renderer.addVertexWithUV(x, y, z - offset, xEnd, yEnd);
            renderer.addVertexWithUV(x, y + 1F, z - offset, xEnd, yStart);
            renderer.addVertexWithUV(x + 1F, y + 1F, z - offset, xStart, yStart);
            renderer.draw();
            break;
        case SOUTH:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x, y, z + 1F + offset, xStart, yEnd);
            renderer.addVertexWithUV(x + 1F, y, z + 1F + offset, xEnd, yEnd);
            renderer.addVertexWithUV(x + 1F, y + 1F, z + 1F + offset, xEnd, yStart);
            renderer.addVertexWithUV(x, y + 1F, z + 1F + offset, xStart, yStart);
            renderer.draw();
            break;
        case WEST:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x - offset, y, z , xStart, yEnd);
            renderer.addVertexWithUV(x - offset, y, z + 1F, xEnd, yEnd);
            renderer.addVertexWithUV(x - offset, y + 1F, z + 1F, xEnd, yStart);
            renderer.addVertexWithUV(x - offset, y + 1F, z, xStart, yStart);
            renderer.draw();
            break;
        case EAST:
            renderer.startDrawingQuads();
            renderer.addVertexWithUV(x + 1 + offset, y, z + 1F, xStart, yEnd);
            renderer.addVertexWithUV(x + 1 + offset, y, z, xEnd, yEnd);
            renderer.addVertexWithUV(x + 1 + offset, y + 1F, z, xEnd, yStart);
            renderer.addVertexWithUV(x + 1 + offset, y + 1F, z + 1F, xStart, yStart);
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
