package moe.plushie.armourers_workshop.builder.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.builder.blockentity.ColorMixerBlockEntity;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.utils.DataAccessor;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateColorMixerPacket extends CustomPacket {

    private final BlockPos pos;
    private final Field field;
    private final Object fieldValue;

    public UpdateColorMixerPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.field = buffer.readEnum(Field.class);
        this.fieldValue = field.accessor.read(buffer);
    }

    public UpdateColorMixerPacket(ColorMixerBlockEntity entity, Field field, Object value) {
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
        if (entity instanceof ColorMixerBlockEntity) {
            field.accessor.set((ColorMixerBlockEntity) entity, fieldValue);
        }
    }

    public enum Field implements DataAccessor.Provider<ColorMixerBlockEntity> {

        COLOR(ColorMixerBlockEntity::getColor, ColorMixerBlockEntity::setColor, DataSerializers.PAINT_COLOR);

        private final DataAccessor<ColorMixerBlockEntity, Object> accessor;

        <T> Field(Function<ColorMixerBlockEntity, T> supplier, BiConsumer<ColorMixerBlockEntity, T> applier, IEntitySerializer<T> dataSerializer) {
            this.accessor = DataAccessor.erased(dataSerializer, supplier, applier);
        }

        @Override
        public DataAccessor<ColorMixerBlockEntity, Object> getAccessor() {
            return accessor;
        }
    }
}
