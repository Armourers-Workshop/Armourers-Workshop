package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.compat.core.blockentity.AbstractBlockEntity;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;

public class BlockEntityDataPacket extends CustomPacket {

    private final AbstractBlockEntity blockEntity;

    public BlockEntityDataPacket(AbstractBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public void handle(IDataSerializer serializer) {
        blockEntity.readAdditionalData(serializer);
        if (blockEntity instanceof UpdatableBlockEntity blockEntity1) {
            blockEntity1.sendBlockUpdates();
        }
    }

    public AbstractBlockEntity blockEntity() {
        return blockEntity;
    }
}
