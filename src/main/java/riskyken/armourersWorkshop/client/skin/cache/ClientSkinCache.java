package riskyken.armourersWorkshop.client.skin.cache;

import java.util.HashSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
import riskyken.armourersWorkshop.client.model.bake.ModelBakery.BakedSkin;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.data.ExpiringHashMap;
import riskyken.armourersWorkshop.common.data.ExpiringHashMap.IExpiringMapCallback;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientRequestSkinData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinIdentifier;
import riskyken.armourersWorkshop.utils.ModLogger;

@SideOnly(Side.CLIENT)
public class ClientSkinCache implements IExpiringMapCallback<Skin> {
    
    public static ClientSkinCache INSTANCE;
    
    /** Cache of skins that are in memory. */
    private final ExpiringHashMap<ISkinIdentifier, Skin> skinIDMap;
    
    /** Skin IDs that have been requested from the server. */
    private final HashSet<ISkinIdentifier> requestedSkinIDs;
    
    private final Executor skinRequestExecutor;
    
    public static void init() {
        INSTANCE = new ClientSkinCache();
    }
    
    protected ClientSkinCache() {
        skinIDMap = new ExpiringHashMap<ISkinIdentifier, Skin>(ConfigHandlerClient.clientModelCacheTime, this);
        requestedSkinIDs = new HashSet<ISkinIdentifier>();
        skinRequestExecutor = Executors.newFixedThreadPool(1);
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
        synchronized (skinIDMap) {
            if (skinIDMap.containsKey(identifier)) {
                return skinIDMap.get(identifier);
            }
        }
        if (requestSkin) {
            requestSkinFromServer(identifier);
        }
        return null;
    }
    
    public void requestSkinFromServer(ISkinPointer skinPointer) {
        requestSkinFromServer(skinPointer.getIdentifier());
    }
    
    private void requestSkinFromServer(ISkinIdentifier identifier) {
        synchronized (requestedSkinIDs) {
            if (!requestedSkinIDs.contains(identifier)) {
                skinRequestExecutor.execute(new SkinRequestThread(identifier));
                requestedSkinIDs.add(identifier);
            }
        }
    }
    
    public boolean isSkinInCache(ISkinPointer skinPointer) {
        return isSkinInCache(skinPointer.getIdentifier());
    }
    
    public boolean isSkinInCache(ISkinIdentifier identifier) {
        synchronized (skinIDMap) {
            return skinIDMap.containsKey(identifier); 
        }
    }
    
    public void markSkinAsDirty(ISkinIdentifier identifier) {
        synchronized (skinIDMap) {
            skinIDMap.remove(identifier);
        }
    }
    
    public void receivedModelFromBakery(BakedSkin bakedSkin) {
        SkinIdentifier identifierRequested = bakedSkin.getSkinIdentifierRequested();
        synchronized (requestedSkinIDs) {
            synchronized (skinIDMap) {
                if (skinIDMap.containsKey(identifierRequested)) {
                    // We already have this skin, remove the old one before adding the new one.
                    Skin oldSkin = skinIDMap.get(identifierRequested);
                    skinIDMap.remove(identifierRequested);
                    oldSkin.cleanUpDisplayLists();
                    ModLogger.log("removing skin");
                }
                if (requestedSkinIDs.contains(identifierRequested)) {
                    skinIDMap.put(identifierRequested, bakedSkin.getSkin());
                    requestedSkinIDs.remove(identifierRequested);
                } else {
                    // We did not request this skin.
                    skinIDMap.put(bakedSkin.getSkinIdentifierUpdated(), bakedSkin.getSkin());
                    ModLogger.log(Level.WARN, "Got an unknown skin - Identifier: " + bakedSkin.getSkinIdentifierUpdated().toString());
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
                ISkinIdentifier key = (ISkinIdentifier) keySet[i];
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
                ISkinIdentifier key = (ISkinIdentifier) keySet[i];
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
                ISkinIdentifier key = (ISkinIdentifier) keySet[i];
                Skin customArmourItemData = skinIDMap.get(key);
                skinIDMap.remove(key);
                customArmourItemData.cleanUpDisplayLists();
            }
        }
        synchronized (requestedSkinIDs) {
            requestedSkinIDs.clear();
        }
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT & event.type == Type.CLIENT & event.phase == Phase.END) {
            skinIDMap.cleanupCheck();
        }
    }

    @Override
    public void itemExpired(Skin mapItem) {
        mapItem.cleanUpDisplayLists();
    }
    
    private static class SkinRequestThread implements Runnable {
        
        private ISkinIdentifier skinIdentifier;
        
        public SkinRequestThread(ISkinIdentifier skinIdentifier) {
            this.skinIdentifier = skinIdentifier;
        }
        
        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            PacketHandler.networkWrapper.sendToServer(new MessageClientRequestSkinData(skinIdentifier));
        }
    }
}
