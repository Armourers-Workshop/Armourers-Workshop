package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import net.minecraft.core.Direction;

import manifold.ext.rt.api.auto;

public class ExtendedFaceRenderer {

    // we manually reduced by 0.002 pixels,
    // it will prevent out of bound in the texture.
    private static final float[][] FACE_MARK_TEXTURES = {
            {0.998f, 0.998f}, {0.998f, 0.000f}, {0.000f, 0.000f}, {0, 0.998f}
    };

    // we define a float to reduce runtime type conversion.
    private static final float[][][] FACE_MARK_VERTEXES = new float[][][]{
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}},   // +y
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},    // -y
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}},   // +z
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},    // -z
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}},   // +x
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},    // -x
    };

/*
    public static void render(int x, int y, int z, Direction direction, IPaintColor paintColor, int alpha, int lightmap, int overlay, PoseStack poseStack, VertexConsumer builder) {
        auto pose = poseStack.last().pose();
        auto normal = poseStack.last().normal();
        auto paintType = paintColor.getPaintType();
        float u = paintType.getU();
        float v = paintType.getV();
        int color = paintColor.getRGB();
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;
        int a = alpha & 0xff;
        if (overlay != 0) {
            float q = (overlay >> 24 & 0xff) / 255f;
            r = ColorUtils.mix(r, overlay >> 16 & 0xff, q);
            g = ColorUtils.mix(g, overlay >> 8 & 0xff, q);
            b = ColorUtils.mix(b, overlay & 0xff, q);
        }
        auto vertexes = SkinUtils.FACE_VERTEXES[direction.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(pose, x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(r, g, b, a)
                    .uv((u + FACE_MARK_TEXTURES[i][0]) / 256f, (v + FACE_MARK_TEXTURES[i][1]) / 256f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(lightmap)
                    .normal(normal, vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }
*/
    public static void renderMarker(int x, int y, int z, Direction direction, IPaintColor paintColor, int alpha, int light, int overlay, PoseStack poseStack, VertexConsumer builder) {
        if (paintColor.getPaintType() == SkinPaintTypes.NORMAL) {
            return;
        }
        auto pose = poseStack.last().pose();
        auto normal = poseStack.last().normal();
        auto paintType = paintColor.getPaintType();
        int u = paintType.getIndex() % 8;
        int v = paintType.getIndex() / 8;
        auto vertexes = FACE_MARK_VERTEXES[direction.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(pose, x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(255, 255, 255, alpha & 0xff)
                    .uv((u + FACE_MARK_TEXTURES[i][0]) / 8f, (v + FACE_MARK_TEXTURES[i][1]) / 8f)
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(normal, vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }

    public static void render2(int x, int y, int z, Direction direction, IPaintColor paintColor, int alpha, int light, int overlay, PoseStack poseStack, VertexConsumer builder) {
        auto pose = poseStack.last().pose();
        auto normal = poseStack.last().normal();
        int u = 0;
        int v = 0;
        int color = paintColor.getRGB();
        auto vertexes = FACE_MARK_VERTEXES[direction.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(pose, x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(color >> 16 & 0xff, color >> 8 & 0xff, color & 0xff, alpha & 0xff)
                    .uv((u + FACE_MARK_TEXTURES[i][0]), (v + FACE_MARK_TEXTURES[i][1]))
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(normal, vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }
}
