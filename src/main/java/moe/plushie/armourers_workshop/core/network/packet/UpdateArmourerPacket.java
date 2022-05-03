package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.builder.tileentity.ArmourerTileEntity;
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

public class UpdateArmourerPacket extends CustomPacket {

    private final BlockPos pos;
    private final Field field;
    private final Object fieldValue;

    public UpdateArmourerPacket(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.field = buffer.readEnum(Field.class);
        this.fieldValue = field.getDataAccessor().dataSerializer.read(buffer);
    }

    public UpdateArmourerPacket(ArmourerTileEntity entity, Field field, Object value) {
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
        if (entity instanceof ArmourerTileEntity) {
            field.set((ArmourerTileEntity) entity, fieldValue);
        }
    }

    public enum Field {
        FLAGS(DataSerializers.INT, ArmourerTileEntity::getFlags, ArmourerTileEntity::setFlags),

        SKIN_TYPE(AWDataSerializers.SKIN_TYPE, ArmourerTileEntity::getSkinType, ArmourerTileEntity::setSkinType),
        SKIN_PROPERTIES(AWDataSerializers.SKIN_PROPERTIES, ArmourerTileEntity::getSkinProperties, ArmourerTileEntity::setSkinProperties),

        TEXTURE_DESCRIPTOR(AWDataSerializers.PLAYER_TEXTURE, ArmourerTileEntity::getTextureDescriptor, ArmourerTileEntity::setTextureDescriptor);

        private final AWDataAccessor<ArmourerTileEntity, ?> dataAccessor;

        <T> Field(IDataSerializer<T> dataSerializer, Function<ArmourerTileEntity, T> supplier, BiConsumer<ArmourerTileEntity, T> applier) {
            this.dataAccessor = AWDataAccessor.of(dataSerializer, supplier, applier);
        }

        public <T> T get(ArmourerTileEntity entity) {
            AWDataAccessor<ArmourerTileEntity, T> dataAccessor = getDataAccessor();
            return dataAccessor.get(entity);
        }

        public <T> void set(ArmourerTileEntity entity, T value) {
            AWDataAccessor<ArmourerTileEntity, T> dataAccessor = getDataAccessor();
            dataAccessor.set(entity, value);
        }

        @SuppressWarnings("unchecked")
        public <T> AWDataAccessor<ArmourerTileEntity, T> getDataAccessor() {
            return (AWDataAccessor<ArmourerTileEntity, T>) dataAccessor;
        }
    }
}
