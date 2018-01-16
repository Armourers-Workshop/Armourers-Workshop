package riskyken.armourersWorkshop.common.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import scala.NotImplementedError;

public class BidirectionalHashMap<K, V> implements Map<K, V> {

    private final HashMap<K, V> mapForward;
    private final HashMap<V, K> mapBackward;
    
    public BidirectionalHashMap() {
        mapForward = new HashMap<K, V>();
        mapBackward = new HashMap<V, K>();
    }
    
    @Override
    public void clear() {
        mapForward.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return mapForward.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return mapForward.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return mapForward.entrySet();
    }

    @Override
    public V get(Object key) {
        return mapForward.get(key);
    }
    
    public K getBackward(V key) {
        return mapBackward.get(key);
    }

    @Override
    public boolean isEmpty() {
        return mapForward.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return mapForward.keySet();
    }

    @Override
    public V put(K key, V value) {
        mapBackward.put(value, key);
        return mapForward.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new NotImplementedError();
    }

    @Override
    public V remove(Object key) {
        V value = mapForward.get(key);
        if (value != null) {
            mapBackward.remove(value);
        }
        return mapForward.remove(key);
    }

    @Override
    public int size() {
        return mapForward.size();
    }

    @Override
    public Collection<V> values() {
        return mapForward.values();
    }
}
