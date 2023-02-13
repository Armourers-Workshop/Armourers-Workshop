package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.Direction;

public class SkinCube implements ISkinCube {

    private ISkinCubeType type;

    private Vector3i pos = Vector3i.ZERO;
    private final IPaintColor[] paintColors = new IPaintColor[6];

    public void setPos(Vector3i pos) {
        this.pos = pos;
    }

    @Override
    public Vector3i getPos() {
        return pos;
    }

    public void setType(ISkinCubeType type) {
        this.type = type;
    }

    @Override
    public ISkinCubeType getType() {
        return type;
    }

    public void setPaintColor(Direction dir, IPaintColor paintColor) {
        paintColors[dir.ordinal()] = paintColor;
    }

    @Override
    public IPaintColor getPaintColor(Direction dir) {
        return paintColors[dir.ordinal()];
    }

    public SkinCubeFace getFace(Direction dir) {
        Vector3i pos = getPos();
        ISkinCubeType cubeType = getType();
        IPaintColor paintColor = getPaintColor(dir);
        int alpha = 255;
        if (cubeType.isGlass()) {
            alpha = 127;
        }
        return new SkinCubeFace(pos, paintColor, alpha, dir, cubeType);
    }
}
