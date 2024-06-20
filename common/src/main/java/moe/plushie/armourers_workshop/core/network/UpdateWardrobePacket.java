package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.data.IGenericValue;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractEntityDataSerializer;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.GenericProperties;
import moe.plushie.armourers_workshop.core.data.GenericProperty;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

import manifold.ext.rt.api.auto;

public class UpdateWardrobePacket extends CustomPacket {

    private final Type type;
    private final int entityId;

    private final IGenericValue<SkinWardrobe, ?> fieldValue;

    private final CompoundTag compoundTag;

    public UpdateWardrobePacket(IFriendlyByteBuf buffer) {
        this.type = buffer.readEnum(Type.class);
        this.entityId = buffer.readInt();
        if (this.type != Type.SYNC_OPTION) {
            this.fieldValue = null;
            this.compoundTag = buffer.readNbt();
        } else {
            this.fieldValue = buffer.readProperty(Field.TYPE);
            this.compoundTag = null;
        }
    }

    public UpdateWardrobePacket(SkinWardrobe wardrobe, Type mode, CompoundTag compoundTag, IGenericValue<SkinWardrobe, ?> fieldValue) {
        this.type = mode;
        this.entityId = wardrobe.getId();
        this.fieldValue = fieldValue;
        this.compoundTag = compoundTag;
    }

    public static UpdateWardrobePacket sync(SkinWardrobe wardrobe) {
        var tag = new CompoundTag();
        var serializer = AbstractDataSerializer.wrap(tag, wardrobe.getEntity());
        wardrobe.serialize(serializer);
        return new UpdateWardrobePacket(wardrobe, Type.SYNC, tag, null);
    }

    public static UpdateWardrobePacket dying(SkinWardrobe wardrobe, int slot, IPaintColor color) {
        var compoundNBT = new CompoundTag();
        compoundNBT.putInt("Slot", slot);
        compoundNBT.putOptionalPaintColor("Color", color, null);
        return new UpdateWardrobePacket(wardrobe, Type.SYNC_ITEM, compoundNBT, null);
    }

    public static UpdateWardrobePacket field(SkinWardrobe wardrobe, IGenericValue<SkinWardrobe, ?> fieldValue) {
        return new UpdateWardrobePacket(wardrobe, Type.SYNC_OPTION, null, fieldValue);
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeEnum(type);
        buffer.writeInt(entityId);
        if (compoundTag != null) {
            buffer.writeNbt(compoundTag);
        }
        if (fieldValue != null) {
            buffer.writeProperty(fieldValue);
        }
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // We can't allow wardrobe updates without container.
        if (!(player.containerMenu instanceof SkinWardrobeMenu)) {
            ModLog.info("reject {} operation for '{}'", getOperator(), player.getDisplayName().getString());
            return;
        }
        if (!checkSecurityByServer()) {
            ModLog.info("reject {} operation for '{}', for security reasons.", getOperator(), player.getDisplayName().getString());
            return;
        }
        ModLog.debug("accept {} operation for '{}'", getOperator(), player.getDisplayName().getString());
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
        var wardrobe = SkinWardrobe.of(player.getLevel().getEntity(entityId));
        if (wardrobe == null) {
            return null;
        }
        return switch (type) {
            case SYNC -> {
                wardrobe.deserialize(AbstractDataSerializer.wrap(compoundTag, player));
                yield wardrobe;
            }
            case SYNC_OPTION -> {
                if (fieldValue != null) {
                    fieldValue.apply(wardrobe);
                    yield wardrobe;
                }
                yield null;
            }
            case SYNC_ITEM -> {
                var inventory = wardrobe.getInventory();
                int slot = compoundTag.getInt("Slot");
                if (slot < inventory.getContainerSize()) {
                    var color = compoundTag.getOptionalPaintColor("Color", null);
                    var itemStack = ItemStack.EMPTY;
                    if (color != null) {
                        itemStack = new ItemStack(ModItems.BOTTLE.get());
                        itemStack.set(ModDataComponents.TOOL_COLOR.get(), color);
                    }
                    inventory.setItem(slot, itemStack);
                    yield wardrobe;
                }
                yield null;
            }
        };
    }

