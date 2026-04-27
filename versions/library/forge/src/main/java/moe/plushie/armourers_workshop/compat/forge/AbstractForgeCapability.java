package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeStorage;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Available("[21, 26)")
public class AbstractForgeCapability<T> implements IEntityCapability<T> {

    private static final TypedProvider<AttachmentType<?>> REGISTRY = AbstractForgeRegistry.from(NeoForgeRegistries.ATTACHMENT_TYPES);

    private final Function<Entity, Optional<T>> factory;
    private final EntityCapability<T, Void> capability;
    private final Supplier<AttachmentType<Serializer<T>>> attachmentType;

    public AbstractForgeCapability(OpenResourceKey registryName, Class<T> type, Function<Entity, Optional<T>> factory) {
        this.factory = factory;
        this.capability = EntityCapability.createVoid(registryName.get(), type);
        this.attachmentType = REGISTRY.register(registryName, key -> AttachmentType.serializable(this::create).build());
        // register into all entity.
        AbstractForgeEventBus.observer(RegisterCapabilitiesEvent.class, event -> {
            for (var entityType : BuiltInRegistries.ENTITY_TYPE) {
                event.registerEntity(capability, entityType, (entity, context) -> entity.getData(attachmentType).value());
            }
        });
    }

    @Override
    public Optional<T> get(Entity entity) {
        return Optional.ofNullable(entity.getCapability(capability));
    }

    private Serializer<T> create(IAttachmentHolder holder) {
        var value = factory.apply((Entity) holder).orElse(null);
        return new Serializer<>((Entity) holder, value);
    }

    private static class Serializer<T> implements INBTSerializable<CompoundTag> {

        protected final T value;
        protected final WeakReference<Entity> entity;

        protected Serializer(Entity entity, T value) {
            this.value = value;
            this.entity = new WeakReference<>(entity);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            if (value instanceof IDataSerializable.Immutable serializable) {
                var serializer = new TagSerializer(SerializationContext.from(provider));
                serializable.serialize(SkinWardrobeStorage.encoder(entity.get(), serializer));
                return serializer.tag();
            }
            return null;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
            if (value instanceof IDataSerializable.Mutable serializable) {
                var serializer = new TagSerializer(tag, SerializationContext.from(provider));
                serializable.deserialize(SkinWardrobeStorage.decoder(entity.get(), serializer));
            }
        }

        public T value() {
            return value;
        }
    }
}
