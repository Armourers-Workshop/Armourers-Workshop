package moe.plushie.armourers_workshop.compat.core.blockentity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.core.network.BlockEntityDataPacket;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@Available("[1.21, 1.22)")
public abstract class AbstractBlockEntityImpl extends BlockEntity {

    public AbstractBlockEntityImpl(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public abstract void readAdditionalData(IDataSerializer serializer);

    public abstract void writeAdditionalData(IDataSerializer serializer);

    @Override
    protected final void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.readAdditionalData(new TagSerializer(compoundTag, SerializationContext.from(provider)));
    }

    @Override
    protected final void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        this.writeAdditionalData(new TagSerializer(compoundTag, SerializationContext.from(provider)));
    }

    @Nullable
    public BlockEntityDataPacket getUpdateDataPacket() {
        return null;
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        // when block not support updates, don't create an update packet.
        var packet = getUpdateDataPacket();
        if (packet != null) {
            return ClientboundBlockEntityDataPacket.create(packet.blockEntity());
        }
        return null;
    }

    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        var serializer = new TagSerializer(SerializationContext.from(provider));
        this.writeAdditionalData(serializer);
        return serializer.tag();
    }
}

