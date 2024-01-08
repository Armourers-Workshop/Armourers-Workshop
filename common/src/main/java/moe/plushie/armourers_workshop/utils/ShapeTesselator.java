package moe.plushie.armourers_workshop.utils;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.utils.math.OpenBoundingBox;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenOrientedBoundingBox;
import moe.plushie.armourers_workshop.utils.math.OpenTransformedBoundingBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class ShapeTesselator {

    public static void point(IVector3f origin, PoseStack poseStack, MultiBufferSource buffers) {
        point(origin.getX(), origin.getY(), origin.getZ(), 1, poseStack, buffers);
    }

    public static void point(IVector3f origin, float length, PoseStack poseStack, MultiBufferSource buffers) {
        point(origin.getX(), origin.getY(), origin.getZ(), length, poseStack, buffers);
    }

    public static void point(float x, float y, float z, float length, PoseStack poseStack, MultiBufferSource buffers) {
        point(x, y, z, length, length, length, poseStack, buffers);
    }

    public static void point(float x, float y, float z, float width, float height, float depth, PoseStack poseStack, MultiBufferSource buffers) {
        auto pose = poseStack.last().pose();
        auto normal = poseStack.last().normal();

        float minX = x - width * 0.5f;
        float minY = y - height * 0.5f;
        float minZ = z - depth * 0.5f;
        float midX = x + 0;
        float midY = y + 0;
        float midZ = z + 0;
        float maxX = x + width * 0.5f;
        float maxY = y + height * 0.5f;
        float maxZ = z + depth * 0.5f;

        auto lineBuilder = buffers.getBuffer(SkinRenderType.lines());

        // x-axis
        lineBuilder.vertex(pose, minX, midY, midZ).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        lineBuilder.vertex(pose, maxX, midY, midZ).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();

        // y-axis
        lineBuilder.vertex(pose, midX, minY, midZ).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        lineBuilder.vertex(pose, midX, maxY, midZ).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();

        // z-axis
        lineBuilder.vertex(pose, midX, midY, minZ).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        lineBuilder.vertex(pose, midX, midY, maxZ).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
    }

    public static void vector(IVector3f origin, PoseStack poseStack, MultiBufferSource buffers) {
        vector(origin.getX(), origin.getY(), origin.getZ(), 1, poseStack, buffers);
    }

    public static void vector(IVector3f origin, float length, PoseStack poseStack, MultiBufferSource buffers) {
        vector(origin.getX(), origin.getY(), origin.getZ(), length, poseStack, buffers);
    }

    public static void vector(float x, float y, float z, float length, PoseStack poseStack, MultiBufferSource buffers) {
        vector(x, y, z, length, length, length, poseStack, buffers);
    }

    public static void vector(float x, float y, float z, float width, float height, float depth, PoseStack poseStack, MultiBufferSource buffers) {
        auto pose = poseStack.last().pose();
        auto normal = poseStack.last().normal();

        float minX = x - width * 0.5f;
        float minY = y - height * 0.5f;
        float minZ = z - depth * 0.5f;
        float midX = x + 0;
        float midY = y + 0;
        float midZ = z + 0;
        float maxX = x + width * 0.5f;
        float maxY = y + height * 0.5f;
        float maxZ = z + depth * 0.5f;

        float n = width * 0.03f;
        float m = height * 0.10f;

        auto lineBuilder = buffers.getBuffer(SkinRenderType.lines());

        // x-axis
        lineBuilder.vertex(pose, minX, midY, midZ).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        lineBuilder.vertex(pose, maxX, midY, midZ).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();

        // y-axis
        lineBuilder.vertex(pose, midX, minY, midZ).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();
        lineBuilder.vertex(pose, midX, maxY, midZ).color(0, 255, 0, 255).normal(normal, 1, 0, 0).endVertex();

        // z-axis
        lineBuilder.vertex(pose, midX, midY, minZ).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();
        lineBuilder.vertex(pose, midX, midY, maxZ).color(0, 0, 255, 255).normal(normal, 1, 0, 0).endVertex();


        auto arrowBuilder = buffers.getBuffer(SkinRenderType.BLIT_COLOR);

        // x-arrow
        arrowBuilder.vertex(pose, maxX - 0, midY + 0, midZ - 0).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY - n, midZ - n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY + n, midZ - n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - 0, midY + 0, midZ - 0).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY - n, midZ + n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY + n, midZ + n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - 0, midY + 0, midZ - 0).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY + n, midZ - n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY + n, midZ + n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - 0, midY + 0, midZ - 0).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY - n, midZ - n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY - n, midZ + n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        // x-arrow-c
        arrowBuilder.vertex(pose, maxX - m, midY - n, midZ - n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY + n, midZ - n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY + n, midZ + n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY + n, midZ + n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY - n, midZ + n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();
        arrowBuilder.vertex(pose, maxX - m, midY - n, midZ - n).color(255, 0, 0, 255).normal(normal, 1, 0, 0).endVertex();

        // y-arrow
        arrowBuilder.vertex(pose, midX - 0, maxY - 0, midZ + 0).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX - n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX - n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX - 0, maxY - 0, midZ + 0).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX + n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX + n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX - 0, maxY - 0, midZ + 0).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX - n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX + n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX - 0, maxY - 0, midZ + 0).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX - n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX + n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        // y-arrow-c
        arrowBuilder.vertex(pose, midX - n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX - n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX + n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX + n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX + n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();
        arrowBuilder.vertex(pose, midX - n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(normal, 0, 1, 0).endVertex();

        // z-arrow
        arrowBuilder.vertex(pose, midX - 0, midY + 0, maxZ - 0).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX - n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX - n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX - 0, midY + 0, maxZ - 0).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX + n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX + n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX - 0, midY + 0, maxZ - 0).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX - n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX + n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX - 0, midY + 0, maxZ - 0).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX - n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX + n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        // z-arrow-c
        arrowBuilder.vertex(pose, midX - n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX - n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX + n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX + n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX + n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
        arrowBuilder.vertex(pose, midX - n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(normal, 0, 0, 1).endVertex();
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

    public static void stroke(OpenBoundingBox aabb, UIColor color, PoseStack poseStack, MultiBufferSource buffers) {
        stroke(aabb.getMinX(), aabb.getMinY(), aabb.getMinZ(), aabb.getMaxX(), aabb.getMaxY(), aabb.getMaxZ(), color, poseStack, buffers);
    }

    public static void stroke(OpenOrientedBoundingBox obb, UIColor color, PoseStack poseStack, MultiBufferSource buffers) {
        poseStack.pushPose();
        poseStack.mulPose(obb.getOrientation());
        stroke(obb.getBoundingBox(), color, poseStack, buffers);
        poseStack.popPose();
    }

    public static void stroke(OpenTransformedBoundingBox tbb, UIColor color, PoseStack poseStack, MultiBufferSource buffers) {
        poseStack.pushPose();
        poseStack.mulPoseMatrix(tbb.getTransform());
        poseStack.mulNormalMatrix(new OpenMatrix3f(tbb.getTransform()));
        stroke(tbb.getBoundingBox(), color, poseStack, buffers);
        poseStack.popPose();
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
