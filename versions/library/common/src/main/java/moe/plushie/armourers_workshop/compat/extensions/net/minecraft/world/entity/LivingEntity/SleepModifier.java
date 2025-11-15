package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.LivingEntity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.phys.Vec3;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.20, )")
public class SleepModifier {

    public static void stopSleeping(@This LivingEntity entity, BlockPos blockPos) {
        //entity.stopSleeping();
        var blockState = entity.level().getBlockState(blockPos);
        if (blockState.getBlock() instanceof BedBlock) {
            var direction = blockState.getValue(BedBlock.FACING);
            entity.level().setBlock(blockPos, blockState.setValue(BedBlock.OCCUPIED, false), 3);
            var vec3 = BedBlock.findStandUpPosition(entity.getType(), entity.level(), blockPos, direction, entity.getYRot()).orElseGet(() -> {
                var blockPos2 = blockPos.above();
                return new Vec3((double) blockPos2.getX() + 0.5, (double) blockPos2.getY() + 0.1, (double) blockPos2.getZ() + 0.5);
            });
            var vec32 = Vec3.atBottomCenterOf(blockPos).subtract(vec3).normalize();
            float f = (float) OpenMath.wrapDegrees(OpenMath.atan2(vec32.z, vec32.x) * (double) (180F / (float) Math.PI) - 90.0D);
            entity.setPos(vec3.x, vec3.y, vec3.z);
            entity.setYRot(f);
            entity.setXRot(0.0f);
        }
    }
}
