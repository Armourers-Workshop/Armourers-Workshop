package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBlockEntity extends BlockEntity implements IBlockEntityHandler {

    public AbstractBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        //#if MC >= 11800
        //# super(blockEntityType, blockPos, blockState);
        //#else
        super(blockEntityType);
        //#endif
    }

    public abstract void readFromNBT(CompoundTag nbt);

    public abstract void writeToNBT(CompoundTag nbt);

    public void sendBlockUpdates() {
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(getBlockPos(), state, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
    }

    //#if MC >= 11800
    //# @Override
    //# public void load(CompoundTag compoundTag) {
        //# super.load(compoundTag);
        //# this.readFromNBT(compoundTag);
    //# }
    //# @Override
    //# protected void saveAdditional(CompoundTag compoundTag) {
        //# super.saveAdditional(compoundTag);
        //# this.writeToNBT(compoundTag);
    //# }
    //#else
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
    //#endif

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        this.writeToNBT(tag);
        //#if MC >= 11800
        //# return ClientboundBlockEntityDataPacket.create(this, be -> tag);
        //#else
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, tag);
        //#endif
    }

    @Override
    public void handleUpdatePacket(BlockState state, CompoundTag tag) {
        this.readFromNBT(tag);
        this.sendBlockUpdates();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return DataSerializers.saveBlockTag(this);
    }
}
