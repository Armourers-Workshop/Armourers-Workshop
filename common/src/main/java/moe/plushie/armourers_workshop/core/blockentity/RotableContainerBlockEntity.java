package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public abstract class RotableContainerBlockEntity extends UpdatableContainerBlockEntity {

    public static final AABB ZERO_BOX = new AABB(0, 0, 0, 0, 0, 0);

    private AABB renderBoundingBox;

    public RotableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public void setRenderChanged() {
        renderBoundingBox = null;
    }

    public Optional<OpenQuaternionf> getRenderRotations(BlockState blockState) {
        return Optional.empty();
    }

    public Optional<OpenRectangle3f> getRenderShape(BlockState blockState) {
        return Optional.empty();
    }

    @Override
    public AABB getVisibleBox(BlockState blockState) {
        if (renderBoundingBox != null) {
            return renderBoundingBox;
        }
        var rect = getRenderShape(blockState).orElse(null);
        if (rect == null) {
            return ZERO_BOX;
        }
        var quaternion = getRenderRotations(blockState).orElse(null);
        if (quaternion != null) {
            rect.transform(quaternion);
        }
        var blockPos = getBlockPos();
        var box = rect.offset(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
        renderBoundingBox = new AABB(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
        return renderBoundingBox;
    }
}
