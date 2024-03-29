package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.utils.DataAccessor;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
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
        this.fieldValue = field.getDataAccessor().dataSerializer.read(buffer);
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
        field.getDataAccessor().dataSerializer.write(buffer, fieldValue);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        BlockEntity entity = player.getLevel().getBlockEntity(pos);
        if (entity instanceof HologramProjectorBlockEntity) {
            field.set((HologramProjectorBlockEntity) entity, fieldValue);
        }
    }

    public enum Field {

        POWER_MODE(DataSerializers.INT, HologramProjectorBlockEntity::getPowerMode, HologramProjectorBlockEntity::setPowerMode),
        IS_GLOWING(DataSerializers.BOOLEAN, HologramProjectorBlockEntity::isGlowing, HologramProjectorBlockEntity::setGlowing),

        SHOWS_ROTATION_POINT(DataSerializers.BOOLEAN, HologramProjectorBlockEntity::shouldShowRotationPoint, HologramProjectorBlockEntity::setShowRotationPoint),

        OFFSET(DataSerializers.VECTOR_3F, HologramProjectorBlockEntity::getModelOffset, HologramProjectorBlockEntity::setModelOffset),
        ANGLE(DataSerializers.VECTOR_3F, HologramProjectorBlockEntity::getModelAngle, HologramProjectorBlockEntity::setModelAngle),

        ROTATION_OFFSET(DataSerializers.VECTOR_3F, HologramProjectorBlockEntity::getRotationOffset, HologramProjectorBlockEntity::setRotationOffset),
        ROTATION_SPEED(DataSerializers.VECTOR_3F, HologramProjectorBlockEntity::getRotationSpeed, HologramProjectorBlockEntity::setRotationSpeed);

        private final DataAccessor<HologramProjectorBlockEntity, ?> dataAccessor;

        <T> Field(IEntitySerializer<T> dataSerializer, Function<HologramProjectorBlockEntity, T> supplier, BiConsumer<HologramProjectorBlockEntity, T> applier) {
            this.dataAccessor = DataAccessor.of(dataSerializer, supplier, applier);
        }

        public <T> T get(HologramProjectorBlockEntity entity) {
            DataAccessor<HologramProjectorBlockEntity, T> dataAccessor = getDataAccessor();
            return dataAccessor.get(entity);
        }

        public <T> void set(HologramProjectorBlockEntity entity, T value) {
            DataAccessor<HologramProjectorBlockEntity, T> dataAccessor = getDataAccessor();
            dataAccessor.set(entity, value);
        }

        public <T> DataAccessor<HologramProjectorBlockEntity, T> getDataAccessor() {
            return ObjectUtils.unsafeCast(dataAccessor);
        }
    }
}
