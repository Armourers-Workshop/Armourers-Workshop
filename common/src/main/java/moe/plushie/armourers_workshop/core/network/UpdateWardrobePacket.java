package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.AWDataAccessor;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateWardrobePacket extends CustomPacket {

    private final Mode mode;
    private final int entityId;

    private final Field field;
    private final Object fieldValue;

    private final CompoundTag compoundNBT;

    public UpdateWardrobePacket(FriendlyByteBuf buffer) {
        this.mode = buffer.readEnum(Mode.class);
        this.entityId = buffer.readInt();
        if (this.mode != Mode.SYNC_OPTION) {
            this.fieldValue = null;
            this.field = null;
            this.compoundNBT = buffer.readNbt();
        } else {
            this.field = buffer.readEnum(Field.class);
            this.fieldValue = field.getDataSerializer().read(buffer);
            this.compoundNBT = null;
        }
    }

    public UpdateWardrobePacket(SkinWardrobe wardrobe, Mode mode, CompoundTag compoundNBT, Field field, Object fieldValue) {
        this.mode = mode;
        this.entityId = wardrobe.getId();
        this.field = field;
        this.fieldValue = fieldValue;
        this.compoundNBT = compoundNBT;
    }

    public static UpdateWardrobePacket sync(SkinWardrobe wardrobe) {
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC, wardrobe.serializeNBT(), null, null);
    }

    public static UpdateWardrobePacket pick(SkinWardrobe wardrobe, int slot, ItemStack itemStack) {
        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.putInt("Slot", slot);
        compoundNBT.put("Item", itemStack.save(new CompoundTag()));
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC_ITEM, compoundNBT, null, null);
    }

    public static UpdateWardrobePacket field(SkinWardrobe wardrobe, Field field, Object value) {
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC_OPTION, null, field, value);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(mode);
        buffer.writeInt(entityId);
        if (compoundNBT != null) {
            buffer.writeNbt(compoundNBT);
        }
        if (field != null) {
            buffer.writeEnum(field);
            field.getDataSerializer().write(buffer, fieldValue);
        }
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // We can't allow wardrobe updates without container.
        String playerName = player.getDisplayName().getString();
        if (!(player.containerMenu instanceof SkinWardrobeMenu)) {
            ModLog.info("the wardrobe {} operation rejected for '{}'", field, playerName);
            return;
        }
        ModLog.debug("the wardrobe {} operation accepted for '{}'", field, playerName);
        SkinWardrobe wardrobe = apply(player);
        if (wardrobe != null) {
            NetworkManager.sendToTracking(this, player);
        }
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        apply(player);
    }

    @Nullable
    private SkinWardrobe apply(Player player) {
        SkinWardrobe wardrobe = SkinWardrobe.of(player.level.getEntity(entityId));
        if (wardrobe == null) {
            return null;
        }
        switch (mode) {
            case SYNC:
                wardrobe.deserializeNBT(compoundNBT);
                return wardrobe;

            case SYNC_ITEM:
                Container inventory = wardrobe.getInventory();
                int slot = compoundNBT.getInt("Slot");
                if (slot < inventory.getContainerSize()) {
                    inventory.setItem(slot, ItemStack.of(compoundNBT.getCompound("Item")));
                    return wardrobe;
                }
                break;

            case SYNC_OPTION:
                if (field != null) {
                    field.set(wardrobe, fieldValue);
                    return wardrobe;
                }
        }
        return null;
    }

    public enum Mode {
        SYNC, SYNC_ITEM, SYNC_OPTION
    }

    public enum Field {

        WARDROBE_ARMOUR_HEAD(EquipmentSlot.HEAD),
        WARDROBE_ARMOUR_CHEST(EquipmentSlot.CHEST),
        WARDROBE_ARMOUR_LEGS(EquipmentSlot.LEGS),
        WARDROBE_ARMOUR_FEET(EquipmentSlot.FEET),

        WARDROBE_EXTRA_RENDER(SkinWardrobe::shouldRenderExtra, SkinWardrobe::setRenderExtra),

        MANNEQUIN_IS_CHILD(MannequinEntity.DATA_IS_CHILD),
        MANNEQUIN_IS_FLYING(MannequinEntity.DATA_IS_FLYING),
        MANNEQUIN_IS_VISIBLE(MannequinEntity.DATA_IS_VISIBLE),
        MANNEQUIN_IS_GHOST(MannequinEntity.DATA_IS_GHOST),
        MANNEQUIN_EXTRA_RENDER(MannequinEntity.DATA_EXTRA_RENDERER),

        MANNEQUIN_POSE(DataSerializers.COMPOUND_TAG, MannequinEntity::saveCustomPose, MannequinEntity::readCustomPose),
        MANNEQUIN_POSITION(DataSerializers.VECTOR_3D, MannequinEntity::position, MannequinEntity::moveTo),

        MANNEQUIN_TEXTURE(MannequinEntity.DATA_TEXTURE);

        private final boolean broadcastChanges;
        private final AWDataAccessor<SkinWardrobe, ?> dataAccessor;

        Field(EquipmentSlot slotType) {
            this(w -> w.shouldRenderEquipment(slotType), (w, v) -> w.setRenderEquipment(slotType, v));
        }

        Field(Function<SkinWardrobe, Boolean> supplier, BiConsumer<SkinWardrobe, Boolean> applier) {
            this.broadcastChanges = true;
            this.dataAccessor = AWDataAccessor
                    .withDataSerializer(SkinWardrobe.class, DataSerializers.BOOLEAN)
                    .withSupplier(supplier)
                    .withApplier(applier);
        }

        <S extends Entity, T> Field(IEntitySerializer<T> dataSerializer, Function<S, T> supplier, BiConsumer<S, T> applier) {
            this.broadcastChanges = false;
            this.dataAccessor = AWDataAccessor
                    .withDataSerializer(SkinWardrobe.class, dataSerializer)
                    .withSupplier((wardrobe) -> {
                        if (wardrobe.getEntity() != null) {
                            return supplier.apply(ObjectUtils.unsafeCast(wardrobe.getEntity()));
                        }
                        return null;
                    })
                    .withApplier((wardrobe, value) -> {
                        if (wardrobe.getEntity() != null) {
                            applier.accept(ObjectUtils.unsafeCast(wardrobe.getEntity()), value);
                        }
                    });
        }

        <T> Field(EntityDataAccessor<T> dataParameter) {
            this(DataSerializers.of(dataParameter.getSerializer()), e -> e.getEntityData().get(dataParameter), (e, v) -> e.getEntityData().set(dataParameter, v));
        }

        public <T> void set(SkinWardrobe wardrobe, T value) {
            AWDataAccessor<SkinWardrobe, T> dataAccessor = getDataAccessor();
            dataAccessor.set(wardrobe, value);
        }

        public <T> T get(SkinWardrobe wardrobe, T defaultValue) {
            AWDataAccessor<SkinWardrobe, T> dataAccessor = getDataAccessor();
            T value = dataAccessor.get(wardrobe);
            if (value != null) {
                return value;
            }
            return defaultValue;
        }

        public <T> AWDataAccessor<SkinWardrobe, T> getDataAccessor() {
            return ObjectUtils.unsafeCast(dataAccessor);
        }

        public <T> IEntitySerializer<T> getDataSerializer() {
            AWDataAccessor<SkinWardrobe, T> dataAccessor = getDataAccessor();
            return dataAccessor.dataSerializer;
        }
    }
}
