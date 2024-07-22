package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ICapabilityType;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IDataSerializerProvider;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeStorage;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Available("[1.21, )")
public class AbstractForgeCapabilityManager {

    public static <T> IRegistryHolder<ICapabilityType<T>> register(IResourceLocation registryName, Class<T> type, Function<Entity, Optional<T>> factory) {
        if (type == SkinWardrobe.class) {
            return ObjectUtils.unsafeCast(createWardrobeCapabilityType(registryName, ObjectUtils.unsafeCast(factory)));
        }
        throw new AssertionError();
    }

    private static IRegistryHolder<ICapabilityType<SkinWardrobe>> createWardrobeCapabilityType(IResourceLocation registryName, Function<Entity, Optional<SkinWardrobe>> provider) {
        EntityCapability<SkinWardrobe, Void> capability = EntityCapability.createVoid(registryName.toLocation(), SkinWardrobe.class);
        ICapabilityType<SkinWardrobe> capabilityType = entity -> Optional.ofNullable(entity.getCapability(capability));
        return new Proxy<>(registryName, SkinWardrobe.class, provider, capabilityType, () -> capability);
    }

    public static class Proxy<T extends IDataSerializerProvider> implements IRegistryHolder<ICapabilityType<T>> {

        final IResourceLocation registryName;
        final Supplier<EntityCapability<T, Void>> capability;

        final Class<T> type;
        final ICapabilityType<T> capabilityType;
        final IRegistryHolder<AttachmentType<Serializer<T>>> attachmentType;

        protected Proxy(IResourceLocation registryName, Class<T> type, Function<Entity, Optional<T>> factory, ICapabilityType<T> capabilityType, Supplier<EntityCapability<T, Void>> capability) {
            this.type = type;
            this.attachmentType = Serializer.register(registryName, factory);
            this.capability = capability;
            this.capabilityType = capabilityType;
            this.registryName = registryName;
            this.register();
        }

        public void register() {
            AbstractForgeEventBus.observer(RegisterCapabilitiesEvent.class, event -> {
                for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                    event.registerEntity(capability.get(), entityType, (entity, context) -> entity.getData(attachmentType).getValue());
                }
            });
        }

        @Override
        public ICapabilityType<T> get() {
            return capabilityType;
        }

        @Override
        public IResourceLocation getRegistryName() {
            return registryName;
        }
    }

    public static class Serializer<T extends IDataSerializerProvider> implements INBTSerializable<CompoundTag> {

        protected static final ArrayList<String> DATA_KEYS = new ArrayList<>();

        protected final T value;
        protected final WeakReference<Entity> entity;

        protected Serializer(Entity entity, T value) {
            this.value = value;
            this.entity = new WeakReference<>(entity);
        }

        public static <T extends IDataSerializerProvider> IRegistryHolder<AttachmentType<Serializer<T>>> register(IResourceLocation registryName, Function<Entity, Optional<T>> factory) {
            DATA_KEYS.add(registryName.toString());
            Function<IAttachmentHolder, Serializer<T>> transformer = holder -> new Serializer<>((Entity) holder, factory.apply((Entity) holder).orElse(null));
            return AbstractForgeRegistries.ATTACHMENT_TYPES.register(registryName.getPath(), () -> AttachmentType.serializable(transformer).build());
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            if (value != null) {
                CompoundTag tag = new CompoundTag();
                value.serialize(SkinWardrobeStorage.writer(entity.get(), tag));
                return tag;
            }
            return null;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
            if (value != null) {
                value.deserialize(SkinWardrobeStorage.reader(entity.get(), tag));
            }
        }

        public T getValue() {
            return value;
        }
    }
}
