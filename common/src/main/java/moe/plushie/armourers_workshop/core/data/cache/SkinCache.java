package moe.plushie.armourers_workshop.core.data.cache;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderObjectBuilder;
import moe.plushie.armourers_workshop.core.skin.Skin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class SkinCache {

    public static SkinCache INSTANCE = new SkinCache();
    public Map<Skin, SkinRenderObjectBuilder> bufferBuilders = new HashMap<>();

    public void cache(Skin key, SkinRenderObjectBuilder buffer) {
        bufferBuilders.put(key, buffer);
    }

    @Nullable
    public SkinRenderObjectBuilder cache(Skin key) {
        return bufferBuilders.get(key);
    }

    public void clear() {
        bufferBuilders.clear();
    }

    public static class Key {

        private int hash;
        private Object[] objects;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key that)) return false;
            return hash == that.hash && Arrays.equals(objects, that.objects);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    public static class LRU<K, V> {

        private final Cache<K, V> cache;

        public LRU() {
            cache = CacheBuilder.newBuilder()
                    .expireAfterAccess(15, TimeUnit.SECONDS)
                    .build();
        }

        public LRU(int size) {
            cache = CacheBuilder.newBuilder()
                    .maximumSize(size)
                    .expireAfterAccess(15, TimeUnit.SECONDS)
                    .build();
        }

        public V computeIfAbsent(K key, Function<K, V> loader) {
            try {
                return cache.get(key, () -> loader.apply(key));
            } catch (ExecutionException e) {
                return null;
            }
        }

        public void clear() {
            cache.cleanUp();
        }
    }
}
