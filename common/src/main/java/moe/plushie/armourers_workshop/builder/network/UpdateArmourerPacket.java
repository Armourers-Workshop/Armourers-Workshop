package moe.plushie.armourers_workshop.builder.network;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.other.CubeChangesCollector;
import moe.plushie.armourers_workshop.builder.other.CubeReplacingEvent;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.permission.BlockPermission;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataAccessor;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateArmourerPacket extends CustomPacket {

    private final BlockPos pos;
    private final Field field;
    private final Object fieldValue;

    public UpdateArmourerPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.field = buffer.readEnum(Field.class);
        this.fieldValue = field.accessor.read(buffer);
    }

    public UpdateArmourerPacket(ArmourerBlockEntity entity, Field field, Object value) {
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
        BlockEntity blockEntity = player.getLevel().getBlockEntity(pos);
        if (!(blockEntity instanceof ArmourerBlockEntity) || !(player.containerMenu instanceof ArmourerMenu)) {
            return;
        }
        BlockUtils.performBatch(() -> {
            acceptFieldUpdate(player, (ArmourerBlockEntity) blockEntity, (ArmourerMenu) player.containerMenu);
        });
    }

    private void acceptFieldUpdate(Player player, ArmourerBlockEntity blockEntity, ArmourerMenu container) {
        String playerName = player.getDisplayName().getString();
        if (!field.permission.accept(blockEntity, player)) {
            return;
        }
        switch (field) {
            case ITEM_LOAD: {
                container.loadArmourItem(player);
                break;
            }
            case ITEM_SAVE: {
                CompoundTag nbt = (CompoundTag) fieldValue;
                ModLog.info("accept save action of the {}, nbt: {}", playerName, nbt);
                GameProfile profile = DataSerializers.readGameProfile(nbt);
                container.saveArmourItem(player, profile, null, null);
                break;
            }
            case ITEM_CLEAR: {
                CompoundTag nbt = (CompoundTag) fieldValue;
                ModLog.info("accept clear action of the {}, nbt: {}", playerName, nbt);
                CubeChangesCollector collector = new CubeChangesCollector(blockEntity.getLevel());
                ISkinPartType partType = SkinPartTypes.byName(nbt.getString(Constants.Key.SKIN_PART_TYPE));
                if (nbt.getBoolean(Constants.Key.SKIN_CUBES)) {
                    blockEntity.clearCubes(collector, partType);
                }
                if (nbt.getBoolean(Constants.Key.SKIN_PAINTS)) {
                    blockEntity.clearPaintData(collector, partType);
                }
                if (nbt.getBoolean(Constants.Key.SKIN_MARKERS) && !nbt.getBoolean(Constants.Key.SKIN_CUBES)) {
                    blockEntity.clearMarkers(collector, partType);
                }
                collector.submit(Component.translatable("action.armourers_workshop.block.clear"), player);
                break;
            }
            case ITEM_COPY: {
                CompoundTag nbt = (CompoundTag) fieldValue;
                ModLog.info("accept copy action of the {}, nbt: {}", playerName, nbt);
                try {
                    boolean isMirror = nbt.getBoolean(Constants.Key.MIRROR);
                    boolean isCopyPaintData = nbt.getBoolean(Constants.Key.SKIN_PAINTS);
                    ISkinPartType sourcePartType = SkinPartTypes.byName(nbt.getString(Constants.Key.SOURCE));
                    ISkinPartType destinationPartType = SkinPartTypes.byName(nbt.getString(Constants.Key.DESTINATION));
                    CubeChangesCollector collector = new CubeChangesCollector(blockEntity.getLevel());
                    blockEntity.copyCubes(collector, sourcePartType, destinationPartType, isMirror);
                    if (isCopyPaintData) {
                        blockEntity.copyPaintData(collector, sourcePartType, destinationPartType, isMirror);
                    }
                    collector.submit(Component.translatable("action.armourers_workshop.block.copy"), player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ITEM_REPLACE: {
                CompoundTag nbt = (CompoundTag) fieldValue;
                ModLog.info("accept replace action of the {}, nbt: {}", playerName, nbt);
                try {
                    ItemStack source = ItemStack.of(nbt.getCompound(Constants.Key.SOURCE));
                    ItemStack destination = ItemStack.of(nbt.getCompound(Constants.Key.DESTINATION));
                    CubeReplacingEvent event = new CubeReplacingEvent(source, destination);
                    event.keepColor = nbt.getBoolean(Constants.Key.KEEP_COLOR);
                    event.keepPaintType = nbt.getBoolean(Constants.Key.KEEP_PAINT_TYPE);
                    if (event.isEmptySource && event.isEmptyDestination) {
                        return;
                    }
                    CubeChangesCollector collector = new CubeChangesCollector(blockEntity.getLevel());
                    blockEntity.replaceCubes(collector, SkinPartTypes.UNKNOWN, event);
                    collector.submit(Component.translatable("action.armourers_workshop.block.replace"), player);
                    player.sendSystemMessage(Component.translatable("inventory.armourers_workshop.armourer.dialog.replace.success", collector.getTotal()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                field.accessor.set(blockEntity, fieldValue);
                break;
            }
        }
    }

    public enum Field implements DataAccessor.Provider<ArmourerBlockEntity> {
        FLAGS(ArmourerBlockEntity::getFlags, ArmourerBlockEntity::setFlags, DataSerializers.INT, ModPermissions.ARMOURER_SETTING),

        SKIN_TYPE(ArmourerBlockEntity::getSkinType, ArmourerBlockEntity::setSkinType, DataSerializers.SKIN_TYPE, ModPermissions.ARMOURER_SETTING),
        SKIN_PROPERTIES(ArmourerBlockEntity::getSkinProperties, ArmourerBlockEntity::setSkinProperties, DataSerializers.SKIN_PROPERTIES, ModPermissions.ARMOURER_SETTING),

        TEXTURE_DESCRIPTOR(ArmourerBlockEntity::getTextureDescriptor, ArmourerBlockEntity::setTextureDescriptor, DataSerializers.PLAYER_TEXTURE, ModPermissions.ARMOURER_SETTING),

        ITEM_CLEAR(null, null, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_CLEAR),
        ITEM_COPY(null, null, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_COPY),
        ITEM_REPLACE(null, null, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_REPLACE),

        ITEM_LOAD(null, null, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_LOAD),
        ITEM_SAVE(null, null, DataSerializers.COMPOUND_TAG, ModPermissions.ARMOURER_SAVE);

        private final BlockPermission permission;
        private final DataAccessor<ArmourerBlockEntity, Object> accessor;

        <T> Field(Function<ArmourerBlockEntity, T> supplier, BiConsumer<ArmourerBlockEntity, T> applier, IEntitySerializer<T> dataSerializer, BlockPermission permission) {
            this.accessor = DataAccessor.erased(dataSerializer, supplier, applier);
            this.permission = permission;
        }

        @Override
        public DataAccessor<ArmourerBlockEntity, Object> getAccessor() {
            return accessor;
        }

        public BlockPermission getPermission() {
            return permission;
        }
    }
}
