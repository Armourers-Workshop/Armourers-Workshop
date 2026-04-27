package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.item.MannequinItem;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MannequinHitResult extends BlockHitResult {

    private final float scale;
    private final float rotation;

    public MannequinHitResult(BlockPos pos, Vec3 location, float scale, float rotation) {
        super(location, Direction.UP, pos, false);
        this.scale = scale;
        this.rotation = rotation;
    }

    public static MannequinHitResult test(Player player, OpenVector3d origin, OpenVector3d target, BlockPos pos) {
        return test(player, origin, new Vec3(target.x, target.y, target.z), pos);
    }

    public static MannequinHitResult test(Player player, OpenVector3d origin, Vec3 target, BlockPos pos) {
        var level = player.level();
        var itemStack = player.getMainHandItem();
        var scale = MannequinItem.getScale(itemStack);
        var rotation = (float) OpenMath.getAngleDegrees(origin.x(), origin.z(), target.x(), target.z()) + 90.0f;

        if (MannequinItem.isSmall(itemStack)) {
            scale *= 0.5f;
        }
        var blockState = level.getBlockState(pos);

        if (player.isSecondaryUseActive()) {
            var shape = blockState.getShape(level, pos);
            target = Vec3.upFromBottomCenterOf(pos, shape.max(Direction.Axis.Y));
            var collisionShape = blockState.getCollisionShape(level, pos);
            if (!Block.isFaceFull(collisionShape, Direction.UP)) {
                var newLocation = Vec3.atBottomCenterOf(pos); // can't stand, reset to bottom
                if (!collisionShape.isEmpty()) {
                    BlockHitResult collisionBox = shape.clip(target, newLocation, pos);
                    if (collisionBox != null) {
                        newLocation = collisionBox.getLocation();
                    }
                }
                target = newLocation;
            }
            int l = OpenMath.floori(player.getYRot() * 16 / 360 + 0.5) % 16;
            rotation = l * 22.5f + 180f;
        }

        // AABB box = MannequinEntity.STANDING_DIMENSIONS.scale(scale).makeBoundingBox(target);
        return new MannequinHitResult(pos, target, scale, rotation);
    }

    public float scale() {
        return scale;
    }

    public float rotation() {
        return rotation;
    }
}
