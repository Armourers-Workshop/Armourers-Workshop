package moe.plushie.armourers_workshop.builder.network;

import moe.plushie.armourers_workshop.api.common.IEntityDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.other.BlockUtils;
import moe.plushie.armourers_workshop.builder.other.CubeChangesCollector;
import moe.plushie.armourers_workshop.builder.other.CubeReplacingEvent;
import moe.plushie.armourers_workshop.core.data.GenericProperties;
import moe.plushie.armourers_workshop.core.data.GenericProperty;
import moe.plushie.armourers_workshop.core.data.GenericValue;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.permission.BlockPermission;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenGameProfile;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Function;

import manifold.ext.rt.api.auto;

public class UpdateArmourerPacket extends CustomPacket {

    private final BlockPos pos;
    private final GenericValue<ArmourerBlockEntity, ?> fieldValue;

    public UpdateArmourerPacket(IFriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.fieldValue = Field.TYPE.read(buffer);
    }

    public UpdateArmourerPacket(ArmourerBlockEntity entity, GenericValue<ArmourerBlockEntity, ?> fieldValue) {
        this.pos = entity.getBlockPos();
        this.fieldValue = fieldValue;
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        fieldValue.write(buffer);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        var blockEntity = player.level().getBlockEntity(pos);
        if (!(blockEntity instanceof ArmourerBlockEntity blockEntity1) || !(player.containerMenu instanceof ArmourerMenu menu1) || !(fieldValue.property() instanceof Field<?> field)) {
            return;
        }
        BlockUtils.performBatch(() -> {
            try {
                field.apply(this, player, blockEntity1, menu1);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void loadItem(Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container, CompoundTag nbt) {
        container.loadArmourItem(player);
    }

    private void saveItem(Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container, CompoundTag tag) {
        ModLog.info("accept save action of the {}, object: {}", player.getScoreboardName(), tag);
        var serializer = new TagSerializer(tag);
        var profile = serializer.decode(OpenGameProfile.CODEC);
        container.saveArmourItem(player, profile, null, null);
    }

    private void copyItem(Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container, CompoundTag tag) throws Exception {
        ModLog.info("accept copy action of the {}, object: {}", player.getScoreboardName(), tag);
        var serializer = new TagSerializer(tag, SerializationContext.from(player));
        var isMirror = serializer.read(CodingKeys.COPY_MIRROR);
        var isCopyPaintData = serializer.read(CodingKeys.COPY_PAINT_DATA);
        var sourcePartType = serializer.read(CodingKeys.SOURCE_PART_TYPE);
        var destinationPartType = serializer.read(CodingKeys.DESTINATION_PART_TYPE);
        var collector = new CubeChangesCollector(blockEntity.getLevel());
        blockEntity.copyCubes(collector, sourcePartType, destinationPartType, isMirror);
        if (isCopyPaintData) {
            blockEntity.copyPaintData(collector, sourcePartType, destinationPartType, isMirror);
        }
        collector.submit(Component.translatable("action.armourers_workshop.block.copy"), player);
    }

    private void replaceItem(Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container, CompoundTag tag) throws Exception {
        ModLog.info("accept replace action of the {}, object: {}", player.getScoreboardName(), tag);
        var serializer = new TagSerializer(tag, SerializationContext.from(player));
        var source = serializer.read(CodingKeys.SOURCE_ITEM);
        var destination = serializer.read(CodingKeys.DESTINATION_ITEM);
        var event = new CubeReplacingEvent(source, destination);
        event.keepColor = serializer.read(CodingKeys.KEEP_COLOR);
        event.keepPaintType = serializer.read(CodingKeys.KEEP_PAINT_TYPE);
        if (event.isEmptySource && event.isEmptyDestination) {
            return;
        }
        var collector = new CubeChangesCollector(blockEntity.getLevel());
        blockEntity.replaceCubes(collector, SkinPartTypes.UNKNOWN, event);
        collector.submit(Component.translatable("action.armourers_workshop.block.replace"), player);
        player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.armourer.dialog.replace.success", collector.total()));
    }

    private void clearItem(Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container, CompoundTag tag) {
        ModLog.info("accept clear action of the {}, object: {}", player.getScoreboardName(), tag);
        var serializer = new TagSerializer(tag, SerializationContext.from(player));
        var collector = new CubeChangesCollector(blockEntity.getLevel());
        var partType = serializer.read(CodingKeys.PART_TYPE);
        if (serializer.read(CodingKeys.CLEAR_CUBES)) {
            blockEntity.clearCubes(collector, partType);
        }
        if (serializer.read(CodingKeys.CLEAR_PAINTS)) {
            blockEntity.clearPaintData(collector, partType);
        }
        if (serializer.read(CodingKeys.CLEAR_MARKERS) && !serializer.read(CodingKeys.CLEAR_CUBES)) {
            blockEntity.clearMarkers(collector, partType);
        }
        collector.submit(Component.translatable("action.armourers_workshop.block.clear"), player);
    }

    public static final class Field<T> extends GenericProperty<ArmourerBlockEntity, T> {

        private static final auto TYPE = GenericProperties.of(ArmourerBlockEntity.class, UpdateArmourerPacket::new);

        public static final auto FLAGS = create(ArmourerBlockEntity::getFlags, ArmourerBlockEntity::setFlags, DataSerializers.INT, ModPermissions.ARMOURER_SETTING);

        public static final auto SKIN_TYPE = create(ArmourerBlockEntity::skinType, ArmourerBlockEntity::setSkinType, DataSerializers.SKIN_TYPE, ModPermissions.ARMOURER_SETTING);
        public static final auto SKIN_PROPERTIES = create(ArmourerBlockEntity::skinProperties, ArmourerBlockEntity::setSkinProperties, DataSerializers.SKIN_PROPERTIES, ModPermissions.ARMOURER_SETTING);

        public static final auto TEXTURE_DESCRIPTOR = create(ArmourerBlockEntity::textureDescriptor, ArmourerBlockEntity::setTextureDescriptor, DataSerializers.PLAYER_TEXTURE, ModPermissions.ARMOURER_SETTING);

        public static final auto ITEM_CLEAR = create(UpdateArmourerPacket::clearItem, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_CLEAR);
        public static final auto ITEM_COPY = create(UpdateArmourerPacket::copyItem, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_COPY);
        public static final auto ITEM_REPLACE = create(UpdateArmourerPacket::replaceItem, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_REPLACE);

        public static final auto ITEM_LOAD = create(UpdateArmourerPacket::loadItem, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_LOAD);
        public static final auto ITEM_SAVE = create(UpdateArmourerPacket::saveItem, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_SAVE);

        private FieldAction<T> action;
        private BlockPermission permission;

        private static <T> Field<T> create(FieldAction<T> action, IEntityDataSerializer<T> dataSerializer, BlockPermission permission) {
            Field<T> field = TYPE.create(dataSerializer).build(Field::new);
            field.action = action;
            field.permission = permission;
            return field;
        }

        private static <T> Field<T> create(Function<ArmourerBlockEntity, T> supplier, BiConsumer<ArmourerBlockEntity, T> applier, IEntityDataSerializer<T> dataSerializer, BlockPermission permission) {
            Field<T> field = TYPE.create(dataSerializer).getter(supplier).setter(applier).build(Field::new);
            field.permission = permission;
            return field;
        }

        private void apply(UpdateArmourerPacket packet, Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container) throws Exception {
            // check permissions for every operation.
            if (!permission.accept(blockEntity, player)) {
                return;
            }
            if (action != null) {
                T value = Objects.unsafeCast(packet.fieldValue.value());
                action.accept(packet, player, blockEntity, container, value);
            } else {
                packet.fieldValue.apply(blockEntity);
            }
        }
    }

    public interface FieldAction<T> {
        void accept(UpdateArmourerPacket packet, Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container, T value) throws Exception;
    }

    public static class CodingKeys {

        public static final IDataSerializerKey<SkinPartType> PART_TYPE = IDataSerializerKey.create("PartType", SkinPartTypes.CODEC, SkinPartTypes.UNKNOWN);

        public static final IDataSerializerKey<ItemStack> SOURCE_ITEM = IDataSerializerKey.create("Source", ExtraCodecs.ITEM_STACK, ItemStack.EMPTY);
        public static final IDataSerializerKey<ItemStack> DESTINATION_ITEM = IDataSerializerKey.create("Destination", ExtraCodecs.ITEM_STACK, ItemStack.EMPTY);

        public static final IDataSerializerKey<SkinPartType> SOURCE_PART_TYPE = IDataSerializerKey.create("Source", SkinPartTypes.CODEC, SkinPartTypes.UNKNOWN);
        public static final IDataSerializerKey<SkinPartType> DESTINATION_PART_TYPE = IDataSerializerKey.create("Destination", SkinPartTypes.CODEC, SkinPartTypes.UNKNOWN);

        public static final IDataSerializerKey<Boolean> COPY_MIRROR = IDataSerializerKey.create("Mirror", IDataCodec.BOOL, false);
        public static final IDataSerializerKey<Boolean> COPY_PAINT_DATA = IDataSerializerKey.create("Paints", IDataCodec.BOOL, false);

        public static final IDataSerializerKey<Boolean> KEEP_COLOR = IDataSerializerKey.create("KeepColor", IDataCodec.BOOL, false);
        public static final IDataSerializerKey<Boolean> KEEP_PAINT_TYPE = IDataSerializerKey.create("KeepPaintType", IDataCodec.BOOL, false);

        public static final IDataSerializerKey<Boolean> CLEAR_CUBES = IDataSerializerKey.create("Cubes", IDataCodec.BOOL, false);
        public static final IDataSerializerKey<Boolean> CLEAR_PAINTS = IDataSerializerKey.create("Paints", IDataCodec.BOOL, false);
        public static final IDataSerializerKey<Boolean> CLEAR_MARKERS = IDataSerializerKey.create("Markers", IDataCodec.BOOL, false);
    }
}
