package riskyken.armourersWorkshop.common.data;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpiringHashMap<K, V> implements Runnable {
    
    private final HashMap<K, CacheMapObject> cacheMap;
    private int expiryTime; 
    private final IExpiringMapCallback callback;
    private final ArrayList<V> cleanupList;
    private volatile Thread cleanupThread;
    
    public ExpiringHashMap(int expiryTime) {
        this(expiryTime, null);
    }
    
    public ExpiringHashMap(int expiryTime, IExpiringMapCallback callback) {
        cacheMap = new HashMap<K, CacheMapObject>();
        this.expiryTime = expiryTime;
        this.callback = callback;
        cleanupList = new ArrayList<V>();
        cleanupThread = new Thread(this);
        cleanupThread.start();
    }
    
    public void setExpiryTime(int expiryTime) {
        this.expiryTime = expiryTime;
    }
    
    @Override
    protected void finalize() throws Throwable {
        cleanupThread = null;
        super.finalize();
    }
    
    public void put(K key, V value) {
        synchronized (cacheMap) {
            cacheMap.put(key, new CacheMapObject(value));
        }
    }
    
    public V get(K key) {
        synchronized (cacheMap) {
            if (cacheMap.containsKey(key)) {
                return cacheMap.get(key).getMapItem();
            }
        }
        return null;
    }
    
    public boolean containsKey(K key) {
        synchronized (cacheMap) {
            return cacheMap.containsKey(key);
        }
    }
    
    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }
    
    public void clear() {
        synchronized (cacheMap) {
            cacheMap.clear();
        }
    }
    
    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        while (cleanupThread == thisThread) {
            try {
                thisThread.sleep(1000);
                cleanup();
            } catch (InterruptedException e) {
                
            }
        }
    }
    
    public void cleanupCheck() {
        synchronized (cleanupList) {
            for (int i = 0; i < cleanupList.size(); i++) {
                callback.itemExpired(cleanupList.get(i));
            }
            cleanupList.clear();
        }
    }
    
    private void cleanup() {
        synchronized (cacheMap) {
            Object[] keySet = cacheMap.keySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                Object key = keySet[i];
                CacheMapObject mapObject = cacheMap.get(key);
                if (mapObject.lastAccessed + (long)expiryTime < System.currentTimeMillis()) {
                    cacheMap.remove(key);
                }
                if (callback != null) {
                    synchronized (cleanupList) {
                        cleanupList.add(mapObject.mapItem);
                    }
                }
            }
        }
    }
    
    protected class CacheMapObject {
        
        private final V mapItem;
        private long lastAccessed;
        
        public CacheMapObject(V mapItem) {
            this.mapItem = mapItem;
            lastAccessed = System.currentTimeMillis();
        }
        
        public V getMapItem() {
            lastAccessed = System.currentTimeMillis();
            return mapItem;
        }
        
        @Override
        public boolean equals(Object obj) {
            return mapItem.equals(obj);
        }
        
        @Override
        public int hashCode() {
            return mapItem.hashCode();
        }
        
        @Override
        public String toString() {
            return mapItem.toString();
        }
    }
    
    public interface IExpiringMapCallback<V> {
        public void itemExpired(V mapItem);
    }
}
