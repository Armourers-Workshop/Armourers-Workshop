package riskyken.armourersWorkshop.client.skin.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinIdentifier;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.data.ExpiringHashMap;
import riskyken.armourersWorkshop.common.data.ExpiringHashMap.IExpiringMapCallback;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientRequestSkinData;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientRequestSkinId;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.ModLogger;

@SideOnly(Side.CLIENT)
public class ClientSkinCache implements IExpiringMapCallback<Skin> {
    
    public static ClientSkinCache INSTANCE;
    
    private final ExpiringHashMap<Integer, Skin> skinIDMap;
    private final HashMap<String, Integer> skinNameMap;
    private final HashSet<Integer> requestedSkinIDs;
    private final HashSet<String> requestedSkinNames;
    private final Executor skinRequestExecutor;
    
    private final HashMap<Integer, Integer> globalSkinIdMap;
    private final Executor globalSkinDownloadExecutor;
    private CompletionService<Skin> globalSkinDownloadCompletion;
    private final HashSet<Integer> globalRequestedSkin;
    
    public static void init() {
        INSTANCE = new ClientSkinCache();
    }
    
    protected ClientSkinCache() {
        skinIDMap = new ExpiringHashMap<Integer, Skin>(ConfigHandlerClient.clientModelCacheTime, this);
        skinNameMap = new HashMap<String, Integer>();
        requestedSkinIDs = new HashSet<Integer>();
        requestedSkinNames = new HashSet<String>();
        skinRequestExecutor = Executors.newFixedThreadPool(1);
        
        globalSkinIdMap = new HashMap<Integer, Integer>();
        globalSkinDownloadExecutor = Executors.newFixedThreadPool(2);
        globalSkinDownloadCompletion = new ExecutorCompletionService<Skin>(globalSkinDownloadExecutor);
        globalRequestedSkin = new HashSet<Integer>();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public Skin getSkin(ISkinPointer skinPointer) {
        return getSkin(skinPointer.getIdentifier(), true);
    }
    
    public Skin getSkin(ISkinPointer skinPointer, boolean requestSkin) {
        return getSkin(skinPointer.getIdentifier(), requestSkin);
    }
    
    public Skin getSkin(ISkinIdentifier identifier) {
        return getSkin(identifier, true);
    }
    
    public Skin getSkin(ISkinIdentifier identifier, boolean requestSkin) {
        if (identifier.getSkinGlobalId() != 0) {
            int serverId = identifier.getSkinGlobalId();
            if (haveSkinForGlobalId(serverId)) {
                return getSkinFromGlobalId(serverId);
            } else {
                if (requestSkin) {
                    requestSkinForServerId(serverId);
                }
                return null;
            }
        }
        
        if (identifier.getSkinLibraryFile() != null) {
            String fileName = identifier.getSkinLibraryFile().getFullName();
            if (haveIdForFileName(fileName)) {
                return getSkin(getIdForFileName(fileName), requestSkin);
            } else {
                if (requestSkin) {
                    requestIdForFileName(fileName);
                }
                return null;
            }
        }
        
        return getSkin(identifier.getSkinLocalId(), requestSkin);
    }
    
    private Skin getSkin(int skinId, boolean requestSkin) {
        synchronized (skinIDMap) {
            if (skinIDMap.containsKey(skinId)) {
                return skinIDMap.get(skinId);
            }
        }
        if (requestSkin) {
            requestSkinFromServer(skinId);
        }
        return null;
    }
    
    public void requestSkinFromServer(ISkinPointer skinPointer) {
        requestSkinFromServer(skinPointer.getIdentifier());
    }
    
    private void requestSkinFromServer(ISkinIdentifier identifier) {
        if (identifier.getSkinGlobalId() != 0) {
            int serverId = identifier.getSkinGlobalId();
            if (!haveSkinForGlobalId(serverId)) {
                requestSkinForServerId(serverId);
            }
            return;
        }
        
        if (identifier.getSkinLibraryFile() != null) {
            String fileName = identifier.getSkinLibraryFile().getFullName();
            if (haveIdForFileName(fileName)) {
                requestSkinFromServer(getIdForFileName(fileName));
            } else {
                requestIdForFileName(fileName);
            }
            return;
        }
        requestSkinFromServer(identifier.getSkinLocalId());
    }
    
    private void requestSkinFromServer(int skinId) {
        synchronized (requestedSkinIDs) {
            if (!requestedSkinIDs.contains(skinId)) {
                skinRequestExecutor.execute(new SkinRequestThread(skinId));
                requestedSkinIDs.add(skinId);
            }
        }
    }
    
    public boolean isSkinInCache(ISkinPointer skinPointer) {
        return isSkinInCache(skinPointer.getIdentifier());
    }
    
    public boolean isSkinInCache(ISkinIdentifier identifier) {
        if (identifier.getSkinGlobalId() != 0) {
            int serverId = identifier.getSkinGlobalId();
            return haveSkinForGlobalId(serverId);
        }
        
        if (identifier.getSkinLibraryFile() != null) {
            if (haveIdForFileName(identifier.getSkinLibraryFile().getFullName())) {
                return isSkinInCache(getIdForFileName(identifier.getSkinLibraryFile().getFullName()));
            }
            return false;
        }
        
        return isSkinInCache(identifier.getSkinLocalId());
    }
    
    private boolean isSkinInCache(int skinId) {
        synchronized (skinIDMap) {
            return skinIDMap.containsKey(skinId); 
        }
    }
    
    public Skin getSkin(String fileName, boolean requestSkin) {
        if (haveIdForFileName(fileName)) {
            int skinId = getIdForFileName(fileName);
            if (isSkinInCache(skinId)) {
                synchronized (skinIDMap) {
                    return skinIDMap.get(skinId);
                }
            } else {
                if (requestSkin) {
                    requestSkinFromServer(skinId);
                }
            }
        } else {
            if (requestSkin) {
                requestIdForFileName(fileName);
            }
        }
        return null;
    }
    
    private Skin getSkinFromGlobalId(int serverId) {
        synchronized (globalSkinIdMap) {
            if (globalSkinIdMap.containsKey(serverId)) {
                int skinId = globalSkinIdMap.get(serverId);
                if (isSkinInCache(skinId)) {
                    synchronized (skinIDMap) {
                        return skinIDMap.get(skinId);
                    }
                } else {
                    globalSkinIdMap.remove(serverId);
                }
            }
        }
        return null;
    }
    
    public void addGlobalIdMap(Skin skin) {
        synchronized (globalSkinIdMap) {
            globalSkinIdMap.put(skin.serverId, skin.lightHash());
        }
    }
    
    private boolean haveSkinForGlobalId(int serverId) {
        synchronized (globalSkinIdMap) {
            if (globalSkinIdMap.containsKey(serverId)) {
                int skinId = globalSkinIdMap.get(serverId);
                return isSkinInCache(skinId);
            }
        }
        return false;
    }
    
    private void requestSkinForServerId(int serverId) {
        synchronized (globalRequestedSkin) {
            if (globalRequestedSkin.contains(serverId)) {
                return;
            }
            globalRequestedSkin.add(serverId);
            SkinDownloader.downloadSkin(globalSkinDownloadCompletion, serverId);
        }
    }
    
    private boolean haveIdForFileName(String fileName) {
        synchronized (skinNameMap) {
            return skinNameMap.containsKey(fileName);
        }
    }
    
    public void clearIdForFileName(String fileName) {
        synchronized (skinNameMap) {
            if (skinNameMap.containsKey(fileName)) {
                ModLogger.log("removing name map for " + fileName);
            }
            skinNameMap.remove(fileName);
        }
    }
    
    private int getIdForFileName(String fileName) {
        synchronized (skinNameMap) {
            return skinNameMap.get(fileName);
        }
    }
    
    private void requestIdForFileName(String fileName) {
        synchronized (requestedSkinNames) {
            if (!requestedSkinNames.contains(fileName)) {
                requestedSkinNames.add(fileName);
                MessageClientRequestSkinId message = new MessageClientRequestSkinId(fileName);
                PacketHandler.networkWrapper.sendToServer(message);
            }
        }
    }
    
    public void setIdForFileName(String fileName, int skinId) {
        synchronized (requestedSkinNames) {
            if (requestedSkinNames.contains(fileName)) {
                requestedSkinNames.remove(fileName);
            } else {
                ModLogger.log(Level.WARN, String.format(
                        "Got ID:%s for file name:%s but it was not requested.",
                        String.valueOf(skinId), fileName));
            }
            synchronized (skinNameMap) {
                skinNameMap.put(fileName, skinId) ;
            }
        }
    }
    
    public void receivedModelFromBakery(Skin skin) {
        int skinID = skin.requestId;
        synchronized (requestedSkinIDs) {
            synchronized (skinIDMap) {
                if (skinIDMap.containsKey(skinID)) {
                    Skin oldSkin = skinIDMap.get(skinID);
                    skinIDMap.remove(skinID);
                    oldSkin.cleanUpDisplayLists();
                    ModLogger.log("removing skin");
                }
                
                if (requestedSkinIDs.contains(skinID)) {
                    skinIDMap.put(skinID, skin);
                    requestedSkinIDs.remove(skinID);
                } else if (skin.serverId != -1) {
                    skinID = skin.lightHash();
                    synchronized (skinIDMap) {
                        globalSkinIdMap.put(skin.serverId, skinID);
                    }
                    skinIDMap.put(skinID, skin);
                    synchronized (globalRequestedSkin) {
                        globalRequestedSkin.remove(skin.serverId);
                    }
                } else {
                    skinID = skin.lightHash();
                    skinIDMap.put(skinID, skin);
                    ModLogger.log(Level.WARN, "Got an unknown skin ID: " + skinID);
                }
            }
        }
    }
    
    public int getCacheSize() {
        synchronized (skinIDMap) {
            return skinIDMap.size();
        }
    }
    
    public int getRequestQueueSize() {
        synchronized (requestedSkinIDs) {
            return requestedSkinIDs.size();
        }
    }
    
    public int getModelCount() {
        int count = 0;
        synchronized (skinIDMap) {
            Object[] keySet = skinIDMap.getKeySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin skin = skinIDMap.getQuiet(key);
                if (skin != null) {
                    count += skin.getModelCount();
                }
            }
        }
        return count;
    }
    
