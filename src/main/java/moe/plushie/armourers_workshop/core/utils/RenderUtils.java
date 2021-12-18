package moe.plushie.armourers_workshop.core.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;

import java.awt.*;

public final class RenderUtils {

    public static void bindTexture(ResourceLocation resourceLocation) {
//        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
    }

    private static void drawLine(IVertexBuilder builder, Matrix4f mat, float x0, float y0, float z0, float x1, float y1, float z1, Color color) {
        builder.vertex(mat, x0, y0, z0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        builder.vertex(mat, x1, y1, z1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    public static void drawBoundingBox(Matrix4f mat, float x0, float y0, float z0, float x1, float y1, float z1, Color color) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
        drawLine(builder, mat, x1, y0, z1, x0, y0, z1, color);
        drawLine(builder, mat, x1, y0, z1, x1, y1, z1, color);
        drawLine(builder, mat, x1, y0, z1, x1, y0, z0, color);
        drawLine(builder, mat, x1, y1, z0, x0, y1, z0, color);
        drawLine(builder, mat, x1, y1, z0, x1, y0, z0, color);
        drawLine(builder, mat, x1, y1, z0, x1, y1, z1, color);
        drawLine(builder, mat, x0, y1, z1, x1, y1, z1, color);
        drawLine(builder, mat, x0, y1, z1, x0, y0, z1, color);
        drawLine(builder, mat, x0, y1, z1, x0, y1, z0, color);
        drawLine(builder, mat, x0, y0, z0, x1, y0, z0, color);
        drawLine(builder, mat, x0, y0, z0, x0, y1, z0, color);
        drawLine(builder, mat, x0, y0, z0, x0, y0, z1, color);
    }

    public static void drawBoundingBox(MatrixStack matrix, VoxelShape shape, Color color) {
        AxisAlignedBB box = shape.bounds();
        float x0 = (float) box.minX;
        float y0 = (float) box.minY;
        float z0 = (float) box.minZ;
        float x1 = (float) box.maxX;
        float y1 = (float) box.maxY;
        float z1 = (float) box.maxZ;
        drawBoundingBox(matrix.last().pose(), x0, y0, z0, x1, y1, z1, color);
    }

    public static void drawAllEdges(MatrixStack matrix, VoxelShape shape, Color color) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
        Matrix4f mat = matrix.last().pose();
        shape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            builder.vertex(mat, (float) x0, (float) y0, (float) z0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            builder.vertex(mat, (float) x1, (float) y1, (float) z1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        });
    }

    public static void drawBoundingBox(MatrixStack matrix, Rectangle3D rec, Color color) {
        int x0 = rec.getMinX();
        int y0 = rec.getMinY();
        int z0 = rec.getMinZ();
        int x1 = rec.getMaxX();
        int y1 = rec.getMaxY();
        int z1 = rec.getMaxZ();
        drawBoundingBox(matrix.last().pose(), x0, y0, z0, x1, y1, z1, color);
//
//        float scale = 1F / 16F;
//        AxisAlignedBB aabb = new AxisAlignedBB(
//                rec.getX() * scale, rec.getY() * scale, rec.getZ() * scale,
//                (rec.getX() + rec.getWidth()) * scale, (rec.getY() + rec.getHeight()) * scale, (rec.getZ() + rec.getDepth()) * scale);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glDisable(GL11.GL_LIGHTING);
////        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
//        GL11.glColor4f((float)r / 255F, (float)g / 255F, (float)b / 255F, 1F);
//        GL11.glLineWidth(1.0F);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        //GL11.glDepthMask(false);
//
////        RenderGlobal.drawSelectionBoundingBox(aabb, r / 255F, g / 255F, b / 255F, 1);
//
//        //GL11.glDepthMask(true);
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL11.glEnable(GL11.GL_LIGHTING);
//        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glColor4f(1, 1, 1, 1);
    }


    private static float lightX;
    private static float lightY;

    public static void disableLighting() {
//        net.minecraft.client.GameSettings
//        lightX = OpenGlHelper.lastBrightnessX;
//        lightY = OpenGlHelper.lastBrightnessY;
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
    }

    public static void enableLighting() {
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
    }

//    public static void setLightingForBlock(World world, BlockPos pos) {
//        int i = world.getCombinedLight(pos, 0);
//        int j = i % 65536;
//        int k = i / 65536;
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
//    }

}
