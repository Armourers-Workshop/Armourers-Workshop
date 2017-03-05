package riskyken.plushieWrapper.client;

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
    public void startDrawing(int drawMode) {
        tessellator.startDrawing(drawMode);
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
    public void setColourRGBA_B(byte r, byte g, byte b, byte a) {
        tessellator.setColorRGBA(r & 0xFF, g & 0xFF, b & 0xFF, a & 0xFF);
    }
    
    @Override
    public void setColorOpaque_F(float r, float g, float b) {
        tessellator.setColorOpaque_F(r, g, b);
    }
    
    @Override
    public void setColorOpaque_I(int r, int g, int b) {
        tessellator.setColorOpaque(r, g, b);
    }
    
    @Override
    public void setColorOpaque_B(byte r, byte g, byte b) {
        tessellator.setColorOpaque(r & 0xFF, g & 0xFF, b & 0xFF);
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
