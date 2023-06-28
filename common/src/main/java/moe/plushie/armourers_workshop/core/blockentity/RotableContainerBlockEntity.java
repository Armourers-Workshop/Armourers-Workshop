package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class RotableContainerBlockEntity extends UpdatableContainerBlockEntity implements IBlockEntityHandler {

    public static final AABB ZERO_BOX = new AABB(0, 0, 0, 0, 0, 0);

    private AABB renderBoundingBox;

    public RotableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public void setRenderChanged() {
        renderBoundingBox = null;
    }

    @Environment(EnvType.CLIENT)
    public OpenQuaternionf getRenderRotations(BlockState blockState) {
        return null;
    }

    @Environment(EnvType.CLIENT)
    public Rectangle3f getRenderBoundingBox(BlockState blockState) {
        return null;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AABB getCustomRenderBoundingBox(BlockState blockState) {
        if (renderBoundingBox != null) {
            return renderBoundingBox;
        }
        Rectangle3f rect = getRenderBoundingBox(blockState);
        if (rect == null) {
            return ZERO_BOX;
        }
        OpenQuaternionf quaternion = getRenderRotations(blockState);
        if (quaternion != null) {
            rect.mul(quaternion);
        }
        renderBoundingBox = rect.offset(Vector3d.atCenterOf(getBlockPos())).asAABB();
        return renderBoundingBox;
    }
}
