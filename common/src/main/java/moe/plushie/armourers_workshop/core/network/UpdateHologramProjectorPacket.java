package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.data.IGenericValue;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.data.GenericProperties;
import moe.plushie.armourers_workshop.core.data.GenericProperty;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

import manifold.ext.rt.api.auto;

public class UpdateHologramProjectorPacket extends CustomPacket {

    private final BlockPos pos;
    private final IGenericValue<HologramProjectorBlockEntity, ?> fieldValue;

    public UpdateHologramProjectorPacket(IFriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.fieldValue = buffer.readProperty(Field.TYPE);
    }

    public UpdateHologramProjectorPacket(HologramProjectorBlockEntity entity, IGenericValue<HologramProjectorBlockEntity, ?> fieldValue) {
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
        if (entity instanceof HologramProjectorBlockEntity blockEntity) {
            fieldValue.apply(blockEntity);
        }
    }

    public static final class Field<T> extends GenericProperty<HologramProjectorBlockEntity, T> {

        private static final auto TYPE = GenericProperties.of(HologramProjectorBlockEntity.class, UpdateHologramProjectorPacket::new);

        public static final auto POWER_MODE = create(HologramProjectorBlockEntity::getPowerMode, HologramProjectorBlockEntity::setPowerMode, DataSerializers.INT);
        public static final auto IS_GLOWING = create(HologramProjectorBlockEntity::isGlowing, HologramProjectorBlockEntity::setGlowing, DataSerializers.BOOLEAN);

        public static final auto SHOWS_ROTATION_POINT = create(HologramProjectorBlockEntity::shouldShowRotationPoint, HologramProjectorBlockEntity::setShowRotationPoint, DataSerializers.BOOLEAN);

        public static final auto OFFSET = create(HologramProjectorBlockEntity::getModelOffset, HologramProjectorBlockEntity::setModelOffset, DataSerializers.VECTOR_3F);
        public static final auto ANGLE = create(HologramProjectorBlockEntity::getModelAngle, HologramProjectorBlockEntity::setModelAngle, DataSerializers.VECTOR_3F);

        public static final auto ROTATION_OFFSET = create(HologramProjectorBlockEntity::getRotationOffset, HologramProjectorBlockEntity::setRotationOffset, DataSerializers.VECTOR_3F);
        public static final auto ROTATION_SPEED = create(HologramProjectorBlockEntity::getRotationSpeed, HologramProjectorBlockEntity::setRotationSpeed, DataSerializers.VECTOR_3F);

        private static <T> Field<T> create(Function<HologramProjectorBlockEntity, T> supplier, BiConsumer<HologramProjectorBlockEntity, T> applier, IEntitySerializer<T> dataSerializer) {
            return TYPE.create(dataSerializer).setter(applier).getter(supplier).build(Field::new);
        }
    }
}
