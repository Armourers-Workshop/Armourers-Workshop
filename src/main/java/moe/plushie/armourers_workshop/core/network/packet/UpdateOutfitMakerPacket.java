package moe.plushie.armourers_workshop.core.network.packet;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.builder.container.OutfitMakerContainer;
import moe.plushie.armourers_workshop.builder.tileentity.OutfitMakerTileEntity;
import moe.plushie.armourers_workshop.utils.AWDataAccessor;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateOutfitMakerPacket extends CustomPacket {

    private final BlockPos pos;
    private final Field field;
    private final Object fieldValue;

    public UpdateOutfitMakerPacket(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.field = buffer.readEnum(Field.class);
        this.fieldValue = field.getDataAccessor().dataSerializer.read(buffer);
    }

    public UpdateOutfitMakerPacket(OutfitMakerTileEntity entity, Field field, Object value) {
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
        TileEntityUpdateCombiner.begin();
        switch (field) {
            case ITEM_CRAFTING: {
                if (player.containerMenu instanceof OutfitMakerContainer) {
                    CompoundNBT nbt = (CompoundNBT)fieldValue;
                    GameProfile profile = NBTUtil.readGameProfile(nbt);
                    ((OutfitMakerContainer) player.containerMenu).saveArmourItem(player, profile);
                }
                break;
            }
            case ITEM_NAME:
            case ITEM_FLAVOUR: {
                TileEntity entity = player.level.getBlockEntity(pos);
                if (entity instanceof OutfitMakerTileEntity) {
                    field.set((OutfitMakerTileEntity) entity, fieldValue);
                }
                break;
            }
        }
        TileEntityUpdateCombiner.end();
    }

    public enum Field {

        ITEM_NAME(DataSerializers.STRING, OutfitMakerTileEntity::getItemName, OutfitMakerTileEntity::setItemName),
        ITEM_FLAVOUR(DataSerializers.STRING, OutfitMakerTileEntity::getItemFlavour, OutfitMakerTileEntity::setItemFlavour),
        ITEM_CRAFTING(DataSerializers.COMPOUND_TAG, null, null);

        private final AWDataAccessor<OutfitMakerTileEntity, ?> dataAccessor;

        <T> Field(IDataSerializer<T> dataSerializer, Function<OutfitMakerTileEntity, T> supplier, BiConsumer<OutfitMakerTileEntity, T> applier) {
            this.dataAccessor = AWDataAccessor.of(dataSerializer, supplier, applier);
        }

        public <T> T get(OutfitMakerTileEntity entity) {
            AWDataAccessor<OutfitMakerTileEntity, T> dataAccessor = getDataAccessor();
            return dataAccessor.get(entity);
        }

        public <T> void set(OutfitMakerTileEntity entity, T value) {
            AWDataAccessor<OutfitMakerTileEntity, T> dataAccessor = getDataAccessor();
            dataAccessor.set(entity, value);
        }

        @SuppressWarnings("unchecked")
        public <T> AWDataAccessor<OutfitMakerTileEntity, T> getDataAccessor() {
            return (AWDataAccessor<OutfitMakerTileEntity, T>) dataAccessor;
        }
    }
}
