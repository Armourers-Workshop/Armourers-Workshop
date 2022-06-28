package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.core.item.MannequinItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class MannequinRayTraceResult extends BlockRayTraceResult {

    private final float scale;
    private final float rotation;

    public MannequinRayTraceResult(BlockPos pos, Vector3d location, float scale, float rotation) {
        super(location, Direction.UP, pos, false);
        this.scale = scale;
        this.rotation = rotation;
    }

    public static MannequinRayTraceResult test(PlayerEntity player, Vector3d origin, Vector3d target, BlockPos pos) {
        World world = player.level;
        ItemStack itemStack = player.getMainHandItem();
        float scale = MannequinItem.getScale(itemStack);
        float rotation = (float) TrigUtils.getAngleDegrees(origin.x(), origin.z(), target.x(), target.z()) + 90.0f;

        if (MannequinItem.isSmall(itemStack)) {
            scale *= 0.5f;
        }
        BlockState blockState = world.getBlockState(pos);

        if (player.isShiftKeyDown()) {
            VoxelShape shape = blockState.getShape(world, pos);
            target = Vector3d.upFromBottomCenterOf(pos, shape.max(Direction.Axis.Y));
            VoxelShape collisionShape = blockState.getCollisionShape(world, pos);
            if (!Block.isFaceFull(collisionShape, Direction.UP)) {
                Vector3d newLocation = Vector3d.atBottomCenterOf(pos); // can't stand, reset to bottom
                if (!collisionShape.isEmpty()) {
                    BlockRayTraceResult collisionBox = shape.clip(target, newLocation, pos);
                    if (collisionBox != null) {
                        newLocation = collisionBox.getLocation();
                    }
                }
                target = newLocation;
            }
            int l = MathHelper.floor(player.yRot * 16 / 360 + 0.5) % 16;
            rotation = l * 22.5f + 180f;
        }

        // AxisAlignedBB box = MannequinEntity.STANDING_DIMENSIONS.scale(scale).makeBoundingBox(target);
        return new MannequinRayTraceResult(pos, target, scale, rotation);
    }

    public float getScale() {
        return scale;
    }

    public float getRotation() {
        return rotation;
    }
}
