package moe.plushie.armourers_workshop.builder.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
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
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.Objects;
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
        var blockEntity = player.getLevel().getBlockEntity(pos);
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

    private void saveItem(Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container, CompoundTag nbt) {
        ModLog.info("accept save action of the {}, nbt: {}", player.getScoreboardName(), nbt);
        var profile = DataSerializers.readGameProfile(nbt);
        container.saveArmourItem(player, profile, null, null);
    }

    private void copyItem(Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container, CompoundTag nbt) throws Exception {
        ModLog.info("accept copy action of the {}, nbt: {}", player.getScoreboardName(), nbt);
        var isMirror = nbt.getOptionalBoolean(Constants.Key.MIRROR).orElse(false);
        var isCopyPaintData = nbt.getOptionalBoolean(Constants.Key.SKIN_PAINTS).orElse(false);
        var sourcePartType = SkinPartTypes.byName(nbt.getOptionalString(Constants.Key.SOURCE).orElse(""));
        var destinationPartType = SkinPartTypes.byName(nbt.getOptionalString(Constants.Key.DESTINATION).orElse(""));
        var collector = new CubeChangesCollector(blockEntity.getLevel());
        blockEntity.copyCubes(collector, sourcePartType, destinationPartType, isMirror);
        if (isCopyPaintData) {
            blockEntity.copyPaintData(collector, sourcePartType, destinationPartType, isMirror);
        }
        collector.submit(Component.translatable("action.armourers_workshop.block.copy"), player);
    }

    private void replaceItem(Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container, CompoundTag nbt) throws Exception {
        ModLog.info("accept replace action of the {}, nbt: {}", player.getScoreboardName(), nbt);
        var level = player.getLevel();
        var source = nbt.getOptionalCompound(Constants.Key.SOURCE).flatMap(tag -> ItemStack.parse(level.registryAccess(), tag)).orElse(ItemStack.EMPTY);
        var destination = nbt.getOptionalCompound(Constants.Key.DESTINATION).flatMap(tag -> ItemStack.parse(level.registryAccess(), tag)).orElse(ItemStack.EMPTY);
        var event = new CubeReplacingEvent(source, destination);
        event.keepColor = nbt.getOptionalBoolean(Constants.Key.KEEP_COLOR).orElse(false);
        event.keepPaintType = nbt.getOptionalBoolean(Constants.Key.KEEP_PAINT_TYPE).orElse(false);
        if (event.isEmptySource && event.isEmptyDestination) {
            return;
        }
        var collector = new CubeChangesCollector(blockEntity.getLevel());
        blockEntity.replaceCubes(collector, SkinPartTypes.UNKNOWN, event);
        collector.submit(Component.translatable("action.armourers_workshop.block.replace"), player);
        player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.armourer.dialog.replace.success", collector.total()));
    }

    private void clearItem(Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container, CompoundTag nbt) {
        ModLog.info("accept clear action of the {}, nbt: {}", player.getScoreboardName(), nbt);
        var collector = new CubeChangesCollector(blockEntity.getLevel());
        var partType = nbt.getOptionalString(Constants.Key.SKIN_PART_TYPE).map(SkinPartTypes::byName).orElse(SkinPartTypes.UNKNOWN);
        if (nbt.getOptionalBoolean(Constants.Key.SKIN_CUBES).orElse(false)) {
            blockEntity.clearCubes(collector, partType);
        }
        if (nbt.getOptionalBoolean(Constants.Key.SKIN_PAINTS).orElse(false)) {
            blockEntity.clearPaintData(collector, partType);
        }
        if (nbt.getOptionalBoolean(Constants.Key.SKIN_MARKERS).orElse(false) && !nbt.getOptionalBoolean(Constants.Key.SKIN_CUBES).orElse(false)) {
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
        public static final auto TEXTURE_MODEL = create(ArmourerBlockEntity::textureModel, ArmourerBlockEntity::setTextureModel, DataSerializers.PLAYER_TEXTURE_MODEL, ModPermissions.ARMOURER_SETTING);

        public static final auto ITEM_CLEAR = create(UpdateArmourerPacket::clearItem, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_CLEAR);
        public static final auto ITEM_COPY = create(UpdateArmourerPacket::copyItem, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_COPY);
        public static final auto ITEM_REPLACE = create(UpdateArmourerPacket::replaceItem, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_REPLACE);

        public static final auto ITEM_LOAD = create(UpdateArmourerPacket::loadItem, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_LOAD);
        public static final auto ITEM_SAVE = create(UpdateArmourerPacket::saveItem, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_SAVE);

        private FieldAction<T> action;
        private BlockPermission permission;

        private static <T> Field<T> create(FieldAction<T> action, IEntitySerializer<T> dataSerializer, BlockPermission permission) {
            Field<T> field = TYPE.create(dataSerializer).build(Field::new);
            field.action = action;
            field.permission = permission;
            return field;
        }

        private static <T> Field<T> create(Function<ArmourerBlockEntity, T> supplier, BiConsumer<ArmourerBlockEntity, T> applier, IEntitySerializer<T> dataSerializer, BlockPermission permission) {
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
}
