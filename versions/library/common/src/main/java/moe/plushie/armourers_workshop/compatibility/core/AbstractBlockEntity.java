package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Available("[1.21, )")
public abstract class AbstractBlockEntity extends BlockEntity implements IBlockEntityHandler {

    public AbstractBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public abstract void readAdditionalData(IDataSerializer serializer);

    public abstract void writeAdditionalData(IDataSerializer serializer);

    public abstract void sendBlockUpdates();

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.readAdditionalData(AbstractDataSerializer.wrap(compoundTag, provider));
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        this.writeAdditionalData(AbstractDataSerializer.wrap(compoundTag, provider));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        this.writeAdditionalData(AbstractDataSerializer.wrap(tag, provider));
        return tag;
    }

    @Override
    public void handleUpdatePacket(BlockState state, IDataSerializer serializer) {
        this.readAdditionalData(serializer);
        this.sendBlockUpdates();
    }
}

