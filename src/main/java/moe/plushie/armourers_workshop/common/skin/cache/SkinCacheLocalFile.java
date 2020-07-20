package moe.plushie.armourers_workshop.common.skin.cache;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.api.common.library.ILibraryFile;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.common.data.type.BidirectionalHashMap;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;

public class SkinCacheLocalFile {
    
    /** Cache of skins that are in memory. */
    private final BidirectionalHashMap<ILibraryFile, Integer> cacheMapFileLink;
    private final Object cacheMapLock = new Object();
    
    /** A list of skin that need to be loaded. */
    private final Queue<SkinRequestMessage> skinLoadQueue;
    private final Object skinLoadQueueLock = new Object();
    
    private final SkinCacheLocalDatabase cacheLocalDatabase;
    
    public SkinCacheLocalFile(SkinCacheLocalDatabase cacheLocalDatabase) {
        this.cacheLocalDatabase = cacheLocalDatabase;
        cacheMapFileLink = new BidirectionalHashMap<ILibraryFile, Integer>();
        skinLoadQueue = new LinkedList<SkinRequestMessage>();
    }
    
    public void doSkinLoading() {
        synchronized (cacheMapLock) {
            synchronized (skinLoadQueueLock) {
                if (!skinLoadQueue.isEmpty()) {
                    SkinRequestMessage requestMessage = skinLoadQueue.remove();
                    Skin skin = null;
                    try {
                        skin = load(requestMessage.getSkinIdentifier());
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
    }
    
    public Skin get(ISkinIdentifier identifier, boolean softLoad) {
        return get(new SkinRequestMessage(identifier, null), softLoad);
    }

    public Skin get(SkinRequestMessage requestMessage, boolean softLoad) {
        ISkinIdentifier identifier = requestMessage.getSkinIdentifier();
        ILibraryFile libraryFile = identifier.getSkinLibraryFile();
        synchronized (cacheMapLock) {
            if (!cacheMapFileLink.containsKey(libraryFile)) {
                if (softLoad) {
                    synchronized (skinLoadQueueLock) {
                        skinLoadQueue.add(requestMessage);
                    }
                    return null;
                } else {
                    load(identifier);
                }
            }
            
            if (cacheMapFileLink.containsKey(libraryFile)) {
                int id = cacheMapFileLink.get(libraryFile);
                SkinIdentifier newIdentifier = new SkinIdentifier(id, requestMessage.getSkinIdentifier().getSkinLibraryFile(), 0, requestMessage.getSkinIdentifier().getSkinType());
                Skin skin = cacheLocalDatabase.get(newIdentifier, false);
                if (skin != null) {
                    return skin;
                } else {
                    ModLogger.log(Level.WARN, "Somehow failed to load a skin that we should have. ID was " + id);
                }
            } else {
                if (requestMessage.getPlayer() != null) {
                    ModLogger.log(Level.ERROR, "Skin [" + libraryFile.getFullName() + "] was requested by " + requestMessage.getPlayer().getName() + " but was not found.");
                } else {
                    ModLogger.log(Level.ERROR, "Skin [" + libraryFile.getFullName() + "] was requested but was not found.");
                }
            }
        }
        return null;
    }
    
    private Skin load(ISkinIdentifier skinIdentifier) {
        Skin skin = SkinIOUtils.loadSkinFromFileName(skinIdentifier.getSkinLibraryFile().getFullName() + SkinIOUtils.SKIN_FILE_EXTENSION);
        addSkinToCache(skin, skinIdentifier.getSkinLibraryFile());
        return skin;
    }
    
    private void addSkinToCache(Skin skin, ILibraryFile libraryFile) {
        if (skin == null) {
            return;
        }
        cacheLocalDatabase.add(skin);
        cacheMapFileLink.put(libraryFile, skin.lightHash());
    }
    
    public void add(LibraryFile libraryFile, int skinId) {
        synchronized (cacheMapLock) {
            cacheMapFileLink.put(libraryFile, skinId);
        }
    }
    
    public boolean containsValue(int skinId) {
        synchronized (cacheMapLock) {
            return cacheMapFileLink.containsValue(skinId);
        }
    }

    public ILibraryFile getBackward(int skinId) {
        synchronized (cacheMapLock) {
            if (cacheMapFileLink.getMapBackward().containsKey(skinId)) {
                return cacheMapFileLink.getBackward(skinId);
            } else {
                return null;
            }
        }
    }
    
    public void remove(ILibraryFile libraryFile) {
        synchronized (cacheMapLock) {
            cacheMapFileLink.remove(libraryFile);
        }
    }
    
    public int size() {
        synchronized (cacheMapLock) {
            return cacheMapFileLink.size();
        }
    }
    
    public void clear() {
        synchronized (cacheMapLock) {
            cacheMapFileLink.clear();
        }
    }
}
