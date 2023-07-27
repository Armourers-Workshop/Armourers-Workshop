package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.builder.data.properties.BooleanProperty;
import moe.plushie.armourers_workshop.builder.data.properties.VectorProperty;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class AdvancedSkinBuilderBlockEntity extends UpdatableBlockEntity implements IBlockEntityHandler {

    private AABB renderBoundingBox;

    public final Vector3f carmeOffset = new Vector3f();
    public final Vector3f carmeRot = new Vector3f();
    public final Vector3f carmeScale = new Vector3f(1, 1, 1);

    //public SkinDescriptor descriptor = new SkinDescriptor("db:QoTHtJTeeZ");
    public SkinDescriptor descriptor = new SkinDescriptor("ks:10830");
    public Vector3f offset = new Vector3f(0, 10, 0);

    public final BooleanProperty mirror2 = new BooleanProperty();
    public final BooleanProperty enabled2 = new BooleanProperty();

    public final VectorProperty location2 = new VectorProperty();
    public final VectorProperty rotation2 = new VectorProperty();
    public final VectorProperty scale2 = new VectorProperty();

    public Vector3f getRenderOrigin() {
        BlockPos pos = getBlockPos();
        return new Vector3f(
                pos.getX() + offset.getX() + 0.5f,
                pos.getY() + offset.getY() + 0.5f,
                pos.getZ() + offset.getZ() + 0.5f
        );
    }

    public AdvancedSkinBuilderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AABB getCustomRenderBoundingBox(BlockState blockState) {
        if (renderBoundingBox != null) {
            return renderBoundingBox;
        }
        float s = 16;
        Vector3f origin = getRenderOrigin();
        Rectangle3f rect = new Rectangle3f(origin.getX() - s / 2, origin.getY() - s / 2, origin.getZ() - s / 2, s, s, s);
        renderBoundingBox = rect.asAABB();
        return renderBoundingBox;
    }
}
