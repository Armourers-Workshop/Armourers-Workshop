package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class CubeTransform {

    public final Level level;
    public final BlockPos blockPos;
    public final OpenDirection direction;
    public final OpenRotation rotation;
    public final OpenRotation invRotation;
    public final OpenQuaternionf rotationDegrees;

    public CubeTransform(Level level, BlockPos blockPos, OpenDirection direction) {
        this.level = level;
        this.blockPos = blockPos;
        this.direction = direction;
        this.rotation = getRotation(direction, false);
        this.invRotation = getRotation(direction, true);
        this.rotationDegrees = getFacingRotation(direction);
    }

    public static OpenRotation getRotation(OpenDirection dir, boolean flags) {
        return switch (dir) {
            case SOUTH -> OpenRotation.CLOCKWISE_180;
            case WEST -> flags ? OpenRotation.CLOCKWISE_90 : OpenRotation.COUNTERCLOCKWISE_90;
            case EAST -> flags ? OpenRotation.COUNTERCLOCKWISE_90 : OpenRotation.CLOCKWISE_90;
            default -> OpenRotation.NONE;
        };
    }

    public static OpenQuaternionf getFacingRotation(OpenDirection dir) {
        return switch (dir) {
            case SOUTH -> new OpenQuaternionf(0, 180, 0, true);
            case WEST -> new OpenQuaternionf(0, 90, 0, true);
            case EAST -> new OpenQuaternionf(0, -90, 0, true);
            default -> OpenQuaternionf.ONE;
        };
    }

    public static OpenVector3f getFacingRotation2(OpenDirection dir) {
        return switch (dir) {
            case SOUTH -> new OpenVector3f(0, 180, 0);
            case WEST -> new OpenVector3f(0, 90, 0);
            case EAST -> new OpenVector3f(0, -90, 0);
            default -> OpenVector3f.ZERO;
        };
    }


    public OpenDirection rotate(OpenDirection dir) {
        return rotation.rotate(dir);
    }

    public OpenDirection invRotate(OpenDirection dir) {
        return invRotation.rotate(dir);
    }

    public BlockPos mul(OpenVector3i pos) {
        return mul(pos.x(), pos.y(), pos.z());
    }

    public BlockPos mul(int x, int y, int z) {
        // in this case not need to apply matrix transform.
        if (rotationDegrees == OpenQuaternionf.ONE) {
            return blockPos.offset(x, y, z);
        }
        // we increase 0.5 offset to avoid down-cast incorrect by float accuracy problems.
        var off = new OpenVector4f(x + 0.5f, y + 0.5f, z + 0.5f, 1);
        off.transform(rotationDegrees);
        return blockPos.offset(OpenMath.floori(off.x()), OpenMath.floori(off.y()), OpenMath.floori(off.z()));
    }

}
