package riskyken.armourers_workshop.common.skin.cache;

import java.io.File;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;
import riskyken.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import riskyken.armourers_workshop.common.config.ConfigHandler;
import riskyken.armourers_workshop.common.data.ExpiringHashMap;
import riskyken.armourers_workshop.common.data.ExpiringHashMap.IExpiringMapCallback;
import riskyken.armourers_workshop.common.skin.data.Skin;
import riskyken.armourers_workshop.utils.ModLogger;
import riskyken.armourers_workshop.utils.SkinIOUtils;

public class SkinCacheLocalDatabase implements IExpiringMapCallback<Skin> {
    
    /** Cache of skins that are in memory. */
    private final ExpiringHashMap<Integer, Skin> cacheMapDatabase;
    private final Object cacheMapLock = new Object();
    
    /** A list of skin that need to be loaded. */
    private final ArrayList<SkinRequestMessage> skinLoadQueue;
    private final Object skinLoadQueueLock = new Object();
    
    private final IExpiringMapCallback<Skin> callback;
    
    public SkinCacheLocalDatabase(IExpiringMapCallback<Skin> callback) {
        this.callback = callback;
        cacheMapDatabase = new ExpiringHashMap<Integer, Skin>(ConfigHandler.serverModelCacheTime, this);
        skinLoadQueue = new ArrayList<SkinRequestMessage>();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void doSkinLoading() {
        synchronized (cacheMapLock) {
            synchronized (skinLoadQueueLock) {
                if (skinLoadQueue.size() > 0) {
                    SkinRequestMessage requestMessage = skinLoadQueue.get(0);
                    Skin skin = load(requestMessage.getSkinIdentifier().getSkinLocalId());
                    if (skin != null) {
                        CommonSkinCache.INSTANCE.onSkinLoaded(skin, requestMessage);
                    }
                    skinLoadQueue.remove(0);
                }
            }
        }
    }
    
    public Skin get(ISkinIdentifier identifier, boolean softLoad) {
        return get(new SkinRequestMessage(identifier, null), softLoad);
    }
    
    public Skin get(SkinRequestMessage requestMessage, boolean softLoad) {
        int skinId = requestMessage.getSkinIdentifier().getSkinLocalId();
        synchronized (cacheMapLock) {
            if (!cacheMapDatabase.containsKey(skinId)) {
                if (softLoad) {
                    synchronized (skinLoadQueueLock) {
                        skinLoadQueue.add(requestMessage);
                    }
                    return null;
                } else {
                    load(skinId);
                }
            }
            if (cacheMapDatabase.containsKey(skinId)) {
                return cacheMapDatabase.get(skinId);
            } else {
                if (requestMessage.getPlayer() != null) {
                    ModLogger.log(Level.ERROR, "Equipment id:" + String.valueOf(skinId) +" was requested by " + requestMessage.getPlayer().getName() + " but was not found.");
                } else {
                    ModLogger.log(Level.ERROR, "Equipment id:" + String.valueOf(skinId) +" was requested but was not found.");
                }
                return null;
            }
        }
    }
    
    private Skin load(int skinId) {
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
                ModLogger.log(Level.ERROR, String.format("Failed to load skin id:%s from disk.", String.valueOf(skinId)));
            }
        } else {
            ModLogger.log(Level.WARN, String.format("The skin id:%s was not found on the disk.", String.valueOf(skinId)));
        }
        return null;
    }
    
    public void add(Skin skin) {
        synchronized (cacheMapLock) {
            addSkinDataToCache(skin, skin.lightHash());
        }
    }
    
    public int size() {
        synchronized (cacheMapLock) {
            return cacheMapDatabase.size();
        }
    }
    
    public void clear() {
        synchronized (cacheMapLock) {
            cacheMapDatabase.clear();
        }
    }
    
    @SubscribeEvent
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        if (event.side == Side.SERVER && event.type == Type.SERVER && event.phase == Phase.END) {
            cacheMapDatabase.cleanupCheck();
        }
    }
    
    @Override
    public void itemExpired(Skin mapItem) {
        if (callback != null) {
            callback.itemExpired(mapItem);
        }
    }
    
    private void addSkinDataToCache(Skin skin, int skinId) {
        if (skin == null) {
            return;
        }
        if (!haveSkinOnDisk(skinId)) {
            saveSkinToDisk(skin);
        }
        if (!cacheMapDatabase.containsKey(skinId)) {
            cacheMapDatabase.put(skinId, skin);
        }
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
