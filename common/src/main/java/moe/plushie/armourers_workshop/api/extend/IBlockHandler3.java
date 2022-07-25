package moe.plushie.armourers_workshop.api.extend;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.AABB;

public interface IBlockHandler3 {

    /**
     * Return an {@link AABB} that controls the visible scope of a {@link TileEntitySpecialRenderer} associated with this {@link BlockEntity}
     * Defaults to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block
     * at this location.
     *
     * @return an appropriately size {@link AABB} for the {@link net.minecraft.world.level.block.entity.BlockEntity}
     */
    @Environment(value = EnvType.CLIENT)
    AABB getRenderBoundingBox();
}
