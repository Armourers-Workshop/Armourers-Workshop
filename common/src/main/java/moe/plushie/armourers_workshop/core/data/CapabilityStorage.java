package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.ICapabilityType;
import moe.plushie.armourers_workshop.api.data.IDataSerializerProvider;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractCapabilityStorage;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

    public static <T> void registerCapability(ResourceLocation registryName, ICapabilityType<T> capabilityType, Function<Entity, Optional<T>> provider) {
        ENTRIES.add(new Entry<>(registryName, capabilityType, provider));
    }

    public static CapabilityStorage attachCapability(Entity entity) {
        if (ENTRIES.isEmpty()) {
            return NONE;
        }
        IdentityHashMap<ICapabilityType<?>, Pair<Entry<?>, Optional<?>>> capabilities = new IdentityHashMap<>();
        for (Entry<?> entry : ENTRIES) {
            Optional<?> cap = entry.provider.apply(entity);
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
        CapabilityStorage storage = ((Provider) entity).getCapabilityStorage();
        Pair<Entry<?>, Optional<?>> value = storage.capabilities.get(capabilityType);
        if (value != null) {
            return ObjectUtils.unsafeCast(value.getValue());
        }
        return Optional.empty();
    }

    public void save(Entity entity, CompoundTag tag) {
        if (this == NONE) {
            return;
        }
        String capsKey = AbstractCapabilityStorage.KEY;
        CompoundTag caps = tag.getCompound(capsKey);
        capabilities.values().forEach(pair -> {
            IDataSerializerProvider provider = ObjectUtils.safeCast(pair.getValue().orElse(null), IDataSerializerProvider.class);
            if (provider != null) {
                CompoundTag tag1 = new CompoundTag();
                provider.serialize(AbstractDataSerializer.wrap(tag1, entity));
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
            IDataSerializerProvider provider = ObjectUtils.safeCast(pair.getValue().orElse(null), IDataSerializerProvider.class);
            if (provider != null) {
                CompoundTag tag1 = ObjectUtils.safeCast(caps.get(pair.getKey().registryName.toString()), CompoundTag.class);
                if (tag1 != null) {
                    provider.deserialize(AbstractDataSerializer.wrap(tag1, entity));
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
        ResourceLocation registryName;
        ICapabilityType<T> capabilityType;
        Function<Entity, Optional<T>> provider;

        Entry(ResourceLocation registryName, ICapabilityType<T> capabilityType, Function<Entity, Optional<T>> provider) {
            this.registryName = registryName;
            this.capabilityType = capabilityType;
            this.provider = provider;
        }
    }
}
