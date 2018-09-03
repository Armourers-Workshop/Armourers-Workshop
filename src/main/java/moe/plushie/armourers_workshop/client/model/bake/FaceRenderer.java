package moe.plushie.armourers_workshop.client.model.bake;

import moe.plushie.armourers_workshop.client.render.IRenderBuffer;
import moe.plushie.armourers_workshop.client.render.RenderBridge;

public class FaceRenderer {
    
    private static final IRenderBuffer BUF = RenderBridge.INSTANCE;
    private static final float SCALE = 0.0625F;
    private static final float NORMAL = 0.75F;
    
    //0 = west
    //1 = east
    //2 = up
    //3 = down
    //4 = north
    //5 = south
    
    //Bottom face   (0.0, 1.0, 0.0)
    //Top face      (0.0, -1.0, 0.0)
    //North face    (0.0, 0.0, -1.0)
    //South face    (0.0, 0.0, 1.0)
    //West face     (1.0, 0.0, 0.0)
    //East face     (-1.0, 0.0, 0.0)
    
    public static void renderFace(double x, double y, double z, byte r, byte g, byte b, byte a, byte face, boolean textured, byte lodLevel) {
        if (face == 0) {
            renderNegYFace(x, y, z, r, g, b, a, textured, SCALE * lodLevel);
        }
        if (face == 1) {
            renderPosYFace(x, y, z, r, g, b, a, textured, SCALE * lodLevel);
        }
        if (face == 2) {
            renderNegZFace(x, y, z, r, g, b, a, textured, SCALE * lodLevel);
        }
        if (face == 3) {
            renderPosZFace(x, y, z, r, g, b, a, textured, SCALE * lodLevel);
        }
        if (face == 4) {
            renderNegXFace(x, y, z, r, g, b, a, textured, SCALE * lodLevel);
        }
        if (face == 5) {
            renderPosXFace(x, y, z, r, g, b, a, textured, SCALE * lodLevel);
        }
    }
    
    public static void renderPosXFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        addVertex(x * SCALE, y * SCALE, z * SCALE + scale, 0, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(-1F, 0F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE + scale, 0, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(-1F, 0F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE, 1, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(-1F, 0F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE, y * SCALE, z * SCALE, 1, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(-1F, 0F, 0F);
        BUF.endVertex();
    }
    
    public static void renderNegXFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE, 0, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(1F, 0F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE, 0, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(1F, 0F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE + scale, 1, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(1F, 0F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE + scale, 1, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(1F, 0F, 0F);
        BUF.endVertex();
    }
    
    public static void renderPosYFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        addVertex(x * SCALE, y * SCALE, z * SCALE + scale , 0 ,0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, -1F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE, y * SCALE, z * SCALE , 0, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, -1F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE, 1, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, -1F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE + scale, 1, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, -1F, 0F);
        BUF.endVertex();
    }
    
    public static void renderNegYFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE + scale, 1, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 1F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE, 1, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 1F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE , 0, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 1F, 0F);
        BUF.endVertex();
        
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE + scale , 0 ,1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 1F, 0F);
        BUF.endVertex();
    }
    
    public static void renderPosZFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE + scale, 0, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, 1F);
        BUF.endVertex();
        
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE + scale, 0, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, 1F);
        BUF.endVertex();
        
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE + scale, 1, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, 1F);
        BUF.endVertex();
        
        addVertex(x * SCALE, y * SCALE, z * SCALE + scale, 1, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, 1F);
        BUF.endVertex();
    }
    
    public static void renderNegZFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        addVertex(x * SCALE, y * SCALE, z * SCALE, 0, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, -1F);
        BUF.endVertex();
        
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE, 0, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, -1F);
        BUF.endVertex();
        
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE, 1, 1, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, -1F);
        BUF.endVertex();
        
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE, 1, 0, textured);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, -1F);
        BUF.endVertex();
    }
    
    private static void addVertex(double x, double y, double z, double u, double v, boolean textured) {
        if (textured) {
            BUF.addVertexWithUV(x, y, z, u, v);
        } else {
            BUF.addVertexWithUV(x, y, z, u, v);
            //BUF.addVertex(x, y, z);
            //BUF.setTextureUV(0, 0);
        }
    }
}
