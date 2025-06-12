package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Available("[1.21, )")
public abstract class AbstractBlockEntityImpl extends BlockEntity {

    public AbstractBlockEntityImpl(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public abstract void readAdditionalData(IDataSerializer serializer);

    public abstract void writeAdditionalData(IDataSerializer serializer);

    public abstract void sendBlockUpdates();

    @Override
    protected final void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.readAdditionalData(new TagSerializer(compoundTag, provider));
    }

    @Override
    protected final void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        this.writeAdditionalData(new TagSerializer(compoundTag, provider));
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        // when block not support updates, don't create an update packet.
        if (!(this instanceof IBlockEntityHandler)) {
            return null;
        }
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        var serializer = new TagSerializer(new CompoundTag(), provider);
        this.writeAdditionalData(serializer);
        return serializer.tag();
    }
}

