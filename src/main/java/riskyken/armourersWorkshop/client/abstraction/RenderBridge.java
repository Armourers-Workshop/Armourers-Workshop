package riskyken.armourersWorkshop.client.abstraction;

import net.minecraft.client.renderer.Tessellator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBridge implements IRenderBuffer {

    public static IRenderBuffer INSTANCE;
    
    Tessellator tessellator;
    
    public static void init() {
        INSTANCE = new RenderBridge();
    }
    
    public RenderBridge() {
        tessellator = Tessellator.instance;
    }
    
    @Override
    public void draw() {
        tessellator.draw();
    }

    @Override
    public void startDrawingQuads() {
        tessellator.startDrawingQuads();
    }

    @Override
    public void startDrawing(int state) {
        tessellator.startDrawing(state);
    }

    @Override
    public void setBrightness(int brightness) {
        tessellator.setBrightness(brightness);
    }
    
    @Override
    public void setColourRGBA_F(float r, float g, float b, float a) {
        tessellator.setColorRGBA_F(r, g, b, a);
    }

    @Override
    public void setNormal(float x, float y, float z) {
        tessellator.setNormal(x, y, z);
    }

    @Override
    public void setTextureUV(double u, double v) {
        tessellator.setTextureUV(u, v);
    }

    @Override
    public void addVertex(double x, double y, double z) {
        tessellator.addVertex(x, y, z);
    }

    @Override
    public void addVertexWithUV(double x, double y, double z, double u, double v) {
        tessellator.addVertexWithUV(x, y, z, u, v);
    }
}
