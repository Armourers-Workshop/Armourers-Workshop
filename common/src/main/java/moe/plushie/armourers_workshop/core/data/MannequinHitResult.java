package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.item.MannequinItem;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MannequinHitResult extends BlockHitResult {

    private final float scale;
    private final float rotation;

    public MannequinHitResult(BlockPos pos, Vec3 location, float scale, float rotation) {
        super(location, Direction.UP, pos, false);
        this.scale = scale;
        this.rotation = rotation;
    }

    public static MannequinHitResult test(Player player, Vec3 origin, Vec3 target, BlockPos pos) {
        Level level = player.level;
        ItemStack itemStack = player.getMainHandItem();
        float scale = MannequinItem.getScale(itemStack);
        float rotation = (float) TrigUtils.getAngleDegrees(origin.x(), origin.z(), target.x(), target.z()) + 90.0f;

        if (MannequinItem.isSmall(itemStack)) {
            scale *= 0.5f;
        }
        BlockState blockState = level.getBlockState(pos);

        if (player.isShiftKeyDown()) {
            VoxelShape shape = blockState.getShape(level, pos);
            target = Vec3.upFromBottomCenterOf(pos, shape.max(Direction.Axis.Y));
            VoxelShape collisionShape = blockState.getCollisionShape(level, pos);
            if (!Block.isFaceFull(collisionShape, Direction.UP)) {
                Vec3 newLocation = Vec3.atBottomCenterOf(pos); // can't stand, reset to bottom
                if (!collisionShape.isEmpty()) {
                    BlockHitResult collisionBox = shape.clip(target, newLocation, pos);
                    if (collisionBox != null) {
                        newLocation = collisionBox.getLocation();
                    }
                }
                target = newLocation;
            }
            int l = MathUtils.floor(player.yRot * 16 / 360 + 0.5) % 16;
            rotation = l * 22.5f + 180f;
        }

        // AABB box = MannequinEntity.STANDING_DIMENSIONS.scale(scale).makeBoundingBox(target);
        return new MannequinHitResult(pos, target, scale, rotation);
    }

    public float getScale() {
        return scale;
    }

    public float getRotation() {
        return rotation;
    }
}
