package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.core.tileentity.ColourMixerTileEntity;
import moe.plushie.armourers_workshop.core.tileentity.HologramProjectorTileEntity;
import moe.plushie.armourers_workshop.core.utils.AWDataAccessor;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateColourMixerPacket extends CustomPacket {

    private final BlockPos pos;
    private final Field field;
    private final Object fieldValue;

    public UpdateColourMixerPacket(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.field = buffer.readEnum(Field.class);
        this.fieldValue = field.getDataAccessor().dataSerializer.read(buffer);
    }

    public UpdateColourMixerPacket(ColourMixerTileEntity entity, Field field, Object value) {
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
        if (entity instanceof ColourMixerTileEntity) {
            field.set((ColourMixerTileEntity) entity, fieldValue);
        }
    }

    public enum Field {

        COLOUR(AWDataSerializers.PAINT_COLOR, ColourMixerTileEntity::getColor, ColourMixerTileEntity::setColor);

        private final AWDataAccessor<ColourMixerTileEntity, ?> dataAccessor;

        <T> Field(IDataSerializer<T> dataSerializer, Function<ColourMixerTileEntity, T> supplier, BiConsumer<ColourMixerTileEntity, T> applier) {
            this.dataAccessor = AWDataAccessor.of(dataSerializer, supplier, applier);
        }

        public <T> T get(ColourMixerTileEntity entity) {
            AWDataAccessor<ColourMixerTileEntity, T> dataAccessor = getDataAccessor();
            return dataAccessor.get(entity);
        }

        public <T> void set(ColourMixerTileEntity entity, T value) {
            AWDataAccessor<ColourMixerTileEntity, T> dataAccessor = getDataAccessor();
            dataAccessor.set(entity, value);
        }

        @SuppressWarnings("unchecked")
        public <T> AWDataAccessor<ColourMixerTileEntity, T> getDataAccessor() {
            return (AWDataAccessor<ColourMixerTileEntity, T>) dataAccessor;
        }
    }
}