    private boolean checkSecurityByServer() {
        return switch (type) {
            case SYNC -> false; // the server side never accept sync request.
            case SYNC_OPTION -> true;
            case SYNC_ITEM -> {
                int slot = compoundTag.getInt("Slot");
                // for security reasons we need to check the position of the slot.
                int index = slot - SkinSlotType.DYE.getIndex();
                if (index < 8 || index >= SkinSlotType.DYE.getMaxSize()) {
                    yield false;
                }
                yield true;
            }
        };
    }

    private Object getOperator() {
        if (fieldValue != null) {
            return fieldValue.getProperty();
        }
        return type;
    }

    public enum Type {
        SYNC, SYNC_ITEM, SYNC_OPTION
    }

    public static final class Field<T> extends GenericProperty<SkinWardrobe, T> {

        private static final auto TYPE = GenericProperties.of(SkinWardrobe.class, UpdateWardrobePacket::field);

        public static final auto WARDROBE_ARMOUR_HEAD = create(EquipmentSlot.HEAD);
        public static final auto WARDROBE_ARMOUR_CHEST = create(EquipmentSlot.CHEST);
        public static final auto WARDROBE_ARMOUR_LEGS = create(EquipmentSlot.LEGS);
        public static final auto WARDROBE_ARMOUR_FEET = create(EquipmentSlot.FEET);

        public static final auto WARDROBE_EXTRA_RENDER = create(SkinWardrobe::shouldRenderExtra, SkinWardrobe::setRenderExtra);

        public static final auto MANNEQUIN_IS_CHILD = create(MannequinEntity.DATA_IS_CHILD);
        public static final auto MANNEQUIN_IS_FLYING = create(MannequinEntity.DATA_IS_FLYING);
        public static final auto MANNEQUIN_IS_VISIBLE = create(MannequinEntity.DATA_IS_VISIBLE);
        public static final auto MANNEQUIN_IS_GHOST = create(MannequinEntity.DATA_IS_GHOST);
        public static final auto MANNEQUIN_EXTRA_RENDER = create(MannequinEntity.DATA_EXTRA_RENDERER);

        public static final auto MANNEQUIN_POSE = create(MannequinEntity::saveCustomPose, MannequinEntity::readCustomPose, DataSerializers.COMPOUND_TAG);
        public static final auto MANNEQUIN_POSITION = create(MannequinEntity::position, MannequinEntity::moveTo, DataSerializers.VECTOR_3D);

        public static final auto MANNEQUIN_TEXTURE = create(MannequinEntity.DATA_TEXTURE);


        private static Field<Boolean> create(EquipmentSlot slotType) {
            return create((source) -> source.shouldRenderEquipment(slotType), (source, value) -> source.setRenderEquipment(slotType, value));
        }

        private static Field<Boolean> create(Function<SkinWardrobe, Boolean> supplier, BiConsumer<SkinWardrobe, Boolean> applier) {
            return TYPE.create(DataSerializers.BOOLEAN).getter(supplier).setter(applier).build(Field::new);
        }

        private static <T> Field<T> create(EntityDataAccessor<T> dataParameter) {
            return create((entity) -> entity.getEntityData().get(dataParameter), (entity, value) -> entity.getEntityData().set(dataParameter, value), AbstractEntityDataSerializer.wrap(dataParameter));
        }

        private static <S extends Entity, T> Field<T> create(Function<S, T> supplier, BiConsumer<S, T> applier, IEntitySerializer<T> dataSerializer) {
            return TYPE.create(dataSerializer).getter((source) -> {
                if (source.getEntity() instanceof Entity entity) {
                    return supplier.apply(ObjectUtils.unsafeCast(entity));
                }
                return null;
            }).setter((source, value) -> {
                if (source.getEntity() instanceof Entity entity) {
                    applier.accept(ObjectUtils.unsafeCast(entity), value);
                }
            }).build(Field::new);
        }
    }
}
