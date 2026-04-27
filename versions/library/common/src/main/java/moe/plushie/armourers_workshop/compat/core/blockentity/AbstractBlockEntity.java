package moe.plushie.armourers_workshop.compat.core.blockentity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntity;
import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.core.network.BlockEntityDataPacket;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@Available("[16, )")
public abstract class AbstractBlockEntity extends AbstractBlockEntityImpl implements IBlockEntity {

    public AbstractBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    protected abstract void abi$readAdditionalData(IDataSerializer serializer);

    protected abstract void abi$writeAdditionalData(IDataSerializer serializer);

    @Nullable
    protected BlockEntityDataPacket abi$getUpdateDataPacket() {
        return null;
    }

    @Nullable
    protected <T> T abi$getCapability(IBlockEntityCapability<T> capability, @Nullable OpenDirection dir) {
        return null;
    }

    /// API Implements

    @Override
    public final void readAdditionalData(IDataSerializer serializer) {
        abi$readAdditionalData(serializer);
    }

    @Override
    public final void writeAdditionalData(IDataSerializer serializer) {
        abi$writeAdditionalData(serializer);
    }

    @Override
    public final BlockEntityDataPacket getUpdateDataPacket() {
        return abi$getUpdateDataPacket();
    }

    @Nullable
    public final <T> T getCapability(IBlockEntityCapability<T> capability, @Nullable OpenDirection dir) {
        return abi$getCapability(capability, dir);
    }
}

