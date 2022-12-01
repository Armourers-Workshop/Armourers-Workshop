package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBlockEntity extends BlockEntity implements IBlockEntityHandler {

    public AbstractBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType);
    }

    public abstract void readFromNBT(CompoundTag nbt);

    public abstract void writeToNBT(CompoundTag nbt);

    public abstract void sendBlockUpdates();

    @Override
    public void load(BlockState state, CompoundTag compoundTag) {
        super.load(state, compoundTag);
        this.readFromNBT(compoundTag);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        this.writeToNBT(compoundTag);
        return compoundTag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        this.writeToNBT(tag);
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, tag);
    }

    @Override
    public void handleUpdatePacket(BlockState state, CompoundTag tag) {
        this.readFromNBT(tag);
        this.sendBlockUpdates();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }
}
