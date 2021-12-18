package moe.plushie.armourers_workshop.core.render.part;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderDispatcher;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderTypeBuffer;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.Direction;

public class SkinPartRenderer {

    private final static byte[][][] FACE_VERTEXES = new byte[][][]{
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // -y
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // +y
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // -z
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // +z
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // -x
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // +x
    };

//    public static final SkinPartRenderer INSTANCE = new SkinPartRenderer();


    public static void renderPart(SkinPart part, SkinDye dye, int light, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
        // ignore part when part is disable
        if (SkinConfig.isEnableSkinPart(part)) {
            return;
        }

        // key = part + dye + faces
        SkinCache.Key key = new SkinCache.Key(part, dye, part.getPackedFaces());
        SkinRenderTypeBuffer buffer = SkinCache.INSTANCE.cache(key);
        if (buffer != null) {
            SkinRenderDispatcher.merge(part, light, matrixStack, buffer);
            return;
        }

        SkinRenderTypeBuffer buffer1 = new SkinRenderTypeBuffer();
        part.render(dye, light, new MatrixStack(), buffer1);
        buffer1.endBatch();
        SkinCache.INSTANCE.cache(key, buffer1);
    }

    public static void renderFace(IVertexBuilder builder, float x, float y, float z, byte r, byte g, byte b, byte a, Direction dir, float u, float v) {
        byte[][] vertexes = FACE_VERTEXES[dir.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(r, g, b, a)
                    .uv(u / 256.0f, v / 256.0f)
                    .normal(vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }
}
