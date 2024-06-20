package moe.plushie.armourers_workshop.builder.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.data.IGenericValue;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.builder.blockentity.ColorMixerBlockEntity;
import moe.plushie.armourers_workshop.core.data.GenericProperties;
import moe.plushie.armourers_workshop.core.data.GenericProperty;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

import manifold.ext.rt.api.auto;

public class UpdateColorMixerPacket extends CustomPacket {

    private final BlockPos pos;
    private final IGenericValue<ColorMixerBlockEntity, ?> fieldValue;

    public UpdateColorMixerPacket(IFriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.fieldValue = buffer.readProperty(Field.TYPE);
    }

    public UpdateColorMixerPacket(ColorMixerBlockEntity entity, IGenericValue<ColorMixerBlockEntity, ?> fieldValue) {
        this.pos = entity.getBlockPos();
        this.fieldValue = fieldValue;
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeProperty(fieldValue);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        var entity = player.getLevel().getBlockEntity(pos);
        if (entity instanceof ColorMixerBlockEntity blockEntity) {
            fieldValue.apply(blockEntity);
        }
    }

    public static final class Field<T> extends GenericProperty<ColorMixerBlockEntity, T> {

        private static final auto TYPE = GenericProperties.of(ColorMixerBlockEntity.class, UpdateColorMixerPacket::new);

        public static final auto COLOR = create(ColorMixerBlockEntity::getColor, ColorMixerBlockEntity::setColor, DataSerializers.PAINT_COLOR);

        private static <T> Field<T> create(Function<ColorMixerBlockEntity, T> supplier, BiConsumer<ColorMixerBlockEntity, T> applier, IEntitySerializer<T> dataSerializer) {
            return TYPE.create(dataSerializer).getter(supplier).setter(applier).build(Field::new);
        }
    }
}
