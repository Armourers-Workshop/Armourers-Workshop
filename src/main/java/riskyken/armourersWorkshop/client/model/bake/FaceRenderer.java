package riskyken.armourersWorkshop.client.model.bake;

import riskyken.plushieWrapper.client.IRenderBuffer;
import riskyken.plushieWrapper.client.RenderBridge;

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
        BUF.setNormal(-NORMAL, 0F, 0F);
        BUF.setColourRGBA_B(r, g, b, a);
        addVertex(x * SCALE, y * SCALE, z * SCALE + scale, 0, 0, textured);
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE + scale, 0, 1, textured);
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE, 1, 1, textured);
        addVertex(x * SCALE, y * SCALE, z * SCALE, 1, 0, textured);
    }
    
    public static void renderNegXFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        BUF.setNormal(NORMAL, 0F, 0F);
        BUF.setColourRGBA_B(r, g, b, a);
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE, 0, 0, textured);
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE, 0, 1, textured);
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE + scale, 1, 1, textured);
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE + scale, 1, 0, textured);
    }
    
    public static void renderPosYFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        BUF.setNormal(0F, -NORMAL, 0F);
        BUF.setColourRGBA_B(r, g, b, a);
        addVertex(x * SCALE, y * SCALE, z * SCALE + scale , 0 ,0, textured);
        addVertex(x * SCALE, y * SCALE, z * SCALE , 0, 1, textured);
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE, 1, 1, textured);
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE + scale, 1, 0, textured);
    }
    
    public static void renderNegYFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        BUF.setNormal(0F, NORMAL, 0F);
        BUF.setColourRGBA_B(r, g, b, a);
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE + scale, 1, 1, textured);
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE, 1, 0, textured);
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE , 0, 0, textured);
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE + scale , 0 ,1, textured);
    }
    
    public static void renderPosZFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        BUF.setNormal(0F, 0F, NORMAL);
        BUF.setColourRGBA_B(r, g, b, a);
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE + scale, 0, 0, textured);
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE + scale, 0, 1, textured);
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE + scale, 1, 1, textured);
        addVertex(x * SCALE, y * SCALE, z * SCALE + scale, 1, 0, textured);
    }
    
    public static void renderNegZFace(double x, double y, double z, byte r, byte g, byte b, byte a, boolean textured, float scale) {
        BUF.setNormal(0F, 0F, -NORMAL);
        BUF.setColourRGBA_B(r, g, b, a);
        addVertex(x * SCALE, y * SCALE, z * SCALE, 0, 0, textured);
        addVertex(x * SCALE, y * SCALE + scale, z * SCALE, 0, 1, textured);
        addVertex(x * SCALE + scale, y * SCALE + scale, z * SCALE, 1, 1, textured);
        addVertex(x * SCALE + scale, y * SCALE, z * SCALE, 1, 0, textured);
    }
    
    private static void addVertex(double x, double y, double z, double u, double v, boolean textured) {
        if (textured) {
            BUF.addVertexWithUV(x, y, z, u, v);
        } else {
            BUF.addVertex(x, y, z);
        }
    }
}
