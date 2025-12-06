package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.compat.core.data.AbstractCapabilityStorage;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeStorage;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Optional;
import java.util.function.Function;

public class CapabilityStorage {

    private static final ArrayList<Entry<?>> ENTRIES = new ArrayList<>();
    private static final CapabilityStorage NONE = new CapabilityStorage(new IdentityHashMap<>());

    private final IdentityHashMap<IEntityCapability<?>, Pair<Entry<?>, Optional<?>>> capabilities;

    private CapabilityStorage(IdentityHashMap<IEntityCapability<?>, Pair<Entry<?>, Optional<?>>> capabilities) {
        this.capabilities = capabilities;
    }

    public static <T> void registerCapability(OpenResourceLocation registryName, IEntityCapability<T> capabilityType, Function<Entity, Optional<T>> provider) {
        ENTRIES.add(new Entry<>(registryName, capabilityType, provider));
    }

    public static CapabilityStorage attachCapability(Entity entity) {
        if (ENTRIES.isEmpty()) {
            return NONE;
        }
        var capabilities = new IdentityHashMap<IEntityCapability<?>, Pair<Entry<?>, Optional<?>>>();
        for (var entry : ENTRIES) {
            var cap = entry.provider.apply(entity);
            if (cap.isPresent()) {
                capabilities.put(entry.capabilityType, Pair.of(entry, cap));
            }
        }
        if (capabilities.isEmpty()) {
            return NONE;
        }
        return new CapabilityStorage(capabilities);
    }

    public static <T> Optional<T> getCapability(Entity entity, IEntityCapability<T> capabilityType) {
        var storage = ((Provider) entity).getCapabilityStorage();
        var value = storage.capabilities.get(capabilityType);
        if (value != null) {
            return Objects.unsafeCast(value.getValue());
        }
        return Optional.empty();
    }

    public void save(Entity entity, IDataSerializer serializer) {
        if (this == NONE) {
            return;
        }
        var caps = new CompoundTag();
        capabilities.values().forEach(pair -> {
            if (pair.getValue().orElse(null) instanceof IDataSerializable.Mutable provider) {
                var serializer1 = new TagSerializer(SerializationContext.from(entity));
                provider.serialize(SkinWardrobeStorage.encoder(entity, serializer1));
                caps.put(pair.getKey().registryName.toString(), serializer1.tag());
            }
        });
        serializer.write(CodingKeys.ACTIVATED_CAPABILITY, caps);
    }

    public void load(Entity entity, IDataSerializer serializer) {
        if (this == NONE) {
            return;
        }
        var caps = getCapTag(serializer);
        if (caps == null || caps.isEmpty()) {
            return;
        }
        capabilities.values().forEach(pair -> {
            if (pair.getValue().orElse(null) instanceof IDataSerializable.Mutable provider) {
                var containerTag = caps.get(pair.getKey().registryName.toString());
                if (containerTag instanceof CompoundTag compoundTag) {
                    var serializer1 = new TagSerializer(compoundTag, SerializationContext.from(entity));
                    provider.deserialize(SkinWardrobeStorage.decoder(entity, serializer1));
                }
            }
        });
    }

    @Nullable
    private CompoundTag getCapTag(IDataSerializer serializer) {
        var newValue = serializer.read(CodingKeys.NEW_CAPABILITY);
        var oldValue = serializer.read(CodingKeys.OLD_CAPABILITY);
        if (oldValue != null) {
            if (newValue != null) {
                return oldValue.copy().merge(newValue);
            }
            return oldValue;
        }
        return newValue;
    }

    public interface Provider {
        CapabilityStorage getCapabilityStorage();
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<CompoundTag> NEW_CAPABILITY = IDataSerializerKey.create(Constants.Key.NEW_CAPABILITY, ExtraCodecs.COMPOUND_TAG);
        public static final IDataSerializerKey<CompoundTag> OLD_CAPABILITY = IDataSerializerKey.create(Constants.Key.OLD_CAPABILITY, ExtraCodecs.COMPOUND_TAG);

        public static final IDataSerializerKey<CompoundTag> ACTIVATED_CAPABILITY = IDataSerializerKey.create(AbstractCapabilityStorage.KEY, ExtraCodecs.COMPOUND_TAG);
    }

    private static class Entry<T> {

        private final OpenResourceLocation registryName;
        private final IEntityCapability<T> capabilityType;
        private final Function<Entity, Optional<T>> provider;

        public Entry(OpenResourceLocation registryName, IEntityCapability<T> capabilityType, Function<Entity, Optional<T>> provider) {
            this.registryName = registryName;
            this.capabilityType = capabilityType;
            this.provider = provider;
        }
    }
}
