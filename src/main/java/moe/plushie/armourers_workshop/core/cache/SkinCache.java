package moe.plushie.armourers_workshop.core.cache;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.skin.Skin;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class SkinCache {

    private final static ArrayDeque<Key> POOL = new ArrayDeque<>();

    public static SkinCache INSTANCE = new SkinCache();
    public Map<Skin, SkinVertexBufferBuilder> bufferBuilders = new HashMap<>();

    public static Object borrowKey(Object... objects) {
        Key key = POOL.poll();
        if (key == null) {
            key = new Key();
        }
        key.hash = Objects.hash(objects);
        key.objects = objects;
        return key;
    }

    public static void returnKey(Object key) {
        if (key instanceof Key) {
            ((Key) key).objects = null;
            POOL.push((Key) key);
        }
    }

    public void cache(Skin key, SkinVertexBufferBuilder buffer) {
        bufferBuilders.put(key, buffer);
    }

    @Nullable
    public SkinVertexBufferBuilder cache(Skin key) {
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
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return hash == key.hash && Arrays.equals(objects, key.objects);
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
