package moe.plushie.armourers_workshop.core.cache;


import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderTypeBuffer;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SkinCache {

    public static SkinCache INSTANCE = new SkinCache();
    public Map<Key, SkinRenderTypeBuffer> renderBuffers = new HashMap<>();

    public void cache(Key key, SkinRenderTypeBuffer buffer) {
        renderBuffers.put(key, buffer);
    }

    @Nullable
    public SkinRenderTypeBuffer cache(Key key) {
        return renderBuffers.get(key);
    }

    public void clear() {
        renderBuffers.clear();
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
}
