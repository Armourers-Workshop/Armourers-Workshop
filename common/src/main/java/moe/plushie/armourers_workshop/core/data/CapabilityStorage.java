package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.ICapabilityType;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IDataSerializerProvider;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractCapabilityStorage;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeStorage;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Optional;
import java.util.function.Function;

public class CapabilityStorage {

    private static final ArrayList<Entry<?>> ENTRIES = new ArrayList<>();
    private static final CapabilityStorage NONE = new CapabilityStorage(new IdentityHashMap<>());

    private final IdentityHashMap<ICapabilityType<?>, Pair<Entry<?>, Optional<?>>> capabilities;

    CapabilityStorage(IdentityHashMap<ICapabilityType<?>, Pair<Entry<?>, Optional<?>>> capabilities) {
        this.capabilities = capabilities;
    }

    public static <T> void registerCapability(IResourceLocation registryName, ICapabilityType<T> capabilityType, Function<Entity, Optional<T>> provider) {
        ENTRIES.add(new Entry<>(registryName, capabilityType, provider));
    }

    public static CapabilityStorage attachCapability(Entity entity) {
        if (ENTRIES.isEmpty()) {
            return NONE;
        }
        var capabilities = new IdentityHashMap<ICapabilityType<?>, Pair<Entry<?>, Optional<?>>>();
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

    public static <T> Optional<T> getCapability(Entity entity, ICapabilityType<T> capabilityType) {
        var storage = ((Provider) entity).getCapabilityStorage();
        var value = storage.capabilities.get(capabilityType);
        if (value != null) {
            return ObjectUtils.unsafeCast(value.getValue());
        }
        return Optional.empty();
    }

    public void save(Entity entity, CompoundTag tag) {
        if (this == NONE) {
            return;
        }
        var capsKey = AbstractCapabilityStorage.KEY;
        var caps = tag.getCompound(capsKey);
        capabilities.values().forEach(pair -> {
            if (pair.getValue().orElse(null) instanceof IDataSerializerProvider provider) {
                var tag1 = new CompoundTag();
                provider.serialize(SkinWardrobeStorage.writer(entity, tag1));
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
        CompoundTag caps = getCapTag(tag);
        if (caps.isEmpty()) {
            return;
        }
        capabilities.values().forEach(pair -> {
            if (pair.getValue().orElse(null) instanceof IDataSerializerProvider provider) {
                var tag1 = ObjectUtils.safeCast(caps.get(pair.getKey().registryName.toString()), CompoundTag.class);
                if (tag1 != null) {
                    provider.deserialize(SkinWardrobeStorage.reader(entity, tag1));
                }
            }
        });
    }

    private CompoundTag getCapTag(CompoundTag tag) {
        if (tag.contains(Constants.Key.OLD_CAPABILITY, Constants.TagFlags.COMPOUND)) {
            CompoundTag caps = tag.getCompound(Constants.Key.OLD_CAPABILITY);
            if (tag.contains(Constants.Key.NEW_CAPABILITY, Constants.TagFlags.COMPOUND)) {
                caps = caps.copy();
                caps.merge(tag.getCompound(Constants.Key.NEW_CAPABILITY));
            }
            return caps;
        }
        return tag.getCompound(Constants.Key.NEW_CAPABILITY);
    }

    public interface Provider {
        CapabilityStorage getCapabilityStorage();
    }

    private static class Entry<T> {
        IResourceLocation registryName;
        ICapabilityType<T> capabilityType;
        Function<Entity, Optional<T>> provider;

        Entry(IResourceLocation registryName, ICapabilityType<T> capabilityType, Function<Entity, Optional<T>> provider) {
            this.registryName = registryName;
            this.capabilityType = capabilityType;
            this.provider = provider;
        }
    }
}
