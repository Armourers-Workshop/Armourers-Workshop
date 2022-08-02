package moe.plushie.armourers_workshop.builder.other;

import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.core.data.OptionalDirection;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;

public class SkinCubeTransform {

    final Level world;
    final BlockPos pos;
    final Direction direction;
    final Rotation rotation;
    final Rotation invRotation;
    final Quaternion rotationDegrees;

    public SkinCubeTransform(Level world, BlockPos pos, Direction direction) {
        this.world = world;
        this.pos = pos;
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

    public static Quaternion getRotationDegrees(Direction dir) {
        switch (dir) {
            case SOUTH:
                return TrigUtils.rotate(0, 180, 0, true);
            case WEST:
                return TrigUtils.rotate(0, 90, 0, true);
            case EAST:
                return TrigUtils.rotate(0, -90, 0, true);
            case NORTH:
            default:
                return Quaternion.ONE;
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
        if (rotationDegrees == Quaternion.ONE) {
            return pos.offset(x, y, z);
        }
        // we increase 0.5 offset to avoid down-cast incorrect by float accuracy problems.
        Vector4f off = new Vector4f(x + 0.5f, y + 0.5f, z + 0.5f, 1);
        off.transform(rotationDegrees);
        return pos.offset(off.x(), off.y(), off.z());
    }

}
