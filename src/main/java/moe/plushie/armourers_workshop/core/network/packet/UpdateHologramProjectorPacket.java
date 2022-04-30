package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.core.tileentity.HologramProjectorTileEntity;
import moe.plushie.armourers_workshop.utils.AWDataAccessor;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateHologramProjectorPacket extends CustomPacket {

    private final BlockPos pos;
    private final Field field;
    private final Object fieldValue;

    public UpdateHologramProjectorPacket(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.field = buffer.readEnum(Field.class);
        this.fieldValue = field.getDataAccessor().dataSerializer.read(buffer);
    }

    public UpdateHologramProjectorPacket(HologramProjectorTileEntity entity, Field field, Object value) {
        this.pos = entity.getBlockPos();
        this.field = field;
        this.fieldValue = value;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeEnum(field);
        field.getDataAccessor().dataSerializer.write(buffer, fieldValue);
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        // TODO: check player
        TileEntity entity = player.level.getBlockEntity(pos);
        if (entity instanceof HologramProjectorTileEntity) {
            field.set((HologramProjectorTileEntity) entity, fieldValue);
        }
    }

    public enum Field {

        POWER_MODE(DataSerializers.INT, HologramProjectorTileEntity::getPowerMode, HologramProjectorTileEntity::setPowerMode),
        IS_GLOWING(DataSerializers.BOOLEAN, HologramProjectorTileEntity::isGlowing, HologramProjectorTileEntity::setGlowing),

        SHOWS_ROTATION_POINT(DataSerializers.BOOLEAN, HologramProjectorTileEntity::shouldShowRotationPoint, HologramProjectorTileEntity::setShowRotationPoint),

        OFFSET(AWDataSerializers.VECTOR_3F, HologramProjectorTileEntity::getModelOffset, HologramProjectorTileEntity::setModelOffset),
        ANGLE(AWDataSerializers.VECTOR_3F, HologramProjectorTileEntity::getModelAngle, HologramProjectorTileEntity::setModelAngle),

        ROTATION_OFFSET(AWDataSerializers.VECTOR_3F, HologramProjectorTileEntity::getRotationOffset, HologramProjectorTileEntity::setRotationOffset),
        ROTATION_SPEED(AWDataSerializers.VECTOR_3F, HologramProjectorTileEntity::getRotationSpeed, HologramProjectorTileEntity::setRotationSpeed);

        private final AWDataAccessor<HologramProjectorTileEntity, ?> dataAccessor;

        <T> Field(IDataSerializer<T> dataSerializer, Function<HologramProjectorTileEntity, T> supplier, BiConsumer<HologramProjectorTileEntity, T> applier) {
            this.dataAccessor = AWDataAccessor.of(dataSerializer, supplier, applier);
        }

        public <T> T get(HologramProjectorTileEntity entity) {
            AWDataAccessor<HologramProjectorTileEntity, T> dataAccessor = getDataAccessor();
            return dataAccessor.get(entity);
        }

        public <T> void set(HologramProjectorTileEntity entity, T value) {
            AWDataAccessor<HologramProjectorTileEntity, T> dataAccessor = getDataAccessor();
            dataAccessor.set(entity, value);
        }

        @SuppressWarnings("unchecked")
        public <T> AWDataAccessor<HologramProjectorTileEntity, T> getDataAccessor() {
            return (AWDataAccessor<HologramProjectorTileEntity, T>) dataAccessor;
        }
    }
}
