package moe.plushie.armourers_workshop.builder.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.data.IGenericValue;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.builder.blockentity.OutfitMakerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.OutfitMakerMenu;
import moe.plushie.armourers_workshop.core.data.GenericProperties;
import moe.plushie.armourers_workshop.core.data.GenericProperty;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

import manifold.ext.rt.api.auto;

public class UpdateOutfitMakerPacket extends CustomPacket {

    private final BlockPos pos;
    private final IGenericValue<OutfitMakerBlockEntity, ?> fieldValue;

    public UpdateOutfitMakerPacket(IFriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.fieldValue = buffer.readProperty(Field.TYPE);
    }

    public UpdateOutfitMakerPacket(OutfitMakerBlockEntity entity, IGenericValue<OutfitMakerBlockEntity, ?> fieldValue) {
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
        var blockEntity = player.getLevel().getBlockEntity(pos);
        if (!(blockEntity instanceof OutfitMakerBlockEntity blockEntity1) || !(fieldValue.getProperty() instanceof Field<?> field)) {
            return;
        }
        // TODO: check player
        BlockUtils.performBatch(() -> {
            try {
                field.apply(this, blockEntity1, player);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void craftItem(OutfitMakerBlockEntity blockEntity, ServerPlayer player) {
        if (!ModPermissions.OUTFIT_MAKER_MAKE.accept(player)) {
            return;
        }
        if (player.containerMenu instanceof OutfitMakerMenu menu) {
            var nbt = (CompoundTag) fieldValue.getValue();
            var profile = DataSerializers.readGameProfile(nbt);
            menu.saveArmourItem(player, profile);
        }
    }

    public static final class Field<T> extends GenericProperty<OutfitMakerBlockEntity, T> {

        private static final auto TYPE = GenericProperties.of(OutfitMakerBlockEntity.class, UpdateOutfitMakerPacket::new);

        public static final auto ITEM_NAME = create(OutfitMakerBlockEntity::getItemName, OutfitMakerBlockEntity::setItemName, DataSerializers.STRING);
        public static final auto ITEM_FLAVOUR = create(OutfitMakerBlockEntity::getItemFlavour, OutfitMakerBlockEntity::setItemFlavour, DataSerializers.STRING);
        public static final auto ITEM_CRAFTING = create(UpdateOutfitMakerPacket::craftItem, DataSerializers.COMPOUND_TAG);

        private FieldAction<T> action;

        private static <T> Field<T> create(FieldAction<T> action, IEntitySerializer<T> dataSerializer) {
            Field<T> field = TYPE.create(dataSerializer).build(Field::new);
            field.action = action;
            return field;
        }

        private static <T> Field<T> create(Function<OutfitMakerBlockEntity, T> supplier, BiConsumer<OutfitMakerBlockEntity, T> applier, IEntitySerializer<T> dataSerializer) {
            return TYPE.create(dataSerializer).getter(supplier).setter(applier).build(Field::new);
        }

        private void apply(UpdateOutfitMakerPacket packet, OutfitMakerBlockEntity blockEntity, ServerPlayer player) throws Exception {
            if (action != null) {
                action.accept(packet, blockEntity, player);
            } else {
                packet.fieldValue.apply(blockEntity);
            }
        }
    }

    public interface FieldAction<T> {
        void accept(UpdateOutfitMakerPacket packet, OutfitMakerBlockEntity blockEntity, ServerPlayer player) throws Exception;
    }
}
