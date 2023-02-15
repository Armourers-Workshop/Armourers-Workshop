package moe.plushie.armourers_workshop.builder.network;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.other.CubeApplier;
import moe.plushie.armourers_workshop.builder.other.CubeReplacingEvent;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.permission.BlockPermission;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.AWDataAccessor;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
        this.fieldValue = field.getDataAccessor().dataSerializer.read(buffer);
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
        field.getDataAccessor().dataSerializer.write(buffer, fieldValue);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        BlockEntity tileEntity = player.level.getBlockEntity(pos);
        if (tileEntity instanceof ArmourerBlockEntity && player.containerMenu instanceof ArmourerMenu) {
            BlockUtils.beginCombiner();
            acceptFieldUpdate(player, (ArmourerBlockEntity) tileEntity, (ArmourerMenu) player.containerMenu);
            BlockUtils.endCombiner();
        }
    }

    private void acceptFieldUpdate(Player player, ArmourerBlockEntity tileEntity, ArmourerMenu container) {
        String playerName = player.getDisplayName().getString();
        if (!field.permission.accept(tileEntity, player)) {
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
                CubeApplier applier = new CubeApplier(tileEntity.getLevel());
                ISkinPartType partType = SkinPartTypes.byName(nbt.getString(Constants.Key.SKIN_PART_TYPE));
                if (nbt.getBoolean(Constants.Key.SKIN_CUBES)) {
                    tileEntity.clearCubes(applier, partType);
                }
                if (nbt.getBoolean(Constants.Key.SKIN_PAINTS)) {
                    tileEntity.clearPaintData(applier, partType);
                }
                if (nbt.getBoolean(Constants.Key.SKIN_MARKERS) && !nbt.getBoolean(Constants.Key.SKIN_CUBES)) {
                    tileEntity.clearMarkers(applier, partType);
                }
                applier.submit(TranslateUtils.title("action.armourers_workshop.block.clear"), player);
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
                    CubeApplier applier = new CubeApplier(tileEntity.getLevel());
                    tileEntity.copyCubes(applier, sourcePartType, destinationPartType, isMirror);
                    if (isCopyPaintData) {
                        tileEntity.copyPaintData(applier, sourcePartType, destinationPartType, isMirror);
                    }
                    applier.submit(TranslateUtils.title("action.armourers_workshop.block.copy"), player);
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
                    CubeApplier applier = new CubeApplier(tileEntity.getLevel());
                    tileEntity.replaceCubes(applier, SkinPartTypes.UNKNOWN, event);
                    applier.submit(TranslateUtils.title("action.armourers_workshop.block.replace"), player);
                    player.sendSystemMessage(TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.replace.success", applier.getChanges()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                field.set(tileEntity, fieldValue);
                break;
            }
        }
    }

    public enum Field {
        FLAGS(DataSerializers.INT, ArmourerBlockEntity::getFlags, ArmourerBlockEntity::setFlags, ModPermissions.ARMOURER_SETTING),

        SKIN_TYPE(DataSerializers.SKIN_TYPE, ArmourerBlockEntity::getSkinType, ArmourerBlockEntity::setSkinType, ModPermissions.ARMOURER_SETTING),
        SKIN_PROPERTIES(DataSerializers.SKIN_PROPERTIES, ArmourerBlockEntity::getSkinProperties, ArmourerBlockEntity::setSkinProperties, ModPermissions.ARMOURER_SETTING),

        TEXTURE_DESCRIPTOR(DataSerializers.PLAYER_TEXTURE, ArmourerBlockEntity::getTextureDescriptor, ArmourerBlockEntity::setTextureDescriptor, ModPermissions.ARMOURER_SETTING),

        ITEM_CLEAR(DataSerializers.COMPOUND_TAG, null, null, ModPermissions.ARMOURER_CLEAR),
        ITEM_COPY(DataSerializers.COMPOUND_TAG, null, null, ModPermissions.ARMOURER_COPY),
        ITEM_REPLACE(DataSerializers.COMPOUND_TAG, null, null, ModPermissions.ARMOURER_REPLACE),

        ITEM_LOAD(DataSerializers.COMPOUND_TAG, null, null, ModPermissions.ARMOURER_LOAD),
        ITEM_SAVE(DataSerializers.COMPOUND_TAG, null, null, ModPermissions.ARMOURER_SAVE);

        public final BlockPermission permission;
        private final AWDataAccessor<ArmourerBlockEntity, ?> dataAccessor;

        <T> Field(IEntitySerializer<T> dataSerializer, Function<ArmourerBlockEntity, T> supplier, BiConsumer<ArmourerBlockEntity, T> applier, BlockPermission permission) {
            this.permission = permission;
            this.dataAccessor = AWDataAccessor.of(dataSerializer, supplier, applier);
        }

        public <T> T get(ArmourerBlockEntity entity) {
            AWDataAccessor<ArmourerBlockEntity, T> dataAccessor = getDataAccessor();
            return dataAccessor.get(entity);
        }

        public <T> void set(ArmourerBlockEntity entity, T value) {
            AWDataAccessor<ArmourerBlockEntity, T> dataAccessor = getDataAccessor();
            dataAccessor.set(entity, value);
        }

        public <T> AWDataAccessor<ArmourerBlockEntity, T> getDataAccessor() {
            return ObjectUtils.unsafeCast(dataAccessor);
        }

        public BlockPermission getPermission() {
            return permission;
        }
    }
}
