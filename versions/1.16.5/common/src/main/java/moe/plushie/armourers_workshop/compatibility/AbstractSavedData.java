package moe.plushie.armourers_workshop.compatibility;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.lang.ref.WeakReference;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractSavedData {

    protected WeakReference<Storage<?>> storage;

    public static <T extends AbstractSavedData> T load(Function<CompoundTag, T> parser, Supplier<T> provider, DimensionDataStorage storage, String name) {
        Storage<T> storage1 = storage.computeIfAbsent(() -> new Storage<>(parser, provider, name), name);
        T value = storage1.get();
        value.storage = new WeakReference<>(storage1);
        return value;
    }
    public void setDirty() {
        Storage<?> storage = storage();
        if (storage != null) {
            storage.setDirty();
        }
    }

    public CompoundTag save(CompoundTag tag) {
        return tag;
    }

    private Storage<?> storage() {
        if (storage != null) {
            return storage.get();
        }
        return null;
    }

    public static class Storage<T extends AbstractSavedData> extends SavedData {
        Function<CompoundTag, T> parser;
        Supplier<T> provider;
        T value;
        public Storage(Function<CompoundTag, T> parser, Supplier<T> provider, String name) {
            super(name);
            this.parser = parser;
            this.provider = provider;
        }

        @Override
        public void load(CompoundTag tag) {
            value = parser.apply(tag);
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            return get().save(tag);
        }

        public T get() {
            if (value == null) {
                value = provider.get();
            }
            return value;
        }
    }
}
