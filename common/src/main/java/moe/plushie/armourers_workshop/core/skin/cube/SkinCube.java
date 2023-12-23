package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class SkinCube implements ISkinCube {

    protected ISkinCubeType type;

    protected Vector3i pos = Vector3i.ZERO;
    protected final EnumMap<Direction, IPaintColor> paintColors = new EnumMap<>(Direction.class);

    public void setPosition(Vector3i pos) {
        this.pos = pos;
    }

    @Override
    public Vector3i getPosition() {
        return pos;
    }

    @Override
    public Rectangle3f getShape() {
        Vector3i pos = getPosition();
        return new Rectangle3f(pos.getX(), pos.getY(), pos.getZ(), 1, 1, 1);
    }

    @Override
    public SkinTransform getTransform() {
        return SkinTransform.IDENTITY;
    }

    public void setType(ISkinCubeType type) {
        this.type = type;
    }

    @Override
    public ISkinCubeType getType() {
        return type;
    }

    public void setPaintColor(Direction dir, IPaintColor paintColor) {
        paintColors.put(dir, paintColor);
    }

    @Override
    public IPaintColor getPaintColor(Direction dir) {
        return paintColors.getOrDefault(dir, PaintColor.CLEAR);
    }

    @Override
    public ITextureKey getTexture(Direction dir) {
        return null;
    }

    @Nullable
    public SkinCubeFace getFace(Direction dir) {
        ITextureKey textureKey = getTexture(dir);
        IPaintColor paintColor = getPaintColor(dir);
        ISkinCubeType cubeType = getType();
        int alpha = 255;
        if (cubeType.isGlass()) {
            alpha = 127;
        }
        return new SkinCubeFace(getShape(), getTransform(), paintColor, alpha, dir, textureKey, cubeType);
    }

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "type", type, "shape", getShape());
    }
}
