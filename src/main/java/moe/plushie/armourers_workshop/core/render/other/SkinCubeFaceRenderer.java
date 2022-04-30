package moe.plushie.armourers_workshop.core.render.other;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;

public class SkinCubeFaceRenderer {

    private static final byte[][][] FACE_VERTEXES = new byte[][][]{
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // -y
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // +y
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // -z
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // +z
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // -x
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // +x
    };

    private static final byte[][] FACE_MARK_TEXTURES = {
            {1, 1}, {1, 0}, {0, 0}, {0, 1}
    };

    private static final byte[][][] FACE_MARK_VERTEXES = new byte[][][]{
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // -y
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // +y
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // -z
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // +z
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // -x
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // +x
    };

    public static void render(int x, int y, int z, Direction direction, IPaintColor paintColor, int alpha, MatrixStack matrixStack, IVertexBuilder builder) {
        if (paintColor.getPaintType() == SkinPaintTypes.NONE) {
            return;
        }
        Matrix4f mat = matrixStack.last().pose();
        ISkinPaintType paintType = paintColor.getPaintType();
        int color = paintColor.getRGB();
        byte[][] vertexes = FACE_VERTEXES[direction.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(mat, x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(color >> 16 & 0xff, color >> 8 & 0xff, color & 0xff, alpha & 0xff)
                    .uv(paintType.getU() / 256f, paintType.getV() / 256f)
                    .normal(vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }

    public static void renderMarker(int x, int y, int z, Direction direction, IPaintColor paintColor, int alpha, MatrixStack matrixStack, IVertexBuilder builder) {
        if (paintColor.getPaintType() == SkinPaintTypes.NORMAL) {
            return;
        }

        Matrix4f mat = matrixStack.last().pose();
        ISkinPaintType paintType = paintColor.getPaintType();
        int u = paintType.getIndex() % 8;
        int v = paintType.getIndex() / 8;
        byte[][] vertexes = FACE_MARK_VERTEXES[direction.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(mat, x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(255, 255, 255, alpha & 0xff)
                    .uv((u + FACE_MARK_TEXTURES[i][0]) / 8f, (v + FACE_MARK_TEXTURES[i][1]) / 8f)
                    .endVertex();
        }
    }
}
