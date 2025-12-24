package moe.plushie.armourers_workshop.core.skin.particle.runtime;

import moe.plushie.armourers_workshop.core.math.OpenAxisAlignedBoundingBox;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface LevelAccessor {

//    public Block getBlock(String registryName) {
//        return AbstractRegistryManager.getBlock(registryName);
//    }

    Block getBlock(OpenVector3f position);

    List<OpenAxisAlignedBoundingBox> getCollisionBlocks(OpenAxisAlignedBoundingBox box);

//    public Block getBlock(SkinParticle particle) {
//        var level = particle.level();
//        if (level != null) {
//            var position = particle.position();
//            var x = OpenMath.floori(position.x());
//            var y = OpenMath.floori(position.y());
//            var z = OpenMath.floori(position.z());
//            var state = level.getBlockState(new BlockPos(x, y, z));
//            return state.getBlock();
//        }
//        return Blocks.AIR;
//    }
//
//    public static List<OpenAxisAlignedBoundingBox> getCollisionBlocks(Level level, OpenAxisAlignedBoundingBox box) {
//        // TODO This could potentially create a lot of garbage depending on how often this method is called - may need
//        // to optimize allocations of abstract AABBs using an object pool, or switch to using a mixin interface
//        var results = new ArrayList<OpenAxisAlignedBoundingBox>();
//        // TODO: NO IMPL - PARTICLE
//        var aabb = new AABB(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
//        for (var shape : level.getCollisions(null, aabb)) {
//            var bounds = shape.bounds();
//            results.add(new OpenAxisAlignedBoundingBox(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ));
//        }
//        return results;
//    }

//    public float calculateXOffset(OpenAxisAlignedBoundingBox otherBox, float xOffset) {
//        if (otherBox.yMax > this.yMin && otherBox.yMin < this.yMax && otherBox.zMax > this.zMin && otherBox.zMin < this.zMax) {
//            if (xOffset > 0.0 && otherBox.xMax <= this.xMin) {
//                double d1 = this.xMin - otherBox.xMax;
//
//                if (d1 < xOffset) {
//                    xOffset = d1;
//                }
//            }
//            else if (xOffset < 0.0 && otherBox.xMin >= this.xMax) {
//                double d0 = this.xMax - otherBox.xMin;
//
//                if (d0 > xOffset) {
//                    xOffset = d0;
//                }
//            }
//
//            return xOffset;
//        }
//        return xOffset;
//    }
//
//    public float calculateYOffset(OpenAxisAlignedBoundingBox otherBox, float yOffset) {
//        if (otherBox.xMax > this.xMin && otherBox.xMin < this.xMax && otherBox.zMax > this.zMin && otherBox.zMin < this.zMax) {
//            if (yOffset > 0.0D && otherBox.yMax <= this.yMin) {
//                double d1 = this.yMin - otherBox.yMax;
//
//                if (d1 < yOffset) {
//                    yOffset = d1;
//                }
//            }
//            else if (yOffset < 0.0D && otherBox.yMin >= this.yMax) {
//                double d0 = this.yMax - otherBox.yMin;
//
//                if (d0 > yOffset) {
//                    yOffset = d0;
//                }
//            }
//
//            return yOffset;
//        }
//        return yOffset;
//    }
//
//    public float calculateZOffset(OpenAxisAlignedBoundingBox otherBox, float zOffset) {
//        if (otherBox.xMax > this.xMin && otherBox.xMin < this.xMax && otherBox.yMax > this.yMin && otherBox.yMin < this.yMax) {
//            if (zOffset > 0.0D && otherBox.zMax <= this.zMin) {
//                double d1 = this.zMin - otherBox.zMax;
//
//                if (d1 < zOffset) {
//                    zOffset = d1;
//                }
//            }
//            else if (zOffset < 0.0D && otherBox.zMin >= this.zMax) {
//                double d0 = this.zMax - otherBox.zMin;
//
//                if (d0 > zOffset) {
//                    zOffset = d0;
//                }
//            }
//
//            return zOffset;
//        }
//        return zOffset;
//    }
}
