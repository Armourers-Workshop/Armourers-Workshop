package moe.plushie.armourers_workshop.utils;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class ShapeTesselator {

    public static void vector(IVector3f origin, PoseStack poseStack, MultiBufferSource buffers) {
        vector(origin.getX(), origin.getY(), origin.getZ(), 1, poseStack, buffers);
    }

    public static void vector(IVector3f origin, float length, PoseStack poseStack, MultiBufferSource buffers) {
        vector(origin.getX(), origin.getY(), origin.getZ(), length, poseStack, buffers);
    }

    public static void vector(float x, float y, float z, float length, PoseStack poseStack, MultiBufferSource buffers) {
        vector(x, y, z, x + length, y + length, z + length, poseStack, buffers);
    }

    public static void vector(float x, float y, float z, float w, float h, float d, PoseStack poseStack, MultiBufferSource buffers) {
        vector(x, y, z, w, h, d, poseStack, buffers.getBuffer(SkinRenderType.lines()));
    }

    public static void vector(float x, float y, float z, float w, float h, float d, PoseStack poseStack, VertexConsumer builder) {
        auto pose = poseStack.last().pose();
        auto normal = poseStack.last().normal();

        float n = 0.020f;
        float m = 0.002f;

        float x0 = x - w * 0.5f;
        float y0 = y - h * 0.5f;
        float z0 = z - d * 0.5f;

        float x1 = x - w * m;
        float y1 = y - h * m;
        float z1 = z - d * m;

        float x2 = x + 0;
        float y2 = y + 0;
        float z2 = z + 0;

        float x3 = x + w * m;
        float y3 = y + h * m;
        float z3 = z + d * m;

        float x4 = x + w * 0.5f;
        float y4 = y + h * 0.5f;
        float z4 = z + d * 0.5f;

        float x5 = x4 - w * n;
        float y5 = y4 - h * n;
        float z5 = z4 - d * n;

        // x-axis
        builder.vertex(pose, x0, y2, z2).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x4, y2, z2).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();

        // y-axis
        builder.vertex(pose, x2, y0, z2).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x2, y4, z2).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();

        // z-axis
        builder.vertex(pose, x2, y2, z0).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x2, y2, z4).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();

        // x-arrow
        builder.vertex(pose, x4, y2, z2).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y1, z1).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x4, y2, z2).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y3, z1).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x4, y2, z2).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y1, z3).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x4, y2, z2).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y3, z3).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y1, z3).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y3, z3).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y3, z3).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y3, z1).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y3, z1).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y1, z1).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y1, z1).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x5, y1, z3).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();

        // y-arrow
        builder.vertex(pose, x2, y4, z2).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y5, z1).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x2, y4, z2).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y5, z1).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x2, y4, z2).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y5, z3).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x2, y4, z2).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y5, z3).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y5, z3).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y5, z3).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y5, z3).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y5, z1).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y5, z1).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y5, z1).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y5, z1).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y5, z3).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();

        // z-arrow
        builder.vertex(pose, x2, y2, z4).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y1, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x2, y2, z4).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y3, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x2, y2, z4).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y1, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x2, y2, z4).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y3, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y3, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y3, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y3, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y1, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y1, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y1, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y1, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x3, y3, z5).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
    }


    public static void stroke(AABB rect, UIColor color, PoseStack poseStack, MultiBufferSource buffers) {
        stroke((float) rect.minX, (float) rect.minY, (float) rect.minZ, (float) rect.maxX, (float) rect.maxY, (float) rect.maxZ, color, poseStack, buffers);
    }

    public static void stroke(IRectangle3f rect, UIColor color, PoseStack poseStack, MultiBufferSource buffers) {
        stroke(rect.getMinX(), rect.getMinY(), rect.getMinZ(), rect.getMaxX(), rect.getMaxY(), rect.getMaxZ(), color, poseStack, buffers);
    }

    public static void stroke(IRectangle3i rect, UIColor color, PoseStack poseStack, MultiBufferSource buffers) {
        stroke(rect.getMinX(), rect.getMinY(), rect.getMinZ(), rect.getMaxX(), rect.getMaxY(), rect.getMaxZ(), color, poseStack, buffers);
    }

    public static void stroke(float x0, float y0, float z0, float x1, float y1, float z1, UIColor color, PoseStack poseStack, MultiBufferSource buffers) {
        fill(x0, y0, z0, x1, y1, z1, color, poseStack, buffers.getBuffer(SkinRenderType.lines()));
    }

    public static void fill(float x0, float y0, float z0, float x1, float y1, float z1, UIColor color, PoseStack poseStack, VertexConsumer builder) {
        auto pose = poseStack.last().pose();
        auto normal = poseStack.last().normal();
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        builder.vertex(pose, x0, y0, z0).color(r, g, b, a).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y0, z0).color(r, g, b, a).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x0, y0, z0).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, x0, y1, z0).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, x0, y0, z0).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
        builder.vertex(pose, x0, y0, z1).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
        builder.vertex(pose, x1, y0, z0).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, x1, y1, z0).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, x1, y1, z0).color(r, g, b, a).normal(normal, -1, 0, 0).endVertex();
        builder.vertex(pose, x0, y1, z0).color(r, g, b, a).normal(normal, -1, 0, 0).endVertex();
        builder.vertex(pose, x0, y1, z0).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
        builder.vertex(pose, x0, y1, z1).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
        builder.vertex(pose, x0, y1, z1).color(r, g, b, a).normal(normal, 0, -1, 0).endVertex();
        builder.vertex(pose, x0, y0, z1).color(r, g, b, a).normal(normal, 0, -1, 0).endVertex();
        builder.vertex(pose, x0, y0, z1).color(r, g, b, a).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y0, z1).color(r, g, b, a).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y0, z1).color(r, g, b, a).normal(normal, 0, 0, -1).endVertex();
        builder.vertex(pose, x1, y0, z0).color(r, g, b, a).normal(normal, 0, 0, -1).endVertex();
        builder.vertex(pose, x0, y1, z1).color(r, g, b, a).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y1, z1).color(r, g, b, a).normal(normal, 1, 0, 0).endVertex();
        builder.vertex(pose, x1, y0, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, x1, y1, z1).color(r, g, b, a).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(pose, x1, y1, z0).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
        builder.vertex(pose, x1, y1, z1).color(r, g, b, a).normal(normal, 0, 0, 1).endVertex();
    }
}
