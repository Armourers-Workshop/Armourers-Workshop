package moe.plushie.armourers_workshop.core.network.packet;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.builder.container.ArmourerContainer;
import moe.plushie.armourers_workshop.builder.tileentity.ArmourerTileEntity;
import moe.plushie.armourers_workshop.builder.world.SkinCubeApplier;
import moe.plushie.armourers_workshop.builder.world.SkinCubeReplacingEvent;
import moe.plushie.armourers_workshop.core.permission.Permissions;
import moe.plushie.armourers_workshop.core.permission.impl.BlockPermission;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.utils.AWDataAccessor;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
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
        TileEntity tileEntity = player.level.getBlockEntity(pos);
        if (tileEntity instanceof ArmourerTileEntity && player.containerMenu instanceof ArmourerContainer) {
            TileEntityUpdateCombiner.begin();
            acceptFieldUpdate(player, (ArmourerTileEntity) tileEntity, (ArmourerContainer) player.containerMenu);
            TileEntityUpdateCombiner.end();
        }
    }

    private void acceptFieldUpdate(PlayerEntity player, ArmourerTileEntity tileEntity, ArmourerContainer container) {
        String playerName = player.getName().getContents();
        if (!field.permission.accept(tileEntity, player)) {
            return;
        }
        switch (field) {
            case ITEM_LOAD: {
                container.loadArmourItem(player);
                break;
            }
            case ITEM_SAVE: {
                CompoundNBT nbt = (CompoundNBT) fieldValue;
                ModLog.info("accept save action of the {}, nbt: {}", playerName, nbt);
                GameProfile profile = NBTUtil.readGameProfile(nbt);
                container.saveArmourItem(player, profile, null, null);
                break;
            }
            case ITEM_CLEAR: {
                CompoundNBT nbt = (CompoundNBT) fieldValue;
                ModLog.info("accept clear action of the {}, nbt: {}", playerName, nbt);
                SkinCubeApplier applier = new SkinCubeApplier(tileEntity.getLevel());
                ISkinPartType partType = SkinPartTypes.byName(nbt.getString(AWConstants.NBT.SKIN_PART_TYPE));
                if (nbt.getBoolean(AWConstants.NBT.SKIN_CUBES)) {
                    tileEntity.clearCubes(applier, partType);
                }
                if (nbt.getBoolean(AWConstants.NBT.SKIN_PAINTS)) {
                    tileEntity.clearPaintData(applier, partType);
                }
                if (nbt.getBoolean(AWConstants.NBT.SKIN_MARKERS) && !nbt.getBoolean(AWConstants.NBT.SKIN_CUBES)) {
                    tileEntity.clearMarkers(applier, partType);
                }
                applier.submit(TranslateUtils.title("action.armourers_workshop.block.clear"), player);
                break;
            }
            case ITEM_COPY: {
                CompoundNBT nbt = (CompoundNBT) fieldValue;
                ModLog.info("accept copy action of the {}, nbt: {}", playerName, nbt);
                try {
                    boolean isMirror = nbt.getBoolean(AWConstants.NBT.MIRROR);
                    boolean isCopyPaintData = nbt.getBoolean(AWConstants.NBT.SKIN_PAINTS);
                    ISkinPartType sourcePartType = SkinPartTypes.byName(nbt.getString(AWConstants.NBT.SOURCE));
                    ISkinPartType destinationPartType = SkinPartTypes.byName(nbt.getString(AWConstants.NBT.DESTINATION));
                    SkinCubeApplier applier = new SkinCubeApplier(tileEntity.getLevel());
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
                CompoundNBT nbt = (CompoundNBT) fieldValue;
                ModLog.info("accept replace action of the {}, nbt: {}", playerName, nbt);
                try {
                    ItemStack source = ItemStack.of(nbt.getCompound(AWConstants.NBT.SOURCE));
                    ItemStack destination = ItemStack.of(nbt.getCompound(AWConstants.NBT.DESTINATION));
                    SkinCubeReplacingEvent event = new SkinCubeReplacingEvent(source, destination);
                    event.keepColor = nbt.getBoolean(AWConstants.NBT.KEEP_COLOR);
                    event.keepPaintType = nbt.getBoolean(AWConstants.NBT.KEEP_PAINT_TYPE);
                    if (event.isEmptySource && event.isEmptyDestination) {
                        return;
                    }
                    SkinCubeApplier applier = new SkinCubeApplier(tileEntity.getLevel());
                    tileEntity.replaceCubes(applier, SkinPartTypes.UNKNOWN, event);
                    applier.submit(TranslateUtils.title("action.armourers_workshop.block.replace"), player);
                    player.sendMessage(TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.replace.success", applier.getChanges()), player.getUUID());
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
        FLAGS(DataSerializers.INT, ArmourerTileEntity::getFlags, ArmourerTileEntity::setFlags, Permissions.ARMOURER_SETTING),

        SKIN_TYPE(AWDataSerializers.SKIN_TYPE, ArmourerTileEntity::getSkinType, ArmourerTileEntity::setSkinType, Permissions.ARMOURER_SETTING),
        SKIN_PROPERTIES(AWDataSerializers.SKIN_PROPERTIES, ArmourerTileEntity::getSkinProperties, ArmourerTileEntity::setSkinProperties, Permissions.ARMOURER_SETTING),

        TEXTURE_DESCRIPTOR(AWDataSerializers.PLAYER_TEXTURE, ArmourerTileEntity::getTextureDescriptor, ArmourerTileEntity::setTextureDescriptor, Permissions.ARMOURER_SETTING),

        ITEM_CLEAR(DataSerializers.COMPOUND_TAG, null, null, Permissions.ARMOURER_CLEAR),
        ITEM_COPY(DataSerializers.COMPOUND_TAG, null, null, Permissions.ARMOURER_COPY),
        ITEM_REPLACE(DataSerializers.COMPOUND_TAG, null, null, Permissions.ARMOURER_REPLACE),

        ITEM_LOAD(DataSerializers.COMPOUND_TAG, null, null, Permissions.ARMOURER_LOAD),
        ITEM_SAVE(DataSerializers.COMPOUND_TAG, null, null, Permissions.ARMOURER_SAVE);

        public final BlockPermission permission;
        private final AWDataAccessor<ArmourerTileEntity, ?> dataAccessor;

        <T> Field(IDataSerializer<T> dataSerializer, Function<ArmourerTileEntity, T> supplier, BiConsumer<ArmourerTileEntity, T> applier, BlockPermission permission) {
            this.permission = permission;
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

        public BlockPermission getPermission() {
            return permission;
        }
    }
}
