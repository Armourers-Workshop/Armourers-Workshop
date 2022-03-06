package moe.plushie.armourers_workshop.core.render.bake;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.api.ISkinCube;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.color.ColorScheme;
import moe.plushie.armourers_workshop.core.color.PaintColor;
import moe.plushie.armourers_workshop.core.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColouredFace {

    private static final PaintColor RAINBOW_TARGET = PaintColor.of(0xff7f7f7f, SkinPaintTypes.RAINBOW);
    private static final byte[][][] FACE_VERTEXES = new byte[][][]{
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // -y
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // +y
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // -z
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // +z
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // -x
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // +x
    };

    public final int x;
    public final int y;
    public final int z;

    public final int alpha;

    private final Direction direction;
    private final PaintColor color;
    private final ISkinCube cube;

    public ColouredFace(int x, int y, int z, PaintColor color, int alpha, Direction direction, ISkinCube cube) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.cube = cube;
        this.color = color;
        this.direction = direction;
        this.alpha = alpha;
    }

    public PaintColor dye(PaintColor source, PaintColor destination, PaintColor average) {
        if (destination.getPaintType() == SkinPaintTypes.NONE) {
            return PaintColor.CLEAR;
        }
        if (average == null) {
            return source;
        }
        int src = (source.getRed() + source.getGreen() + source.getBlue()) / 3;
        int avg = (average.getRed() + average.getGreen() + average.getBlue()) / 3;
        int r = MathHelper.clamp(destination.getRed() + src - avg, 0, 255);
        int g = MathHelper.clamp(destination.getGreen() + src - avg, 0, 255);
        int b = MathHelper.clamp(destination.getBlue() + src - avg, 0, 255);
        return PaintColor.of(0xff000000 | r << 16 | g << 8 | b, destination.getPaintType());
    }

    public PaintColor resolve(PaintColor paintColor, ColorScheme scheme, ColorDescriptor descriptor, ISkinPartType partType, int count) {
        ISkinPaintType paintType = paintColor.getPaintType();
        if (paintType == SkinPaintTypes.NONE) {
            return PaintColor.CLEAR;
        }
        if (paintType == SkinPaintTypes.RAINBOW) {
            return dye(paintColor, RAINBOW_TARGET, descriptor.getAverageColor(paintType));
        }
        if (paintType == SkinPaintTypes.TEXTURE) {
            BakedEntityTexture texture = PlayerTextureLoader.getInstance().getTextureModel(scheme.getTexture());
            if (texture != null) {
                PaintColor paintColor1 = texture.getColor(x, y, z, direction, partType);
                if (paintColor1 != null) {
                    return paintColor1;
                }
            }
            return paintColor;
        }
        if (paintType.getDyeType() != null && count < 2) {
            PaintColor paintColor1 = scheme.getResolvedColor(paintType);
            if (paintColor1 == null) {
                return paintColor;
            }
            paintColor = dye(paintColor, paintColor1, descriptor.getAverageColor(paintType));
            return resolve(paintColor, scheme, descriptor, partType, count + 1);
        }
        return paintColor;
    }

    public void render(BakedSkinPart part, ColorScheme scheme, MatrixStack matrixStack, IVertexBuilder builder) {
        PaintColor resolvedColor = resolve(color, scheme, part.getColorInfo(), part.getType(), 0);
        if (resolvedColor.getPaintType() == SkinPaintTypes.NONE) {
            return;
        }
        ISkinPaintType paintType = resolvedColor.getPaintType();
        int color = resolvedColor.getRGB();
        byte[][] vertexes = FACE_VERTEXES[direction.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2]).color(color >> 16 & 0xff, color >> 8 & 0xff, color & 0xff, alpha & 0xff).uv(paintType.getU() / 256.0f, paintType.getV() / 256.0f).normal(vertexes[4][0], vertexes[4][1], vertexes[4][2]).endVertex();
        }
    }

    public ISkinCube getCube() {
        return cube;
    }

    public PaintColor getColor() {
        return color;
    }

    public Direction getDirection() {
        return direction;
    }

    public ISkinPaintType getPaintType() {
        return color.getPaintType();
    }
}
