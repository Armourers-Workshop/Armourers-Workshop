package moe.plushie.armourers_workshop.builder.network;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.builder.blockentity.OutfitMakerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.OutfitMakerMenu;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.DataAccessor;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateOutfitMakerPacket extends CustomPacket {

    private final BlockPos pos;
    private final Field field;
    private final Object fieldValue;

    public UpdateOutfitMakerPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.field = buffer.readEnum(Field.class);
        this.fieldValue = field.getDataAccessor().dataSerializer.read(buffer);
    }

    public UpdateOutfitMakerPacket(OutfitMakerBlockEntity entity, Field field, Object value) {
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
        BlockUtils.beginCombiner();
        accept2(packetHandler, player);
        BlockUtils.endCombiner();
    }

    private void accept2(IServerPacketHandler packetHandler, ServerPlayer player) {
        switch (field) {
            case ITEM_CRAFTING: {
                if (!ModPermissions.OUTFIT_MAKER_MAKE.accept(player)) {
                    return;
                }
                if (player.containerMenu instanceof OutfitMakerMenu) {
                    CompoundTag nbt = (CompoundTag) fieldValue;
                    GameProfile profile = DataSerializers.readGameProfile(nbt);
                    ((OutfitMakerMenu) player.containerMenu).saveArmourItem(player, profile);
                }
                break;
            }
            case ITEM_NAME:
            case ITEM_FLAVOUR: {
                BlockEntity entity = player.level.getBlockEntity(pos);
                if (entity instanceof OutfitMakerBlockEntity) {
                    field.set((OutfitMakerBlockEntity) entity, fieldValue);
                }
                break;
            }
        }
    }


    public enum Field {

        ITEM_NAME(DataSerializers.STRING, OutfitMakerBlockEntity::getItemName, OutfitMakerBlockEntity::setItemName),
        ITEM_FLAVOUR(DataSerializers.STRING, OutfitMakerBlockEntity::getItemFlavour, OutfitMakerBlockEntity::setItemFlavour),
        ITEM_CRAFTING(DataSerializers.COMPOUND_TAG, null, null);

        private final DataAccessor<OutfitMakerBlockEntity, ?> dataAccessor;

        <T> Field(IEntitySerializer<T> dataSerializer, Function<OutfitMakerBlockEntity, T> supplier, BiConsumer<OutfitMakerBlockEntity, T> applier) {
            this.dataAccessor = DataAccessor.of(dataSerializer, supplier, applier);
        }

        public <T> T get(OutfitMakerBlockEntity entity) {
            DataAccessor<OutfitMakerBlockEntity, T> dataAccessor = getDataAccessor();
            return dataAccessor.get(entity);
        }

        public <T> void set(OutfitMakerBlockEntity entity, T value) {
            DataAccessor<OutfitMakerBlockEntity, T> dataAccessor = getDataAccessor();
            dataAccessor.set(entity, value);
        }

        public <T> DataAccessor<OutfitMakerBlockEntity, T> getDataAccessor() {
            return ObjectUtils.unsafeCast(dataAccessor);
        }
    }
}