    public int getPartCount() {
        int count = 0;
        synchronized (skinIDMap) {
            Object[] keySet = skinIDMap.getKeySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin skin = skinIDMap.getQuiet(key);
                count += skin.getPartCount();
            }
        }
        return count;
    }
    
    public void clearCache() {
        synchronized (skinIDMap) {
            Object[] keySet = skinIDMap.getKeySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin customArmourItemData = skinIDMap.get(key);
                skinIDMap.remove(key);
                customArmourItemData.cleanUpDisplayLists();
            }
        }
        synchronized (requestedSkinIDs) {
            requestedSkinIDs.clear();
        }
        synchronized (globalRequestedSkin) {
            globalRequestedSkin.clear();
        }
        synchronized (globalSkinIdMap) {
            globalSkinIdMap.clear();
        }
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT & event.type == Type.CLIENT & event.phase == Phase.END) {
            skinIDMap.cleanupCheck();
            
            Future<Skin> futureSkin = globalSkinDownloadCompletion.poll();
            if (futureSkin != null) {
                try {
                    Skin skin = futureSkin.get();
                    if (skin != null) {
                        SkinPointer skinPointer = new SkinPointer(skin);
                        if (skin != null && !isSkinInCache(skinPointer)) {
                            ModelBakery.INSTANCE.receivedUnbakedModel(skin);
                        } else {
                            if (skin != null) {
                                addGlobalIdMap(skin);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void itemExpired(Skin mapItem) {
        mapItem.cleanUpDisplayLists();
    }
    
    private static class SkinRequestThread implements Runnable {
        
        private int skinId;
        
        public SkinRequestThread(int skinId) {
            this.skinId = skinId;
        }
        
        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            PacketHandler.networkWrapper.sendToServer(new MessageClientRequestSkinData(skinId));
        }
    }
}
