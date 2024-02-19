package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.ICapabilityType;
import moe.plushie.armourers_workshop.api.common.ITagRepresentable;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
        CompoundTag caps = tag.getCompound(Constants.Key.OLD_CAPABILITY);
        capabilities.values().forEach(pair -> {
            Object value = pair.getValue().orElse(null);
            if (value instanceof ITagRepresentable) {
                ITagRepresentable<Tag> value1 = ObjectUtils.unsafeCast(value);
                Tag tag1 = value1.serializeNBT();
                if (tag1 != null) {
                    caps.put(pair.getKey().registryName.toString(), tag1);
                }
            }
        });
        if (!caps.isEmpty()) {
            tag.put(Constants.Key.OLD_CAPABILITY, caps);
        } else {
            tag.remove(Constants.Key.OLD_CAPABILITY);
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
            Object value = pair.getValue().orElse(null);
            if (value instanceof ITagRepresentable) {
                Tag tag1 = caps.get(pair.getKey().registryName.toString());
                if (tag1 != null) {
                    ITagRepresentable<Tag> value1 = ObjectUtils.unsafeCast(value);
                    value1.deserializeNBT(tag1);
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
