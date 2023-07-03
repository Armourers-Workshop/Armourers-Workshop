package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

public class ExtendedFaceRenderer {

    private static final byte[][] FACE_MARK_TEXTURES = {
            {1, 1}, {1, 0}, {0, 0}, {0, 1}
    };

    private static final byte[][][] FACE_MARK_VERTEXES = new byte[][][]{
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}},   // +y
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},    // -y
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}},   // +z
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},    // -z
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}},   // +x
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},    // -x
    };

    public static void render(int x, int y, int z, Direction direction, IPaintColor paintColor, int alpha, int lightmap, int overlay, PoseStack poseStack, VertexConsumer builder) {
        PoseStack.Pose pose = poseStack.last();
        ISkinPaintType paintType = paintColor.getPaintType();
        float u = paintType.getU() / 256f;
        float v = paintType.getV() / 256f;
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
        byte[][] vertexes = SkinUtils.FACE_VERTEXES[direction.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(pose.pose(), x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(r, g, b, a)
                    .uv(u, v)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(lightmap)
                    .normal(pose.normal(), vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }

    public static void renderMarker(int x, int y, int z, Direction direction, IPaintColor paintColor, int alpha, int light, int overlay, PoseStack poseStack, VertexConsumer builder) {
        if (paintColor.getPaintType() == SkinPaintTypes.NORMAL) {
            return;
        }

        PoseStack.Pose last = poseStack.last();
        ISkinPaintType paintType = paintColor.getPaintType();
        int u = paintType.getIndex() % 8;
        int v = paintType.getIndex() / 8;
        byte[][] vertexes = FACE_MARK_VERTEXES[direction.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(last.pose(), x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(255, 255, 255, alpha & 0xff)
                    .uv((u + FACE_MARK_TEXTURES[i][0]) / 8f, (v + FACE_MARK_TEXTURES[i][1]) / 8f)
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(last.normal(), vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }

    public static void render2(int x, int y, int z, Direction direction, IPaintColor paintColor, int alpha, int light, int overlay, PoseStack poseStack, VertexConsumer builder) {
        PoseStack.Pose last = poseStack.last();
        int u = 0;
        int v = 0;
        int color = paintColor.getRGB();
        byte[][] vertexes = FACE_MARK_VERTEXES[direction.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(last.pose(), x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(color >> 16 & 0xff, color >> 8 & 0xff, color & 0xff, alpha & 0xff)
                    .uv((u + FACE_MARK_TEXTURES[i][0]), (v + FACE_MARK_TEXTURES[i][1]))
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(last.normal(), vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }
}
