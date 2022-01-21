package moe.plushie.armourers_workshop.core.cache;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.core.render.SkinRenderBuffer;
import moe.plushie.armourers_workshop.core.render.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.skin.data.Skin;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class SkinCache {

    public static SkinCache INSTANCE = new SkinCache();
    public Map<Skin, SkinVertexBufferBuilder> bufferBuilders = new HashMap<>();


    public void cache(Skin key, SkinVertexBufferBuilder buffer) {
        bufferBuilders.put(key, buffer);
    }

    @Nullable
    public SkinVertexBufferBuilder cache(Skin key) {
        return bufferBuilders.get(key);
    }

    public void clear() {
        bufferBuilders.clear();
        SkinRenderBuffer.getInstance().clear();
    }


    public static class Key {

        private final int hash;
        private final Object[] objects;

        public Key(Object... objects) {
            this.objects = objects;
            this.hash = Objects.hash(objects);
        }

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
