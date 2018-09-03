package moe.plushie.armourers_workshop.client.render;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRenderBuffer {


    public void draw();
    
    public void startDrawingQuads(VertexFormat vertexFormat);
    
    public void startDrawing(int glMode, VertexFormat vertexFormat);
    
    public void setColourRGBA_F(float r, float g, float b, float a);
    
    public void setColourRGBA_B(byte r, byte g, byte b, byte a);
    
    public void setNormal(float x, float y, float z);
    
    public void setTextureUV(double u, double v);
    
    public void addVertex(double x, double y, double z);
    
    public void addVertexWithUV(double x, double y, double z, double u, double v);
    
    public void lightmap(int j, int k);
    
    public void endVertex();
}
