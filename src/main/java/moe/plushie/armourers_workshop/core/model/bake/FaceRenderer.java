package moe.plushie.armourers_workshop.core.model.bake;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;


public class FaceRenderer {

    public static class RE {
        public Matrix4f matrix;
        public IVertexBuilder builder;


        public void setColourRGBA_F(float r, float g, float b, float a) {
            builder = builder.color(r, g, b, a);
        }

        public void setColourRGBA_B(byte r, byte g, byte b, byte a) {
            builder = builder.color(r, g, b, a);
        }

        public void setNormal(float x, float y, float z) {
           builder = builder.normal(x, y, z);
        }

        public void setTextureUV(double u, double v) {
            builder = builder.uv((float)u, (float)v);
        }

        public void addVertex(double x, double y, double z) {
            builder = builder.vertex(matrix, (float)x, (float)y, (float)z);
        }

        public void addVertexWithUV(double x, double y, double z, double u, double v) {
            builder = builder.vertex(matrix, (float)x,(float)y, (float)z);//.uv((float)u, (float)v);
        }
        public void endVertex() {
            builder.endVertex();
        }

    }

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

    public static void renderFace(RE buf, double x, double y, double z, byte r, byte g, byte b, byte a, byte face, byte lodLevel, double texX, double texY, double texU, double texV) {
        if (face == 0) {
            renderNegYFace(buf, x, y, z, r, g, b, a, lodLevel, texX, texY, texU, texV);
        }
        if (face == 1) {
            renderPosYFace(buf, x, y, z, r, g, b, a, lodLevel, texX, texY, texU, texV);
        }
        if (face == 2) {
            renderNegZFace(buf, x, y, z, r, g, b, a, lodLevel, texX, texY, texU, texV);
        }
        if (face == 3) {
            renderPosZFace(buf, x, y, z, r, g, b, a, lodLevel, texX, texY, texU, texV);
        }
        if (face == 4) {
            renderNegXFace(buf, x, y, z, r, g, b, a, lodLevel, texX, texY, texU, texV);
        }
        if (face == 5) {
            renderPosXFace(buf, x, y, z, r, g, b, a, lodLevel, texX, texY, texU, texV);
        }
    }

    public static void renderPosXFace(RE buf, double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        buf.addVertexWithUV(x, y, z + scale, texX, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(-1F, 0F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x, y + scale, z + scale, texX, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(-1F, 0F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x, y + scale, z, texU, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(-1F, 0F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x, y, z, texU, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(-1F, 0F, 0F);
        buf.endVertex();
    }

    public static void renderNegXFace(RE buf, double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        buf.addVertexWithUV(x + scale, y, z, texX, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(1F, 0F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x + scale, y + scale, z, texX, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(1F, 0F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x + scale, y + scale, z + scale, texU, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(1F, 0F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x + scale, y, z + scale, texU, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(1F, 0F, 0F);
        buf.endVertex();
    }

    public static void renderPosYFace(RE buf, double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        buf.addVertexWithUV(x, y, z + scale, texX, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, -1F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x, y, z, texX, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, -1F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x + scale, y, z, texU, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, -1F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x + scale, y, z + scale, texU, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, -1F, 0F);
        buf.endVertex();
    }

    public static void renderNegYFace(RE buf, double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        buf.addVertexWithUV(x + scale, y + scale, z + scale, texU, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 1F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x + scale, y + scale, z, texU, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 1F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x, y + scale, z, texX, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 1F, 0F);
        buf.endVertex();

        buf.addVertexWithUV(x, y + scale, z + scale, texX, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 1F, 0F);
        buf.endVertex();
    }

    public static void renderPosZFace(RE buf, double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        buf.addVertexWithUV(x + scale, y, z + scale, texX, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 0F, 1F);
        buf.endVertex();

        buf.addVertexWithUV(x + scale, y + scale, z + scale, texX, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 0F, 1F);
        buf.endVertex();

        buf.addVertexWithUV(x, y + scale, z + scale, texU, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 0F, 1F);
        buf.endVertex();

        buf.addVertexWithUV(x, y, z + scale, texU, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 0F, 1F);
        buf.endVertex();
    }

    public static void renderNegZFace(RE buf, double x, double y, double z, byte r, byte g, byte b, byte a, float scale, double texX, double texY, double texU, double texV) {
        buf.addVertexWithUV(x, y, z, texX, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 0F, -1F);
        buf.endVertex();

        buf.addVertexWithUV(x, y + scale, z, texX, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 0F, -1F);
        buf.endVertex();

        buf.addVertexWithUV(x + scale, y + scale, z, texU, texV);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 0F, -1F);
        buf.endVertex();

        buf.addVertexWithUV(x + scale, y, z, texU, texY);
        buf.setColourRGBA_B(r, g, b, a);
        buf.setNormal(0F, 0F, -1F);
        buf.endVertex();
    }
}
