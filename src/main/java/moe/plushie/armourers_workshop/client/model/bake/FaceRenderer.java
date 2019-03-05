package moe.plushie.armourers_workshop.client.model.bake;

import moe.plushie.armourers_workshop.client.render.IRenderBuffer;
import moe.plushie.armourers_workshop.client.render.RenderBridge;

public class FaceRenderer {

    private static final IRenderBuffer BUF = RenderBridge.INSTANCE;
    private static final float SCALE = 0.0625F;
    private static final float NORMAL = 0.75F;

    // 0 = west
    // 1 = east
    // 2 = up
    // 3 = down
    // 4 = north
    // 5 = south
    
    // Bottom face   (0.0, 1.0, 0.0)
    // Top face      (0.0, -1.0, 0.0)
    // North face    (0.0, 0.0, -1.0)
    // South face    (0.0, 0.0, 1.0)
    // West face     (1.0, 0.0, 0.0)
    // East face     (-1.0, 0.0, 0.0)

    public static void renderFace(double x, double y, double z, byte r, byte g, byte b, byte a, byte face, byte lodLevel, double texX, double texY, double texU, double texV) {
        if (face == 0) {
            renderNegYFace(x, y, z, r, g, b, a, SCALE * lodLevel, texX, texY, texU, texV);
        }
        if (face == 1) {
            renderPosYFace(x, y, z, r, g, b, a, SCALE * lodLevel, texX, texY, texU, texV);
        }
        if (face == 2) {
            renderNegZFace(x, y, z, r, g, b, a, SCALE * lodLevel, texX, texY, texU, texV);
        }
        if (face == 3) {
            renderPosZFace(x, y, z, r, g, b, a, SCALE * lodLevel, texX, texY, texU, texV);
        }
        if (face == 4) {
            renderNegXFace(x, y, z, r, g, b, a, SCALE * lodLevel, texX, texY, texU, texV);
        }
        if (face == 5) {
            renderPosXFace(x, y, z, r, g, b, a, SCALE * lodLevel, texX, texY, texU, texV);
        }
    }

    public static void renderPosXFace(double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        BUF.addVertexWithUV(x * SCALE, y * SCALE, z * SCALE + scale, texX, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(-1F, 0F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE, y * SCALE + scale, z * SCALE + scale, texX, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(-1F, 0F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE, y * SCALE + scale, z * SCALE, texU, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(-1F, 0F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE, y * SCALE, z * SCALE, texU, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(-1F, 0F, 0F);
        BUF.endVertex();
    }

    public static void renderNegXFace(double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE, z * SCALE, texX, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(1F, 0F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE + scale, z * SCALE, texX, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(1F, 0F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE + scale, z * SCALE + scale, texU, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(1F, 0F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE, z * SCALE + scale, texU, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(1F, 0F, 0F);
        BUF.endVertex();
    }

    public static void renderPosYFace(double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        BUF.addVertexWithUV(x * SCALE, y * SCALE, z * SCALE + scale, texX, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, -1F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE, y * SCALE, z * SCALE, texX, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, -1F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE, z * SCALE, texU, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, -1F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE, z * SCALE + scale, texU, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, -1F, 0F);
        BUF.endVertex();
    }

    public static void renderNegYFace(double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE + scale, z * SCALE + scale, texU, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 1F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE + scale, z * SCALE, texU, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 1F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE, y * SCALE + scale, z * SCALE, texX, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 1F, 0F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE, y * SCALE + scale, z * SCALE + scale, texX, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 1F, 0F);
        BUF.endVertex();
    }

    public static void renderPosZFace(double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE, z * SCALE + scale, texX, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, 1F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE + scale, z * SCALE + scale, texX, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, 1F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE, y * SCALE + scale, z * SCALE + scale, texU, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, 1F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE, y * SCALE, z * SCALE + scale, texU, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, 1F);
        BUF.endVertex();
    }

    public static void renderNegZFace(double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        BUF.addVertexWithUV(x * SCALE, y * SCALE, z * SCALE, texX, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, -1F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE, y * SCALE + scale, z * SCALE, texX, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, -1F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE + scale, z * SCALE, texU, texV);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, -1F);
        BUF.endVertex();

        BUF.addVertexWithUV(x * SCALE + scale, y * SCALE, z * SCALE, texU, texY);
        BUF.setColourRGBA_B(r, g, b, a);
        BUF.setNormal(0F, 0F, -1F);
        BUF.endVertex();
    }
}
