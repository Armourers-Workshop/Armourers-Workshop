package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractCapabilityStorage;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeStorage;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.Objects;
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

    CapabilityStorage(IdentityHashMap<IEntityCapability<?>, Pair<Entry<?>, Optional<?>>> capabilities) {
        this.capabilities = capabilities;
    }

    public static <T> void registerCapability(IResourceLocation registryName, IEntityCapability<T> capabilityType, Function<Entity, Optional<T>> provider) {
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

    public void save(Entity entity, CompoundTag tag) {
        if (this == NONE) {
            return;
        }
        var capsKey = AbstractCapabilityStorage.KEY;
        var caps = tag.getOptionalCompound(capsKey).orElseGet(CompoundTag::new);
        capabilities.values().forEach(pair -> {
            if (pair.getValue().orElse(null) instanceof IDataSerializable.Mutable provider) {
                var tag1 = new CompoundTag();
                provider.serialize(SkinWardrobeStorage.encoder(entity, tag1));
                caps.put(pair.getKey().registryName.toString(), tag1);
            }
        });
        if (!caps.isEmpty()) {
            tag.put(capsKey, caps);
        } else {
            tag.remove(capsKey);
        }
    }

    public void load(Entity entity, CompoundTag tag) {
        if (this == NONE) {
            return;
        }
        var caps = getCapTag(tag);
        if (caps == null || caps.isEmpty()) {
            return;
        }
        capabilities.values().forEach(pair -> {
            if (pair.getValue().orElse(null) instanceof IDataSerializable.Mutable provider) {
                var containerTag = caps.get(pair.getKey().registryName.toString());
                if (containerTag instanceof CompoundTag compoundTag) {
                    provider.deserialize(SkinWardrobeStorage.decoder(entity, compoundTag));
                }
            }
        });
    }

    @Nullable
    private CompoundTag getCapTag(CompoundTag tag) {
        var newValue = tag.getOptionalCompound(Constants.Key.NEW_CAPABILITY);
        var oldValue = tag.getOptionalCompound(Constants.Key.OLD_CAPABILITY).orElse(null);
        if (oldValue != null) {
            return newValue.map(newCaps -> oldValue.copy().merge(newCaps)).orElse(oldValue);
        }
        return newValue.orElse(null);
    }

    public interface Provider {
        CapabilityStorage getCapabilityStorage();
    }

    private static class Entry<T> {
        IResourceLocation registryName;
        IEntityCapability<T> capabilityType;
        Function<Entity, Optional<T>> provider;

        Entry(IResourceLocation registryName, IEntityCapability<T> capabilityType, Function<Entity, Optional<T>> provider) {
            this.registryName = registryName;
            this.capabilityType = capabilityType;
            this.provider = provider;
        }
    }
}
