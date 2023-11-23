package moe.plushie.armourers_workshop.core.skin.face;

import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import net.minecraft.core.Direction;

public class SkinCubeFace {

    public final int alpha;

    private final Direction direction;
    private final IPaintColor color;
    private final ISkinCubeType type;

    private final IRectangle3f shape;
    private final ITransformf transform;

    private final ITextureKey texture;

    public SkinCubeFace(IRectangle3f shape, ITransformf transform, IPaintColor color, int alpha, Direction direction, ITextureKey textureKey, ISkinCubeType type) {
        this.type = type;
        this.color = color;
        this.alpha = alpha;
        this.direction = direction;
        this.shape = shape;
        this.transform = transform;
        this.texture = textureKey;
    }

    public ITextureKey getTexture() {
        return texture;
    }

    public IRectangle3f getShape() {
        return shape;
    }

    public ITransformf getTransform() {
        return transform;
    }

    public ISkinCubeType getType() {
        return type;
    }

    public IPaintColor getColor() {
        return color;
    }

    public int getAlpha() {
        return alpha;
    }

    public Direction getDirection() {
        return direction;
    }

    public ISkinPaintType getPaintType() {
        return color.getPaintType();
    }
}
