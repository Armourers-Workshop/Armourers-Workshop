package moe.plushie.armourers_workshop.common.skin.cache;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.data.type.ExpiringHashMap.IExpiringMapCallback;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SkinCacheLocalDatabase implements RemovalListener<Integer, Skin> {
    
    /** Cache of skins that are in memory. */
    private final LoadingCache<Integer, Skin> skinCache;
    
    /** A list of skin that need to be loaded. */
    private final Queue<SkinRequestMessage> skinLoadQueue;
    private final Object skinLoadQueueLock = new Object();
    
    private final IExpiringMapCallback<Skin> callback;
    
    public SkinCacheLocalDatabase(IExpiringMapCallback<Skin> callback) {
        this.callback = callback;
        CacheBuilder builder = CacheBuilder.newBuilder();
        builder.removalListener(this);
        if (ConfigHandler.skinCacheExpireTime > 1) {
            builder.expireAfterAccess(ConfigHandler.skinCacheExpireTime, TimeUnit.SECONDS);
        }
        if (ConfigHandler.skinCacheMaxSize > 1) {
            builder.maximumSize(ConfigHandler.skinCacheMaxSize);
        }
        skinCache = builder.build(new SkinLoader());
        skinLoadQueue = new LinkedList<SkinRequestMessage>();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void doSkinLoading() {
        synchronized (skinLoadQueueLock) {
            if (!skinLoadQueue.isEmpty()) {
                SkinRequestMessage requestMessage = skinLoadQueue.remove();
                Skin skin = null;
                try {
                    skin = skinCache.get(requestMessage.getSkinIdentifier().getSkinLocalId());
                } catch (Exception e) {
                    CommonSkinCache.INSTANCE.onSkinLoaded(null, requestMessage);
                    e.printStackTrace();
                }
                if (skin != null) {
                    CommonSkinCache.INSTANCE.onSkinLoaded(skin, requestMessage);
                }
            }
        }
    }
    
    public Skin get(ISkinIdentifier identifier, boolean softLoad) {
        return get(new SkinRequestMessage(identifier, null), softLoad);
    }
    
    public Skin get(SkinRequestMessage requestMessage, boolean softLoad) {
        int skinId = requestMessage.getSkinIdentifier().getSkinLocalId();
        if (!softLoad) {
            try {
                return skinCache.get(skinId);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Skin skin = skinCache.getIfPresent(skinId);
            if (skin != null) {
                return skin;
            } else {
                synchronized (skinLoadQueueLock) {
                    skinLoadQueue.add(requestMessage);
                }
            }
        }
        return null;
    }
    
    public void add(Skin skin) {
        addSkinDataToCache(skin, skin.lightHash());
    }
    
    public int size() {
        return skinCache.asMap().size();
    }
    
    public void clear() {
        skinCache.invalidateAll();
    }
    
    @Override
    public void onRemoval(RemovalNotification<Integer, Skin> notification) {
        if (callback != null) {
            callback.itemExpired(notification.getValue());
        }
    }
    
    private class SkinLoader extends CacheLoader<Integer, Skin> {

        @Override
        public Skin load(Integer key) throws Exception {
            return loadSkin(key);
        }
        
        private Skin loadSkin(int skinId) throws IOException {
            if (haveSkinOnDisk(skinId)) {
                Skin skin;
                skin = loadSkinFromDisk(skinId);
                if (skin != null) {
                    addSkinDataToCache(skin, skinId);
                    if (skin.hashCode() != skinId) {
                        addSkinDataToCache(skin, skin.hashCode());
                    }
                    return skin;
                } else {
                    throw new IOException(String.format("Failed to load skin id:%s from disk.", String.valueOf(skinId)));
                }
            } else {
                throw new IOException(String.format("The skin id:%s was not found on the disk.", String.valueOf(skinId)));
            }
        }
    }
    
    private void addSkinDataToCache(Skin skin, int skinId) {
        if (skin == null) {
            return;
        }
        if (!haveSkinOnDisk(skinId)) {
            saveSkinToDisk(skin);
        }
        skinCache.asMap().putIfAbsent(skinId, skin);
    }
    
    private void saveSkinToDisk(Skin skin) {
        File file = new File(SkinIOUtils.getSkinDatabaseDirectory(), String.valueOf(skin.hashCode()));
        SkinIOUtils.saveSkinToFile(file, skin);
    }
    
    private boolean haveSkinOnDisk(int equipmentId) {
        File file = new File(SkinIOUtils.getSkinDatabaseDirectory(), String.valueOf(equipmentId));
        return file.exists();
    }
    
    private Skin loadSkinFromDisk(int equipmentId) {
        File file = new File(SkinIOUtils.getSkinDatabaseDirectory(), String.valueOf(equipmentId));
        return SkinIOUtils.loadSkinFromFile(file);
    }
}
