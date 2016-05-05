package riskyken.armourersWorkshop.client.skin;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientRequestSkinData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;

@SideOnly(Side.CLIENT)
public class ClientSkinCache {
    
    public static ClientSkinCache INSTANCE;
    
    private final HashMap<Integer, Skin> skinIDMap;
    private final HashMap<String, Skin> skinNameMap;
    private final HashSet<Integer> requestedSkinIDs;
    private final HashSet<String> requestedSkinNames;
    
    public static void init() {
        INSTANCE = new ClientSkinCache();
    }
    
    protected ClientSkinCache() {
        skinIDMap = new HashMap<Integer, Skin>();
        skinNameMap = new HashMap<String, Skin>();
        requestedSkinIDs = new HashSet<Integer>();
        requestedSkinNames = new HashSet<String>();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void requestSkinFromServer(String fileName) {
        synchronized (requestedSkinNames) {
            if (!requestedSkinNames.contains(fileName)) {
                requestedSkinNames.add(fileName);
            }
        }
    }
    
    public void requestSkinFromServer(ISkinPointer skinPointer) {
        requestSkinFromServer(skinPointer.getSkinId());
    }
    
    public void requestSkinFromServer(int skinID) {
        synchronized (requestedSkinIDs) {
            if (!requestedSkinIDs.contains(skinID)) {
                PacketHandler.networkWrapper.sendToServer(new MessageClientRequestSkinData(skinID));
                requestedSkinIDs.add(skinID);
            }
        }
    }
    
    public boolean isSkinInCache(String fileName) {
        synchronized (skinNameMap) {
            return skinNameMap.containsKey(fileName);
        }
    }
    /*
    public boolean isSkinInCache(int skinID) {
        synchronized (skinIDMap) {
            return skinIDMap.containsKey(skinID); 
        }
    }
    */
    public boolean isSkinInCache(ISkinPointer skinPointer) {
        synchronized (skinIDMap) {
            return skinIDMap.containsKey(skinPointer.getSkinId()); 
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
            }
            
            if (requestedSkinIDs.contains(skinID)) {
                synchronized (skinIDMap) {
                    skinIDMap.put(skinID, skin);
                }
                requestedSkinIDs.remove(skinID);
            } else {
                ModLogger.log(Level.WARN, "Got an unknown skin ID: " + skinID);
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
            Object[] keySet = skinIDMap.keySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin skin = skinIDMap.get(key);
                count += skin.getModelCount();
            }
        }
        return count;
    }
    
    public int getPartCount() {
        int count = 0;
        synchronized (skinIDMap) {
            Object[] keySet = skinIDMap.keySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin skin = skinIDMap.get(key);
                count += skin.getPartCount();
            }
        }
        return count;
    }
    
    public void clearCache() {
        synchronized (skinIDMap) {
            Object[] keySet = skinIDMap.keySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin customArmourItemData = skinIDMap.get(key);
                skinIDMap.remove(key);
                customArmourItemData.cleanUpDisplayLists();
            }
        }
    }
    
    public Skin getSkin(ISkinPointer skinPointer) {
        return getSkin(skinPointer, true);
    }
    
    public Skin getSkin(ISkinPointer skinPointer, boolean requestSkin) {
        synchronized (skinIDMap) {
            if (skinIDMap.containsKey(skinPointer.getSkinId())) {
                return skinIDMap.get(skinPointer.getSkinId());
            }
        }
        if (requestSkin) {
            requestSkinFromServer(skinPointer.getSkinId());
        }
        return null;
    }
    /*
    public Skin getSkin(int skinID) {
        return getSkin(skinID, true);
    }
    
    public Skin getSkin(int skinID, boolean requestSkin) {
        synchronized (skinIDMap) {
            if (skinIDMap.containsKey(skinID)) {
                return skinIDMap.get(skinID);
            }
        }
        if (requestSkin) {
            requestSkinFromServer(skinID);
        }
        return null;
    }
    */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT & event.type == Type.CLIENT & event.phase == Phase.END) {
            tick();
        }
    }
    
    public void tick() {
        synchronized (skinIDMap) {
            Object[] keySet = skinIDMap.keySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                skinIDMap.get(key).tick();
            }
            
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin customArmourItemData = skinIDMap.get(key);
                if (customArmourItemData.needsCleanup(ConfigHandler.clientModelCacheTime)) {
                    skinIDMap.remove(key);
                    customArmourItemData.cleanUpDisplayLists();
                    break;
                }
            }
        }
    }
}
