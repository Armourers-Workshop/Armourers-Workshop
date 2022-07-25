package moe.plushie.armourers_workshop.core.blockentity;

import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.api.extend.IBlockHandler3;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class RotableContainerBlockEntity extends AbstractContainerBlockEntity implements IBlockHandler3 {

    public static final AABB ZERO_BOX = new AABB(0, 0, 0, 0, 0, 0);

    private AABB renderBoundingBox;

    public RotableContainerBlockEntity(BlockEntityType<?> entityType) {
        super(entityType);
    }

    public void setRenderChanged() {
        renderBoundingBox = null;
    }

    @Environment(value = EnvType.CLIENT)
    public Quaternion getRenderRotations() {
        return null;
    }

    @Environment(value = EnvType.CLIENT)
    public Rectangle3f getRenderBoundingBox(BlockState state) {
        return null;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public AABB getRenderBoundingBox() {
        if (renderBoundingBox != null) {
            return renderBoundingBox;
        }
        Rectangle3f rect = getRenderBoundingBox(getBlockState());
        if (rect == null) {
            return ZERO_BOX;
        }
        Quaternion quaternion = getRenderRotations();
        if (quaternion != null) {
            rect.mul(quaternion);
        }
        renderBoundingBox = rect.offset(Vector3d.atCenterOf(getBlockPos())).asAABB();
        return renderBoundingBox;
    }
}
