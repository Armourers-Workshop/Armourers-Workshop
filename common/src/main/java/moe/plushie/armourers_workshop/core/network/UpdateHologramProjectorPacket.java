package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.utils.DataAccessor;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateHologramProjectorPacket extends CustomPacket {

    private final BlockPos pos;
    private final Field field;
    private final Object fieldValue;

    public UpdateHologramProjectorPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.field = buffer.readEnum(Field.class);
        this.fieldValue = field.accessor.read(buffer);
    }

    public UpdateHologramProjectorPacket(HologramProjectorBlockEntity entity, Field field, Object value) {
        this.pos = entity.getBlockPos();
        this.field = field;
        this.fieldValue = value;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeEnum(field);
        field.accessor.write(buffer, fieldValue);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        BlockEntity entity = player.getLevel().getBlockEntity(pos);
        if (entity instanceof HologramProjectorBlockEntity) {
            field.accessor.set((HologramProjectorBlockEntity) entity, fieldValue);
        }
    }

    public enum Field implements DataAccessor.Provider<HologramProjectorBlockEntity> {

        POWER_MODE(HologramProjectorBlockEntity::getPowerMode, HologramProjectorBlockEntity::setPowerMode, DataSerializers.INT),
        IS_GLOWING(HologramProjectorBlockEntity::isGlowing, HologramProjectorBlockEntity::setGlowing, DataSerializers.BOOLEAN),

        SHOWS_ROTATION_POINT(HologramProjectorBlockEntity::shouldShowRotationPoint, HologramProjectorBlockEntity::setShowRotationPoint, DataSerializers.BOOLEAN),

        OFFSET(HologramProjectorBlockEntity::getModelOffset, HologramProjectorBlockEntity::setModelOffset, DataSerializers.VECTOR_3F),
        ANGLE(HologramProjectorBlockEntity::getModelAngle, HologramProjectorBlockEntity::setModelAngle, DataSerializers.VECTOR_3F),

        ROTATION_OFFSET(HologramProjectorBlockEntity::getRotationOffset, HologramProjectorBlockEntity::setRotationOffset, DataSerializers.VECTOR_3F),
        ROTATION_SPEED(HologramProjectorBlockEntity::getRotationSpeed, HologramProjectorBlockEntity::setRotationSpeed, DataSerializers.VECTOR_3F);

        private final DataAccessor<HologramProjectorBlockEntity, Object> accessor;

        <T> Field(Function<HologramProjectorBlockEntity, T> supplier, BiConsumer<HologramProjectorBlockEntity, T> applier, IEntitySerializer<T> dataSerializer) {
            this.accessor = DataAccessor.erased(dataSerializer, supplier, applier);
        }

        @Override
        public DataAccessor<HologramProjectorBlockEntity, Object> getAccessor() {
            return accessor;
        }
    }
}
