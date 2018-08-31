package riskyken.armourers_workshop.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBridge implements IRenderBuffer {

    public static IRenderBuffer INSTANCE = new RenderBridge();
    
    Tessellator tessellator;
    
    public static void init() {
        INSTANCE = new RenderBridge();
    }
    
    public RenderBridge() {
        tessellator = Tessellator.getInstance();
    }
    
    @Override
    public void draw() {
        tessellator.draw();
    }

    @Override
    public void startDrawingQuads() {
        tessellator.getBuffer().begin(2, DefaultVertexFormats.POSITION_COLOR);;
    }

    @Override
    public void startDrawing(int drawMode) {
        tessellator.getBuffer().begin(drawMode, DefaultVertexFormats.POSITION_COLOR);
    }

    @Override
    public void setBrightness(int brightness) {
        //tessellator.setBrightness(brightness);
    }
    
    @Override
    public void setColourRGBA_F(float r, float g, float b, float a) {
        tessellator.getBuffer().color(r, g, b, a);
        //tessellator.setColorRGBA_F(r, g, b, a);
    }
    
    @Override
    public void setColourRGBA_B(byte r, byte g, byte b, byte a) {
        tessellator.getBuffer().color(r & 0xFF, g & 0xFF, b & 0xFF, a & 0xFF);
        //tessellator.setColorRGBA(r & 0xFF, g & 0xFF, b & 0xFF, a & 0xFF);
    }
    
    @Override
    public void setColorOpaque_F(float r, float g, float b) {
        tessellator.getBuffer().color(r, g, b, 1F);
        //tessellator.setColorOpaque_F(r, g, b);
    }
    
    @Override
    public void setColorOpaque_I(int r, int g, int b) {
        //tessellator.getBuffer().col
        //tessellator.setColorOpaque(r, g, b);
    }
    
    @Override
    public void setColorOpaque_B(byte r, byte g, byte b) {
        //tessellator.setColorOpaque(r & 0xFF, g & 0xFF, b & 0xFF);
    }

    @Override
    public void setNormal(float x, float y, float z) {
        //tessellator.setNormal(x, y, z);
    }

    @Override
    public void setTextureUV(double u, double v) {
        //tessellator.setTextureUV(u, v);
    }

    @Override
    public void addVertex(double x, double y, double z) {
        //tessellator.addVertex(x, y, z);
    }

    @Override
    public void addVertexWithUV(double x, double y, double z, double u, double v) {
        //tessellator.addVertexWithUV(x, y, z, u, v);
    }
}
