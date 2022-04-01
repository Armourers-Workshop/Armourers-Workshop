package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.utils.AWDataAccessor;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.network.play.ServerPlayNetHandler;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class UpdateWardrobePacket extends CustomPacket {

    private final Mode mode;
    private final int entityId;

    private final Field field;
    private final Object fieldValue;

    private final CompoundNBT compoundNBT;

    public UpdateWardrobePacket(PacketBuffer buffer) {
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

    public UpdateWardrobePacket(SkinWardrobe wardrobe, Mode mode, CompoundNBT compoundNBT, Field field, Object fieldValue) {
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
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("Slot", slot);
        compoundNBT.put("Item", itemStack.serializeNBT());
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC_ITEM, compoundNBT, null, null);
    }

    public static UpdateWardrobePacket field(SkinWardrobe wardrobe, Field field, Object value) {
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC_OPTION, null, field, value);
    }

    @Override
    public void encode(PacketBuffer buffer) {
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
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        // TODO: check operator permission
        SkinWardrobe wardrobe = apply(player);
        if (wardrobe != null) {
            NetworkHandler.getInstance().sendToAll(this);
        }
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
        apply(player);
    }

    @Nullable
    private SkinWardrobe apply(PlayerEntity player) {
        SkinWardrobe wardrobe = SkinWardrobe.of(player.level.getEntity(entityId));
        if (wardrobe == null) {
            return null;
        }
        switch (mode) {
            case SYNC:
                wardrobe.deserializeNBT(compoundNBT);
                return wardrobe;

            case SYNC_ITEM:
                IInventory inventory = wardrobe.getInventory();
                int slot = compoundNBT.getInt("Slot");
                if (slot < inventory.getContainerSize()) {
                    inventory.setItem(slot, ItemStack.of(compoundNBT.getCompound("Item")));
                    return wardrobe;
                }
                break;

            case SYNC_OPTION:
                if (field != null) {
                    field.set(wardrobe, fieldValue);
                    if (field.isBroadcastChanges()) {
                        return wardrobe;
                    }
                }
        }
        return null;
    }

    public enum Mode {
        SYNC, SYNC_ITEM, SYNC_OPTION
    }

    public enum Field {

        ARMOUR_HEAD(EquipmentSlotType.HEAD),
        ARMOUR_CHEST(EquipmentSlotType.CHEST),
        ARMOUR_LEGS(EquipmentSlotType.LEGS),
        ARMOUR_FEET(EquipmentSlotType.FEET),

        MANNEQUIN_IS_CHILD(MannequinEntity.DATA_IS_CHILD),
        MANNEQUIN_IS_FLYING(MannequinEntity.DATA_IS_FLYING),
        MANNEQUIN_IS_VISIBLE(MannequinEntity.DATA_IS_VISIBLE),
        MANNEQUIN_IS_GHOST(MannequinEntity.DATA_IS_GHOST),
        MANNEQUIN_EXTRA_RENDER(MannequinEntity.DATA_EXTRA_RENDERER),

        MANNEQUIN_POSE(DataSerializers.COMPOUND_TAG, MannequinEntity::saveCustomPose, MannequinEntity::readCustomPose),
        MANNEQUIN_POSITION(AWDataSerializers.VECTOR_3D, MannequinEntity::position, MannequinEntity::moveTo),

        MANNEQUIN_TEXTURE(MannequinEntity.DATA_TEXTURE);

        private final boolean broadcastChanges;
        private final AWDataAccessor<SkinWardrobe, ?> dataAccessor;

        Field(EquipmentSlotType slotType) {
            this.broadcastChanges = true;
            this.dataAccessor = AWDataAccessor
                    .withDataSerializer(SkinWardrobe.class, DataSerializers.BOOLEAN)
                    .withSupplier((wardrobe) -> wardrobe.shouldRenderEquipment(slotType))
                    .withApplier((wardrobe, value) -> wardrobe.setRenderEquipment(slotType, value));
        }

        @SuppressWarnings("unchecked")
        <S extends Entity, T> Field(IDataSerializer<T> dataSerializer, Function<S, T> supplier, BiConsumer<S, T> applier) {
            this.broadcastChanges = false;
            this.dataAccessor = AWDataAccessor
                    .withDataSerializer(SkinWardrobe.class, dataSerializer)
                    .withSupplier((wardrobe) -> {
                        if (wardrobe.getEntity() != null) {
                            return supplier.apply((S) wardrobe.getEntity());
                        }
                        return null;
                    })
                    .withApplier((wardrobe, value) -> {
                        if (wardrobe.getEntity() != null) {
                            applier.accept((S) wardrobe.getEntity(), value);
                        }
                    });
        }

        <T> Field(DataParameter<T> dataParameter) {
            this(dataParameter.getSerializer(), e -> e.getEntityData().get(dataParameter), (e, v) -> e.getEntityData().set(dataParameter, v));
        }

        public <T> void set(SkinWardrobe wardrobe, T value) {
            AWDataAccessor<SkinWardrobe, T> dataAccessor = getDataAccessor();
            dataAccessor.set(wardrobe, value);        }

        public <T> T get(SkinWardrobe wardrobe, T defaultValue) {
            AWDataAccessor<SkinWardrobe, T> dataAccessor = getDataAccessor();
            T value = dataAccessor.get(wardrobe);
            if (value != null) {
                return value;
            }
            return defaultValue;
        }

        @SuppressWarnings("unchecked")
        public <T> AWDataAccessor<SkinWardrobe, T> getDataAccessor() {
            return (AWDataAccessor<SkinWardrobe, T>) dataAccessor;
        }

        public <T> IDataSerializer<T> getDataSerializer() {
            AWDataAccessor<SkinWardrobe, T> dataAccessor = getDataAccessor();
            return dataAccessor.dataSerializer;
        }

        public boolean isBroadcastChanges() {
            return broadcastChanges;
        }
    }
}
