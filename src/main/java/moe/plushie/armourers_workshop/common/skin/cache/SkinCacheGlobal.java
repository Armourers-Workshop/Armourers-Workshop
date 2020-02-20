 package moe.plushie.armourers_workshop.common.skin.cache;

import java.util.HashSet;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.common.data.type.BidirectionalHashMap;
import moe.plushie.armourers_workshop.common.library.global.SkinDownloader.DownloadSkinCallable;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.utils.ModLogger;

public class SkinCacheGlobal {

    /** Cache of skins that are in memory. */
    private final BidirectionalHashMap<Integer, Integer> cacheMapFileLink;
    private final Object cacheMapLock = new Object();
    
    private final SkinCacheLocalDatabase cacheLocalDatabase;
    
    private final HashSet<Integer> downloadingSet;
    private final Object downloadingSetLock = new Object();
    
    private final Executor executorSkinDownloader;
    private final CompletionService<Skin> completionServiceSkinDownloader;
    
    public  SkinCacheGlobal(SkinCacheLocalDatabase cacheLocalDatabase) {
        this.cacheLocalDatabase = cacheLocalDatabase;
        cacheMapFileLink = new BidirectionalHashMap<Integer, Integer>();
        
        downloadingSet = new HashSet<Integer>();
        executorSkinDownloader= Executors.newFixedThreadPool(2);
        completionServiceSkinDownloader = new ExecutorCompletionService<Skin>(executorSkinDownloader);
    }
    
    private void downloadSkin(ISkinIdentifier identifier) {
        synchronized (downloadingSet) {
            if (!downloadingSet.contains(identifier.getSkinGlobalId())) {
                downloadingSet.add(identifier.getSkinGlobalId());
                completionServiceSkinDownloader.submit(new DownloadSkinCallable(null, identifier.getSkinGlobalId()));
            }
        }
    }
    
    public void doSkinLoading() {
        Future<Skin> futureSkin = completionServiceSkinDownloader.poll();
        if (futureSkin!= null) {
            try {
                Skin skin = futureSkin.get();
                if (skin != null) {
                    synchronized (cacheMapLock) {
                        int globalId = skin.serverId;
                        cacheLocalDatabase.add(skin);
                        cacheMapFileLink.put(globalId, skin.lightHash());
                        CommonSkinCache.INSTANCE.onGlobalSkinLoaded(skin, globalId);
                        synchronized (downloadingSetLock) {
                            downloadingSet.remove(skin.serverId);
                        }
                    }
                } else {
                    ModLogger.log(Level.ERROR, "Failed to load skin from global database.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public Skin get(ISkinIdentifier identifier, boolean softLoad) {
        return get(new SkinRequestMessage(identifier, null), softLoad);
    }

    public Skin get(SkinRequestMessage requestMessage, boolean softLoad) {
        ISkinIdentifier identifier = requestMessage.getSkinIdentifier();
        int globalId = identifier.getSkinGlobalId();
        synchronized (cacheMapLock) {
            if (!cacheMapFileLink.containsKey(globalId)) {
                downloadSkin(identifier);
                return null;
            }
            if (cacheMapFileLink.containsKey(globalId)) {
                int id = cacheMapFileLink.get(globalId);
                SkinIdentifier newIdentifier = new SkinIdentifier(id, null, globalId, identifier.getSkinType());
                Skin skin = cacheLocalDatabase.get(newIdentifier, false);
                if (skin != null) {
                    return skin;
                } else {
                    ModLogger.log(Level.WARN, "Somehow failed to load a skin that we should have. ID was " + id);
                }
            } else {
                if (requestMessage.getPlayer() != null) {
                    ModLogger.log(Level.ERROR, "Skin [" + identifier.toString() + "] was requested by " + requestMessage.getPlayer().getName() + " but was not found.");
                } else {
                    ModLogger.log(Level.ERROR, "Skin [" + identifier.toString() + "] was requested but was not found.");
                }
            }
        }
        return null;
    }
    
    public boolean containsValue(int skinId) {
        synchronized (cacheMapLock) {
            return cacheMapFileLink.containsValue(skinId);
        }
    }
    
    public int getBackward(int skinId) {
        synchronized (cacheMapLock) {
            if (cacheMapFileLink.getMapBackward().containsKey(skinId)) {
                return cacheMapFileLink.getBackward(skinId);
            } else {
                return 0;
            }
        }
    }
    
    public void remove(int globalId) {
        synchronized (cacheMapLock) {
            cacheMapFileLink.remove(globalId);
        }
    }
    
    public int size() {
        synchronized (cacheMapLock) {
            return cacheMapFileLink.size();
        }
    }
    
    public void clear() {
        synchronized (cacheMapLock) {
            synchronized (downloadingSetLock) {
                cacheMapFileLink.clear();
                downloadingSet.clear();
            }
        }
    }
}
