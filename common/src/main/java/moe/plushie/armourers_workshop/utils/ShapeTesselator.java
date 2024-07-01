package moe.plushie.armourers_workshop.utils;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.core.armature.JointShape;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.utils.math.OpenBoundingBox;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenOrientedBoundingBox;
import moe.plushie.armourers_workshop.utils.math.OpenTransformedBoundingBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

@Environment(EnvType.CLIENT)
public class ShapeTesselator {

    private static final byte[][][] FACE_MARK_TEXTURES = {
            // 0, 1(w), 2(h), 3(d)
            {{1, 3}, {1, 0}, {0, 0}, {0, 3}},
            {{1, 3}, {1, 0}, {0, 0}, {0, 3}},

            {{1, 2}, {1, 0}, {0, 0}, {0, 2}},
            {{1, 2}, {1, 0}, {0, 0}, {0, 2}},

            {{3, 2}, {3, 0}, {0, 0}, {0, 2}},
            {{3, 2}, {3, 0}, {0, 0}, {0, 2}},
    };

    private static final byte[][][] FACE_MARK_VERTEXES = new byte[][][]{
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // -y
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // +y
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // -z
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // +z
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // -x
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // +x
    };

    public static void point(IVector3f origin, IPoseStack poseStack, IBufferSource bufferSource) {
        point(origin.getX(), origin.getY(), origin.getZ(), 1, poseStack, bufferSource);
    }

    public static void point(IVector3f origin, float length, IPoseStack poseStack, IBufferSource bufferSource) {
        point(origin.getX(), origin.getY(), origin.getZ(), length, poseStack, bufferSource);
    }

    public static void point(float x, float y, float z, float length, IPoseStack poseStack, IBufferSource bufferSource) {
        point(x, y, z, length, length, length, poseStack, bufferSource);
    }

    public static void point(float x, float y, float z, float width, float height, float depth, IPoseStack poseStack, IBufferSource bufferSource) {
        var entry = poseStack.last();

        float minX = x - width * 0.5f;
        float minY = y - height * 0.5f;
        float minZ = z - depth * 0.5f;
        float midX = x + 0;
        float midY = y + 0;
        float midZ = z + 0;
        float maxX = x + width * 0.5f;
        float maxY = y + height * 0.5f;
        float maxZ = z + depth * 0.5f;

        var lineBuilder = bufferSource.getBuffer(SkinRenderType.lines());

        // x-axis
        lineBuilder.vertex(entry, minX, midY, midZ).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        lineBuilder.vertex(entry, maxX, midY, midZ).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();

        // y-axis
        lineBuilder.vertex(entry, midX, minY, midZ).color(0, 255, 0, 255).normal(entry, 1, 0, 0).endVertex();
        lineBuilder.vertex(entry, midX, maxY, midZ).color(0, 255, 0, 255).normal(entry, 1, 0, 0).endVertex();

        // z-axis
        lineBuilder.vertex(entry, midX, midY, minZ).color(0, 0, 255, 255).normal(entry, 1, 0, 0).endVertex();
        lineBuilder.vertex(entry, midX, midY, maxZ).color(0, 0, 255, 255).normal(entry, 1, 0, 0).endVertex();
    }

    public static void vector(IVector3f origin, IPoseStack poseStack, IBufferSource bufferSource) {
        vector(origin.getX(), origin.getY(), origin.getZ(), 1, poseStack, bufferSource);
    }

    public static void vector(IVector3f origin, float length, IPoseStack poseStack, IBufferSource bufferSource) {
        vector(origin.getX(), origin.getY(), origin.getZ(), length, poseStack, bufferSource);
    }

    public static void vector(float x, float y, float z, float length, IPoseStack poseStack, IBufferSource bufferSource) {
        vector(x, y, z, length, length, length, poseStack, bufferSource);
    }

