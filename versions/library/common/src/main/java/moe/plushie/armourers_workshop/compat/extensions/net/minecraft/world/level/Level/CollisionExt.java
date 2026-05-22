package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.level.Level;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.math.OpenAxisAlignedBoundingBox;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[18, )")
public class CollisionExt {

    public static List<OpenVoxelShape> getCollisions(@This Level level, Entity entity, OpenAxisAlignedBoundingBox box) {
        var aabb = new AABB(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
        var results = new ArrayList<OpenVoxelShape>();
        for (var shape : level.getCollisions(entity, aabb)) {
            results.add(wrap(shape));
        }
        return results;
    }

    private static OpenVoxelShape wrap(VoxelShape shape) {
        var bounds = shape.bounds();
        return OpenVoxelShape.box(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ);
    }
}
