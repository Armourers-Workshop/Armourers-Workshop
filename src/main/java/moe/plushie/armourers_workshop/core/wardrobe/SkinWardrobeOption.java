package moe.plushie.armourers_workshop.core.wardrobe;

import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum SkinWardrobeOption {

    ARMOUR_HEAD(EquipmentSlotType.HEAD),
    ARMOUR_CHEST(EquipmentSlotType.CHEST),
    ARMOUR_LEGS(EquipmentSlotType.LEGS),
    ARMOUR_FEET(EquipmentSlotType.FEET),

    MANNEQUIN_IS_CHILD(MannequinEntity.DATA_IS_CHILD),
    MANNEQUIN_IS_FLYING(MannequinEntity.DATA_IS_FLYING),
    MANNEQUIN_IS_VISIBLE(MannequinEntity::isVisible, MannequinEntity::setVisible, DataSerializers.BOOLEAN),
    MANNEQUIN_IS_GHOST(MannequinEntity.DATA_IS_GHOST),
    MANNEQUIN_EXTRA_RENDER(MannequinEntity.DATA_EXTRA_RENDERER),

    MANNEQUIN_POSE(MannequinEntity::saveCustomPose, MannequinEntity::readCustomPose, DataSerializers.COMPOUND_TAG),
    MANNEQUIN_POSITION(MannequinEntity::position, MannequinEntity::moveTo, DataAccessor.SERIALIZER_VECTOR),
    MANNEQUIN_TEXTURE(MannequinEntity.DATA_TEXTURE);

    private final boolean broadcastChanges;
    private final DataAccessor<?> dataAccessor;

    SkinWardrobeOption(EquipmentSlotType slotType) {
        this.broadcastChanges = true;
        this.dataAccessor = DataAccessor.withDataSerializer(DataSerializers.BOOLEAN)
                .withSupplier((wardrobe) -> wardrobe.shouldRenderEquipment(slotType))
                .withApplier((wardrobe, value) -> wardrobe.setRenderEquipment(slotType, value));
    }

    @SuppressWarnings("unchecked")
    <T, D> SkinWardrobeOption(Function<T, D> getter, BiConsumer<T, D> setter, IDataSerializer<D> dataSerializer) {
        this.broadcastChanges = false;
        this.dataAccessor = DataAccessor.withDataSerializer(dataSerializer)
                .withSupplier((wardrobe) -> {
                    if (wardrobe.getEntity() != null) {
                        return getter.apply((T) wardrobe.getEntity());
                    }
                    return null;
                })
                .withApplier((wardrobe, value) -> {
                    if (wardrobe.getEntity() != null) {
                        setter.accept((T) wardrobe.getEntity(), value);
                    }
                });
    }

    <T> SkinWardrobeOption(DataParameter<T> dataParameter) {
        this.broadcastChanges = false;
        this.dataAccessor = DataAccessor.withDataSerializer(dataParameter.getSerializer())
                .withSupplier((wardrobe) -> entityData(wardrobe).map(data -> data.get(dataParameter)).orElse(null))
                .withApplier(((wardrobe, value) -> entityData(wardrobe).ifPresent(data -> data.set(dataParameter, value))));
    }

    private static Optional<EntityDataManager> entityData(SkinWardrobe wardrobe) {
        Entity entity = wardrobe.getEntity();
        if (entity != null) {
            return Optional.of(entity.getEntityData());
        }
        return Optional.empty();
    }

    public <T> void set(SkinWardrobe wardrobe, T value) {
        DataAccessor<T> dataAccessor = getDataAccessor();
        if (dataAccessor.applier != null) {
            dataAccessor.applier.accept(wardrobe, value);
        }
    }

    public <T> T get(SkinWardrobe wardrobe, T defaultValue) {
        DataAccessor<T> dataAccessor = getDataAccessor();
        if (dataAccessor.supplier != null) {
            T value = dataAccessor.supplier.apply(wardrobe);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> DataAccessor<T> getDataAccessor() {
        return (DataAccessor<T>) dataAccessor;
    }

    public <T> IDataSerializer<T> getDataSerializer() {
        DataAccessor<T> dataAccessor = getDataAccessor();
        return dataAccessor.dataSerializer;
    }

    public boolean isBroadcastChanges() {
        return broadcastChanges;
    }

    public static class DataAccessor<T> {
        public static final IDataSerializer<Vector3d> SERIALIZER_VECTOR = new IDataSerializer<Vector3d>() {
            public void write(PacketBuffer buffer, Vector3d pos) {
                buffer.writeDouble(pos.x());
                buffer.writeDouble(pos.y());
                buffer.writeDouble(pos.z());
            }

            public Vector3d read(PacketBuffer buffer) {
                return new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            }

            public Vector3d copy(Vector3d pos) {
                return pos;
            }
        };

        IDataSerializer<T> dataSerializer;
        Function<SkinWardrobe, T> supplier;
        BiConsumer<SkinWardrobe, T> applier;

        public static <T> DataAccessor<T> withDataSerializer(IDataSerializer<T> dataSerializer) {
            DataAccessor<T> dataAccessor = new DataAccessor<>();
            dataAccessor.dataSerializer = dataSerializer;
            return dataAccessor;
        }

        public DataAccessor<T> withApplier(BiConsumer<SkinWardrobe, T> applier) {
            this.applier = applier;
            return this;
        }

        public DataAccessor<T> withSupplier(Function<SkinWardrobe, T> supplier) {
            this.supplier = supplier;
            return this;
        }
    }
}