    public static void vector(float x, float y, float z, float width, float height, float depth, IPoseStack poseStack, IBufferSource bufferSource) {
        var entry = poseStack.last();

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

        var lineBuilder = bufferSource.getBuffer(SkinRenderType.lines());

        // x-axis
        lineBuilder.vertex(entry, minX, midY, midZ).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        lineBuilder.vertex(entry, maxX, midY, midZ).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();

        // y-axis
        lineBuilder.vertex(entry, midX, minY, midZ).color(0, 255, 0, 255).normal(entry, 1, 0, 0).endVertex();
        lineBuilder.vertex(entry, midX, maxY, midZ).color(0, 255, 0, 255).normal(entry, 1, 0, 0).endVertex();

        // z-axis
        lineBuilder.vertex(entry, midX, midY, minZ).color(0, 0, 255, 255).normal(entry, 1, 0, 0).endVertex();
        lineBuilder.vertex(entry, midX, midY, maxZ).color(0, 0, 255, 255).normal(entry, 1, 0, 0).endVertex();


        var arrowBuilder = bufferSource.getBuffer(SkinRenderType.BLIT_COLOR);

        // x-arrow
        arrowBuilder.vertex(entry, maxX - 0, midY + 0, midZ - 0).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY - n, midZ - n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY + n, midZ - n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - 0, midY + 0, midZ - 0).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY - n, midZ + n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY + n, midZ + n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - 0, midY + 0, midZ - 0).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY + n, midZ - n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY + n, midZ + n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - 0, midY + 0, midZ - 0).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY - n, midZ - n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY - n, midZ + n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        // x-arrow-c
        arrowBuilder.vertex(entry, maxX - m, midY - n, midZ - n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY + n, midZ - n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY + n, midZ + n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY + n, midZ + n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY - n, midZ + n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();
        arrowBuilder.vertex(entry, maxX - m, midY - n, midZ - n).color(255, 0, 0, 255).normal(entry, 1, 0, 0).endVertex();

        // y-arrow
        arrowBuilder.vertex(entry, midX - 0, maxY - 0, midZ + 0).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX - n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX - n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX - 0, maxY - 0, midZ + 0).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX + n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX + n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX - 0, maxY - 0, midZ + 0).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX - n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX + n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX - 0, maxY - 0, midZ + 0).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX - n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX + n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        // y-arrow-c
        arrowBuilder.vertex(entry, midX - n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX - n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX + n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX + n, maxY - m, midZ + n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX + n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();
        arrowBuilder.vertex(entry, midX - n, maxY - m, midZ - n).color(0, 255, 0, 255).normal(entry, 0, 1, 0).endVertex();

        // z-arrow
        arrowBuilder.vertex(entry, midX - 0, midY + 0, maxZ - 0).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX - n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX - n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX - 0, midY + 0, maxZ - 0).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX + n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX + n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX - 0, midY + 0, maxZ - 0).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX - n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX + n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX - 0, midY + 0, maxZ - 0).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX - n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX + n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        // z-arrow-c
        arrowBuilder.vertex(entry, midX - n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX - n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX + n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX + n, midY + n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX + n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
        arrowBuilder.vertex(entry, midX - n, midY - n, maxZ - m).color(0, 0, 255, 255).normal(entry, 0, 0, 1).endVertex();
    }

    public static void stroke(AABB rect, UIColor color, IPoseStack poseStack, IBufferSource bufferSource) {
        stroke((float) rect.minX, (float) rect.minY, (float) rect.minZ, (float) rect.maxX, (float) rect.maxY, (float) rect.maxZ, color, poseStack, bufferSource);
    }

    public static void stroke(IRectangle3f rect, UIColor color, IPoseStack poseStack, IBufferSource bufferSource) {
        stroke(rect.getMinX(), rect.getMinY(), rect.getMinZ(), rect.getMaxX(), rect.getMaxY(), rect.getMaxZ(), color, poseStack, bufferSource);
    }

    public static void stroke(IRectangle3i rect, UIColor color, IPoseStack poseStack, IBufferSource bufferSource) {
        stroke(rect.getMinX(), rect.getMinY(), rect.getMinZ(), rect.getMaxX(), rect.getMaxY(), rect.getMaxZ(), color, poseStack, bufferSource);
    }

    public static void stroke(OpenBoundingBox aabb, UIColor color, IPoseStack poseStack, IBufferSource bufferSource) {
        stroke(aabb.getMinX(), aabb.getMinY(), aabb.getMinZ(), aabb.getMaxX(), aabb.getMaxY(), aabb.getMaxZ(), color, poseStack, bufferSource);
    }

    public static void stroke(OpenOrientedBoundingBox obb, UIColor color, IPoseStack poseStack, IBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.rotate(obb.getOrientation());
        stroke(obb.getBoundingBox(), color, poseStack, bufferSource);
        poseStack.popPose();
    }

    public static void stroke(OpenTransformedBoundingBox tbb, UIColor color, IPoseStack poseStack, IBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.multiply(tbb.getTransform());
        poseStack.multiply(new OpenMatrix3f(tbb.getTransform()));
        stroke(tbb.getBoundingBox(), color, poseStack, bufferSource);
        poseStack.popPose();
    }

    public static void stroke(JointShape shape, UIColor color, IPoseStack poseStack, IBufferSource bufferSource) {
        poseStack.pushPose();
        var rect = shape.bounds();
        shape.transform().apply(poseStack);
        stroke(rect, color, poseStack, bufferSource);
        poseStack.translate(rect.getX(), rect.getY(), rect.getZ());
        for (var shape1 : shape.children()) {
            stroke(shape1, color, poseStack, bufferSource);
        }
        poseStack.popPose();
    }

    public static void stroke(float x0, float y0, float z0, float x1, float y1, float z1, UIColor color, IPoseStack poseStack, IBufferSource bufferSource) {
        fill(x0, y0, z0, x1, y1, z1, color, poseStack, bufferSource.getBuffer(SkinRenderType.lines()));
    }

    public static void fill(float x0, float y0, float z0, float x1, float y1, float z1, UIColor color, IPoseStack poseStack, IVertexConsumer builder) {
        var entry = poseStack.last();
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        builder.vertex(entry, x0, y0, z0).color(r, g, b, a).normal(entry, 1, 0, 0).endVertex();
        builder.vertex(entry, x1, y0, z0).color(r, g, b, a).normal(entry, 1, 0, 0).endVertex();
        builder.vertex(entry, x0, y0, z0).color(r, g, b, a).normal(entry, 0, 1, 0).endVertex();
        builder.vertex(entry, x0, y1, z0).color(r, g, b, a).normal(entry, 0, 1, 0).endVertex();
        builder.vertex(entry, x0, y0, z0).color(r, g, b, a).normal(entry, 0, 0, 1).endVertex();
        builder.vertex(entry, x0, y0, z1).color(r, g, b, a).normal(entry, 0, 0, 1).endVertex();
        builder.vertex(entry, x1, y0, z0).color(r, g, b, a).normal(entry, 0, 1, 0).endVertex();
        builder.vertex(entry, x1, y1, z0).color(r, g, b, a).normal(entry, 0, 1, 0).endVertex();
        builder.vertex(entry, x1, y1, z0).color(r, g, b, a).normal(entry, -1, 0, 0).endVertex();
        builder.vertex(entry, x0, y1, z0).color(r, g, b, a).normal(entry, -1, 0, 0).endVertex();
        builder.vertex(entry, x0, y1, z0).color(r, g, b, a).normal(entry, 0, 0, 1).endVertex();
        builder.vertex(entry, x0, y1, z1).color(r, g, b, a).normal(entry, 0, 0, 1).endVertex();
        builder.vertex(entry, x0, y1, z1).color(r, g, b, a).normal(entry, 0, -1, 0).endVertex();
        builder.vertex(entry, x0, y0, z1).color(r, g, b, a).normal(entry, 0, -1, 0).endVertex();
        builder.vertex(entry, x0, y0, z1).color(r, g, b, a).normal(entry, 1, 0, 0).endVertex();
        builder.vertex(entry, x1, y0, z1).color(r, g, b, a).normal(entry, 1, 0, 0).endVertex();
        builder.vertex(entry, x1, y0, z1).color(r, g, b, a).normal(entry, 0, 0, -1).endVertex();
        builder.vertex(entry, x1, y0, z0).color(r, g, b, a).normal(entry, 0, 0, -1).endVertex();
        builder.vertex(entry, x0, y1, z1).color(r, g, b, a).normal(entry, 1, 0, 0).endVertex();
        builder.vertex(entry, x1, y1, z1).color(r, g, b, a).normal(entry, 1, 0, 0).endVertex();
        builder.vertex(entry, x1, y0, z1).color(r, g, b, a).normal(entry, 0, 1, 0).endVertex();
        builder.vertex(entry, x1, y1, z1).color(r, g, b, a).normal(entry, 0, 1, 0).endVertex();
        builder.vertex(entry, x1, y1, z0).color(r, g, b, a).normal(entry, 0, 0, 1).endVertex();
        builder.vertex(entry, x1, y1, z1).color(r, g, b, a).normal(entry, 0, 0, 1).endVertex();
    }

    public static void cube(IRectangle3i rect, float r, float g, float b, float a, IPoseStack poseStack, IBufferSource bufferSource) {
        float x = rect.getMinX();
        float y = rect.getMinY();
        float z = rect.getMinZ();
        float w = rect.getWidth();
        float h = rect.getHeight();
        float d = rect.getDepth();
        cube(x, y, z, w, h, d, r, g, b, a, poseStack, bufferSource);
    }

    public static void cube(IRectangle3f rect, float r, float g, float b, float a, IPoseStack poseStack, IBufferSource bufferSource) {
        float x = rect.getMinX();
        float y = rect.getMinY();
        float z = rect.getMinZ();
        float w = rect.getWidth();
        float h = rect.getHeight();
        float d = rect.getDepth();
        cube(x, y, z, w, h, d, r, g, b, a, poseStack, bufferSource);
    }

    public static void cube(IRectangle3f rect, float r, float g, float b, float a, IPoseStack poseStack, IVertexConsumer consumer) {
        float x = rect.getMinX();
        float y = rect.getMinY();
        float z = rect.getMinZ();
        float w = rect.getWidth();
        float h = rect.getHeight();
        float d = rect.getDepth();
        cube(x, y, z, w, h, d, r, g, b, a, poseStack, consumer);
    }

    public static void cube(float x, float y, float z, float w, float h, float d, float r, float g, float b, float a, IPoseStack poseStack, IBufferSource bufferSource) {
//        var builder1 = SkinVertexBufferBuilder.getBuffer(bufferSource);
//        var builder = builder1.getBuffer(SkinRenderType.IMAGE_GUIDE);
        var builder = bufferSource.getBuffer(SkinRenderType.IMAGE_GUIDE);
        cube(x, y, z, w, h, d, r, g, b, a, poseStack, builder);
    }

    public static void cube(float x, float y, float z, float w, float h, float d, float r, float g, float b, float a, IPoseStack poseStack, IVertexConsumer consumer) {
        if (w == 0 || h == 0 || d == 0) {
            return;
        }
        var pose = poseStack.last();
        for (var dir : Direction.values()) {
            var vertexes = FACE_MARK_VERTEXES[dir.get3DDataValue()];
            var textures = FACE_MARK_TEXTURES[dir.get3DDataValue()];
            float u = 0f;
            float v = 0f;
            float[] values = {0, w, h, d};
            for (int i = 0; i < 4; ++i) {
                consumer.vertex(pose, x + vertexes[i][0] * w, y + vertexes[i][1] * h, z + vertexes[i][2] * d)
                        .color(r, g, b, a)
                        .uv(u + values[textures[i][0]], v + values[textures[i][1]])
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(0xf000f0)
                        .endVertex();
            }
        }
    }
}
