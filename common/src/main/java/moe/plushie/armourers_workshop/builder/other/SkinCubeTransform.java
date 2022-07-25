package moe.plushie.armourers_workshop.builder.other;

import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class SkinCubeTransform {

    final Level world;
    final BlockPos pos;
    final Direction direction;
    final Quaternion rotation;

    public SkinCubeTransform(Level world, BlockPos pos, Direction direction) {
        this.world = world;
        this.pos = pos;
        this.direction = direction;
        this.rotation = getRotation(direction);
    }

    public static Quaternion getRotation(Direction dir) {
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

    public BlockPos mul(Vector3i pos) {
        return mul(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos mul(int x, int y, int z) {
        // in this case not need to apply matrix transform.
        if (rotation == Quaternion.ONE) {
            return pos.offset(x, y, z);
        }
        // we increase 0.5 offset to avoid down-cast incorrect by float accuracy problems.
        Vector4f off = new Vector4f(x + 0.5f, y + 0.5f, z + 0.5f, 1);
        off.transform(rotation);
        return pos.offset(off.x(), off.y(), off.z());
    }

}
