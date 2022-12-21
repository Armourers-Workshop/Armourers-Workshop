package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.utils.math.Quaternionf;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

public class CubeTransform {

    public final Level level;
    public final BlockPos blockPos;
    public final Direction direction;
    public final Rotation rotation;
    public final Rotation invRotation;
    public final Quaternionf rotationDegrees;

    public CubeTransform(Level level, BlockPos blockPos, Direction direction) {
        this.level = level;
        this.blockPos = blockPos;
        this.direction = direction;
        this.rotation = getRotation(direction, false);
        this.invRotation = getRotation(direction, true);
        this.rotationDegrees = getRotationDegrees(direction);
    }

    public static Rotation getRotation(Direction dir, boolean flags) {
        switch (dir) {
            case SOUTH:
                return Rotation.CLOCKWISE_180;
            case WEST:
                if (flags) {
                    return Rotation.CLOCKWISE_90;
                }
                return Rotation.COUNTERCLOCKWISE_90;
            case EAST:
                if (flags) {
                    return Rotation.COUNTERCLOCKWISE_90;
                }
                return Rotation.CLOCKWISE_90;
            case NORTH:
            default:
                return Rotation.NONE;
        }
    }

    public static Quaternionf getRotationDegrees(Direction dir) {
        switch (dir) {
            case SOUTH:
                return new Quaternionf(0, 180, 0, true);
            case WEST:
                return new Quaternionf(0, 90, 0, true);
            case EAST:
                return new Quaternionf(0, -90, 0, true);
            case NORTH:
            default:
                return Quaternionf.ONE;
        }
    }

    public Direction rotate(Direction dir) {
        return rotation.rotate(dir);
    }

    public Direction invRotate(Direction dir) {
        return invRotation.rotate(dir);
    }

    public BlockPos mul(Vector3i pos) {
        return mul(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos mul(int x, int y, int z) {
        // in this case not need to apply matrix transform.
        if (rotationDegrees == Quaternionf.ONE) {
            return blockPos.offset(x, y, z);
        }
        // we increase 0.5 offset to avoid down-cast incorrect by float accuracy problems.
        Vector4f off = new Vector4f(x + 0.5f, y + 0.5f, z + 0.5f, 1);
        off.transform(rotationDegrees);
        return blockPos.offset(off.x(), off.y(), off.z());
    }

}
