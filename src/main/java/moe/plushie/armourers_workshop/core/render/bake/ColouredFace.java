package moe.plushie.armourers_workshop.core.render.bake;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.render.other.SkinCubeFaceRenderer;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.utils.color.ColorDescriptor;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColouredFace {

    private static final PaintColor RAINBOW_TARGET = PaintColor.of(0xff7f7f7f, SkinPaintTypes.RAINBOW);

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
            IPaintColor paintColor1 = scheme.getResolvedColor(paintType);
            if (paintColor1 == null) {
                return paintColor;
            }
            paintColor = dye(paintColor, PaintColor.of(paintColor1), descriptor.getAverageColor(paintType));
            return resolve(paintColor, scheme, descriptor, partType, count + 1);
        }
        return paintColor;
    }

    public void render(BakedSkinPart part, ColorScheme scheme, MatrixStack matrixStack, IVertexBuilder builder) {
        PaintColor resolvedColor = resolve(color, scheme, part.getColorInfo(), part.getType(), 0);
        SkinCubeFaceRenderer.render(x, y, z, direction, resolvedColor, alpha, matrixStack, builder);
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
