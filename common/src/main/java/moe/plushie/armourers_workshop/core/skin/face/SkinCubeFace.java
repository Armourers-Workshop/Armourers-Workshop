package moe.plushie.armourers_workshop.core.skin.face;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.render.ExtendedFaceRenderer;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class SkinCubeFace {

    private static final PaintColor RAINBOW_TARGET = PaintColor.of(0xff7f7f7f, SkinPaintTypes.RAINBOW);

    public final int x;
    public final int y;
    public final int z;

    public final int alpha;

    private final Direction direction;
    private final PaintColor color;
    private final ISkinCubeType type;

    public SkinCubeFace(Vector3i pos, IPaintColor color, int alpha, Direction direction, ISkinCubeType type) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();

        this.type = type;
        this.color = PaintColor.of(color);
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
        int r = MathUtils.clamp(destination.getRed() + src - avg, 0, 255);
        int g = MathUtils.clamp(destination.getGreen() + src - avg, 0, 255);
        int b = MathUtils.clamp(destination.getBlue() + src - avg, 0, 255);
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
            // get textures color only work on the client side.
            Optional<PaintColor> paintColor1 = EnvironmentExecutor.callWhenOn(EnvironmentType.CLIENT, () -> () -> getTextureColor(scheme.getTexture(), partType));
            return paintColor1.orElse(paintColor);
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

    @Environment(value = EnvType.CLIENT)
    public void render(BakedSkinPart part, ColorScheme scheme, int light, int overlay, PoseStack poseStack, VertexConsumer builder) {
        PaintColor resolvedColor = resolve(color, scheme, part.getColorInfo(), part.getType(), 0);
        if (resolvedColor.getPaintType() == SkinPaintTypes.NONE) {
            return;
        }
        ExtendedFaceRenderer.render(x, y, z, direction, resolvedColor, alpha, light, overlay, poseStack, builder);
    }

    @Environment(value = EnvType.CLIENT)
    public PaintColor getTextureColor(ResourceLocation texture, ISkinPartType partType) {
        BakedEntityTexture bakedTexture = PlayerTextureLoader.getInstance().getTextureModel(texture);
        if (bakedTexture != null) {
            return bakedTexture.getColor(x, y, z, direction, partType);
        }
        return null;
    }

    public ISkinCubeType getType() {
        return type;
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
