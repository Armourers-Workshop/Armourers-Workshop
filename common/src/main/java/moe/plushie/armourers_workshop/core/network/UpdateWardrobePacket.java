package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractEntityDataSerializer;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.DataAccessor;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.nbt.CompoundTag;
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

    private final Type type;
    private final int entityId;

    private final Field field;
    private final Object fieldValue;

    private final CompoundTag compoundTag;

    public UpdateWardrobePacket(IFriendlyByteBuf buffer) {
        this.type = buffer.readEnum(Type.class);
        this.entityId = buffer.readInt();
        if (this.type != Type.SYNC_OPTION) {
            this.fieldValue = null;
            this.field = null;
            this.compoundTag = buffer.readNbt();
        } else {
            this.field = buffer.readEnum(Field.class);
            this.fieldValue = field.accessor.read(buffer);
            this.compoundTag = null;
        }
    }

    public UpdateWardrobePacket(SkinWardrobe wardrobe, Type mode, CompoundTag compoundTag, Field field, Object fieldValue) {
        this.type = mode;
        this.entityId = wardrobe.getId();
        this.field = field;
        this.fieldValue = fieldValue;
        this.compoundTag = compoundTag;
    }

    public static UpdateWardrobePacket sync(SkinWardrobe wardrobe) {
        CompoundTag tag = new CompoundTag();
        AbstractDataSerializer serializer = AbstractDataSerializer.wrap(tag, wardrobe.getEntity());
        wardrobe.serialize(serializer);
        return new UpdateWardrobePacket(wardrobe, Type.SYNC, tag, null, null);
    }

    public static UpdateWardrobePacket dying(SkinWardrobe wardrobe, int slot, IPaintColor color) {
        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.putInt("Slot", slot);
        compoundNBT.putOptionalPaintColor("Color", color, null);
        return new UpdateWardrobePacket(wardrobe, Type.SYNC_ITEM, compoundNBT, null, null);
    }

    public static UpdateWardrobePacket field(SkinWardrobe wardrobe, Field field, Object value) {
        return new UpdateWardrobePacket(wardrobe, Type.SYNC_OPTION, null, field, value);
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeEnum(type);
        buffer.writeInt(entityId);
        if (compoundTag != null) {
            buffer.writeNbt(compoundTag);
        }
        if (field != null) {
            buffer.writeEnum(field);
            field.accessor.write(buffer, fieldValue);
        }
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // We can't allow wardrobe updates without container.
        String playerName = player.getDisplayName().getString();
        if (!(player.containerMenu instanceof SkinWardrobeMenu)) {
            ModLog.info("reject {} operation for '{}'", getOperator(), playerName);
            return;
        }
        if (!checkSecurityByServer()) {
            ModLog.info("reject {} operation for '{}', for security reasons.", getOperator(), playerName);
            return;
        }
        ModLog.debug("accept {} operation for '{}'", getOperator(), playerName);
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
        SkinWardrobe wardrobe = SkinWardrobe.of(player.getLevel().getEntity(entityId));
        if (wardrobe == null) {
            return null;
        }
        switch (type) {
            case SYNC: {
                wardrobe.deserialize(AbstractDataSerializer.wrap(compoundTag, player));
                return wardrobe;
            }
            case SYNC_ITEM: {
                Container inventory = wardrobe.getInventory();
                int slot = compoundTag.getInt("Slot");
                if (slot < inventory.getContainerSize()) {
                    IPaintColor color = compoundTag.getOptionalPaintColor("Color", null);
                    ItemStack itemStack = ItemStack.EMPTY;
                    if (color != null) {
                        itemStack = new ItemStack(ModItems.BOTTLE.get());
                        itemStack.set(ModDataComponents.TOOL_COLOR.get(), color);
                    }
                    inventory.setItem(slot, itemStack);
                    return wardrobe;
                }
                break;
            }
            case SYNC_OPTION: {
                if (field != null) {
                    field.accessor.set(wardrobe, fieldValue);
                    return wardrobe;
                }
                break;
            }
        }
        return null;
    }

    private boolean checkSecurityByServer() {
        switch (type) {
            case SYNC: {
                // the server side never accept sync request.
                return false;
            }
            case SYNC_ITEM: {
                int slot = compoundTag.getInt("Slot");
                // for security reasons we need to check the position of the slot.
                int index = slot - SkinSlotType.DYE.getIndex();
                if (index < 8 || index >= SkinSlotType.DYE.getMaxSize()) {
                    return false;
                }
                return true;
            }
            case SYNC_OPTION: {
                return true;
            }
        }
        return true;
    }

    private Object getOperator() {
        if (field != null) {
            return field;
        }
        return type;
    }

    public enum Type {
        SYNC, SYNC_ITEM, SYNC_OPTION
    }

    public enum Field implements DataAccessor.Provider<SkinWardrobe> {

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

        MANNEQUIN_POSE(MannequinEntity::saveCustomPose, MannequinEntity::readCustomPose, DataSerializers.COMPOUND_TAG),
        MANNEQUIN_POSITION(MannequinEntity::position, MannequinEntity::moveTo, DataSerializers.VECTOR_3D),

        MANNEQUIN_TEXTURE(MannequinEntity.DATA_TEXTURE);

        private final DataAccessor<SkinWardrobe, Object> accessor;

        Field(EquipmentSlot slotType) {
            this(w -> w.shouldRenderEquipment(slotType), (w, v) -> w.setRenderEquipment(slotType, v));
        }

        Field(Function<SkinWardrobe, Boolean> supplier, BiConsumer<SkinWardrobe, Boolean> applier) {
            this.accessor = DataAccessor.erased(DataSerializers.BOOLEAN, supplier, applier);
        }

        <S extends Entity, T> Field(Function<S, T> supplier, BiConsumer<S, T> applier, IEntitySerializer<T> dataSerializer) {
            this.accessor = DataAccessor.erased(dataSerializer,
                    (wardrobe) -> {
                        if (wardrobe.getEntity() != null) {
                            return supplier.apply(ObjectUtils.unsafeCast(wardrobe.getEntity()));
                        }
                        return null;
                    },
                    (wardrobe, value) -> {
                        if (wardrobe.getEntity() != null) {
                            applier.accept(ObjectUtils.unsafeCast(wardrobe.getEntity()), value);
                        }
                    });
        }

        <T> Field(EntityDataAccessor<T> dataParameter) {
            this(e -> e.getEntityData().get(dataParameter), (e, v) -> e.getEntityData().set(dataParameter, v), AbstractEntityDataSerializer.wrap(dataParameter));
        }

        @Override
        public DataAccessor<SkinWardrobe, Object> getAccessor() {
            return accessor;
        }
    }
}
