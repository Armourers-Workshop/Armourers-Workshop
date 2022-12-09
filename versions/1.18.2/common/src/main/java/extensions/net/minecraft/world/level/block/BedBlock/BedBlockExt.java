package extensions.net.minecraft.world.level.block.BedBlock;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

@Extension
public class BedBlockExt {

    public static Optional<Vec3> findStandUpPosition(@ThisClass Class<?> clazz, EntityType<?> entityType, CollisionGetter collisionGetter, BlockPos blockPos, Direction dir, float f) {
        return BedBlock.findStandUpPosition(entityType, collisionGetter, blockPos, f);
    }
}
