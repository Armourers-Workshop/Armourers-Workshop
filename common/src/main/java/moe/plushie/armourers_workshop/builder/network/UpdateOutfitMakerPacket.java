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
        this.fieldValue = field.accessor.read(buffer);
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
        field.accessor.write(buffer, fieldValue);
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
                BlockEntity entity = player.getLevel().getBlockEntity(pos);
                if (entity instanceof OutfitMakerBlockEntity) {
                    field.accessor.set((OutfitMakerBlockEntity) entity, fieldValue);
                }
                break;
            }
        }
    }

    public enum Field implements DataAccessor.Provider<OutfitMakerBlockEntity> {

        ITEM_NAME(OutfitMakerBlockEntity::getItemName, OutfitMakerBlockEntity::setItemName, DataSerializers.STRING),
        ITEM_FLAVOUR(OutfitMakerBlockEntity::getItemFlavour, OutfitMakerBlockEntity::setItemFlavour, DataSerializers.STRING),
        ITEM_CRAFTING(null, null, DataSerializers.COMPOUND_TAG);

        private final DataAccessor<OutfitMakerBlockEntity, Object> accessor;

        <T> Field(Function<OutfitMakerBlockEntity, T> supplier, BiConsumer<OutfitMakerBlockEntity, T> applier, IEntitySerializer<T> dataSerializer) {
            this.accessor = DataAccessor.erased(dataSerializer, supplier, applier);
        }

        @Override
        public DataAccessor<OutfitMakerBlockEntity, Object> getAccessor() {
            return accessor;
        }
    }
}
