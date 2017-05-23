package riskyken.armourersWorkshop.common.skin.cache;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.data.ExpiringHashMap;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSkinDataSend;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSkinIdSend;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

/**
 * Holds a cache of equipment data on the server that will be sent to clients if
 * they request it.
 * 
 * @author RiskyKen
 *
 */
public final class CommonSkinCache implements Runnable {
    
    public static final CommonSkinCache INSTANCE = new CommonSkinCache();
    
    /** Cache of skins that are in memory. */
    private ExpiringHashMap<Integer, Skin> skinDataCache;
    
    private HashMap<String, Integer> fileNameIdLinkMap = new HashMap<String, Integer>();
    
    /** A list of skin that need to be loaded. */
    private ArrayList<Integer> skinLoadQueue = new ArrayList<Integer>();
    
    private ArrayList<QueueMessage> messageQueue = new ArrayList<QueueMessage>();
    
    private volatile Thread serverSkinThread = null;
    
    private long lastSendTick;
    
    private boolean madeDatabase = false;
    
    public CommonSkinCache() {
        skinDataCache = new ExpiringHashMap<Integer, Skin>(ConfigHandler.serverModelCacheTime);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void clearAll() {
        synchronized (skinDataCache) {
            skinDataCache.clear();
            messageQueue.clear();
        }
    }
    
    
    public void serverStarted() {
        SkinIOUtils.makeDatabaseDirectory();
        serverSkinThread = new Thread(this, "Armourer's Workshop Server Skin Thread");
        serverSkinThread.start();
    }
    
    public void serverStopped() {
        clearAll();
        serverSkinThread = null;
    }
    
    @SubscribeEvent
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        if (event.side == Side.SERVER && event.type == Type.SERVER && event.phase == Phase.END) {
            skinDataCache.cleanupCheck();
        }
    }
    
    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        ModLogger.log("Starting server skin thread.");
        while (serverSkinThread == thisThread) {
            try {
                thisThread.sleep(10);
            } catch (InterruptedException e) {
            }
            processMessageQueue();
            loadSkinQueue();
        }
        ModLogger.log("Stopped server skin thread.");
    }
    
    public void clientRequestEquipmentData(int skinId, EntityPlayerMP player) {
        QueueMessage queueMessage = new QueueMessage(skinId, player);
        synchronized (messageQueue) {
            messageQueue.add(queueMessage);
        }
    }
    
    public void clientRequestSkinId(String fileName, EntityPlayerMP player) {
        QueueMessage queueMessage = new QueueMessage(fileName, player);
        synchronized (messageQueue) {
            messageQueue.add(queueMessage);
        }
    }
    
    private void processMessageQueue() {
        if (ConfigHandler.serverSkinSendRate > 1) {
            long curTick = System.currentTimeMillis();
            if (curTick >= lastSendTick + (60000 / ConfigHandler.serverSkinSendRate)) {
                lastSendTick = curTick;
                processNextMessage();
            }
        } else {
            processNextMessage();
        }
    }
    
    private void loadSkinQueue() {
        if (skinLoadQueue.size() > 0) {
            int skinId = skinLoadQueue.get(0);
            synchronized (skinDataCache) {
                if (haveEquipmentOnDisk(skinId)) {
                    Skin skin = loadEquipmentFromDisk(skinId);
                    addEquipmentDataToCache(skin, skinId);
                }
            }
            synchronized (skinLoadQueue) {
                skinLoadQueue.remove(0);
            }
        }
    }
    
    private void processNextMessage() {
        synchronized (messageQueue) {
            if (messageQueue.size() > 0) {
                processMessage(messageQueue.get(0));
                messageQueue.remove(0);
            }
        }
    }
    
    public Skin addSkinToCache(InputStream inputStream) {
        Skin skin = SkinIOUtils.loadSkinFromStream(inputStream);
        if (skin != null) {
            addEquipmentDataToCache(skin, null);
            return skin;
        }
        return null;
    }
    
    private void processMessage(QueueMessage queueMessage) {
        if (queueMessage.useId) {
            sendSkinToClient(queueMessage.skinId, queueMessage.player);
        } else {
            sendSkinIdToClient(queueMessage.fileName, queueMessage.player);
        }
    }
    
    private void sendSkinToClient(int skinId, EntityPlayerMP player) {
        synchronized (skinDataCache) {
            if (!skinDataCache.containsKey(skinId)) {
                if (haveEquipmentOnDisk(skinId)) {
                    Skin skin;
                    skin = loadEquipmentFromDisk(skinId);
                    if (skin != null) {
                        addEquipmentDataToCache(skin, skinId);
                        if (skin.hashCode() != skinId) {
                            addEquipmentDataToCache(skin, skin.hashCode());
                        }
                    } else {
                        ModLogger.log(Level.ERROR, String.format("Failed to load skin id:%s from disk.", String.valueOf(skinId)));
                    }
                }
            }
            
            if (skinDataCache.containsKey(skinId)) {
                Skin skin = skinDataCache.get(skinId);
                skin.requestId = skinId;
                PacketHandler.networkWrapper.sendTo(new MessageServerSkinDataSend(skin), player);
            } else {
                ModLogger.log(Level.ERROR, "Equipment id:" + skinId +" was requested by "
            + player.getCommandSenderName() + " but was not found.");
            }
        }
    }
    
    private void sendSkinIdToClient(String fileName, EntityPlayerMP player) {
        if (!fileNameIdLinkMap.containsKey(fileName)) {
            String basicFileName = fileName;
            Skin skin = SkinIOUtils.loadSkinFromFileName(basicFileName + ".armour");
            if (skin != null) {
                addEquipmentDataToCache(skin, basicFileName);
            } else {
                ModLogger.log(Level.ERROR, String.format("Player %s requested ID for file name %s but the file was not found.",
                        player.getCommandSenderName(), fileName));
            }
        }
        
        if (fileNameIdLinkMap.containsKey(fileName)) {
            MessageServerSkinIdSend message = new MessageServerSkinIdSend(fileName, fileNameIdLinkMap.get(fileName));
            PacketHandler.networkWrapper.sendTo(message, player);
        }
    }
    
    public void addEquipmentDataToCache(Skin skin, String fileName) {
        try {
            skin.lightHash();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, String.format("Unable to create ID for file %s.", fileName));
            return;
        }
        addEquipmentDataToCache(skin, skin.lightHash());
        if (!StringUtils.isNullOrEmpty(fileName)) {
            fileNameIdLinkMap.put(fileName, skin.lightHash());
        }
    }
    
    private void addEquipmentDataToCache(Skin equipmentData, int equipmentId) {
        if (equipmentData == null) {
            return;
        }
        synchronized (skinDataCache) {
            if (!skinDataCache.containsKey(equipmentId)) {
                skinDataCache.put(equipmentId, equipmentData);
                if (!haveEquipmentOnDisk(equipmentId)) {
                    saveEquipmentToDisk(equipmentData);
                }
            }
        }
    }
    
    public Skin getSkin(ISkinPointer skinPointer) {
        return getEquipmentData(skinPointer.getSkinId());
    }
    
    public Skin getEquipmentData(int equipmentId) {
        synchronized (skinDataCache) {
            if (!skinDataCache.containsKey(equipmentId)) {
                if (haveEquipmentOnDisk(equipmentId)) {
                    Skin equipmentData;
                    equipmentData = loadEquipmentFromDisk(equipmentId);
                    addEquipmentDataToCache(equipmentData, equipmentId);
                }
            }
        }
        if (skinDataCache.containsKey(equipmentId)) {
            Skin skin = skinDataCache.get(equipmentId);
            return skin;
        }
        return null;
    }
    
    /**
     * Returns a skin if it is in the cache. If not the skin will be loaded by another thread.
     * @param skinId
     * @return 
     */
    public Skin softGetSkin(int skinId) {
        if (skinDataCache.containsKey(skinId)) {
            Skin skin = skinDataCache.get(skinId);
            return skin;
        }
        synchronized (skinLoadQueue) {
            boolean inQueue = false;
            for (int i = 0; i < skinLoadQueue.size(); i++) {
                if (skinLoadQueue.get(i) == skinId) {
                    inQueue = true;
                    break;
                }
            }
            if (!inQueue) {
                skinLoadQueue.add(skinId);
            }
        }
        return null;
    }
    
    public int size() {
        synchronized (skinDataCache) {
            return skinDataCache.size();
        }
    }
    
    private boolean haveEquipmentOnDisk(int equipmentId) {
        File file = new File(SkinIOUtils.getSkinDatabaseDirectory(), String.valueOf(equipmentId));
        return file.exists();
    }
    
    private void saveEquipmentToDisk(Skin skin) {
        File file = new File(SkinIOUtils.getSkinDatabaseDirectory(), String.valueOf(skin.hashCode()));
        SkinIOUtils.saveSkinToFile(file, skin);
    }
    
    private Skin loadEquipmentFromDisk(int equipmentId) {
        File file = new File(SkinIOUtils.getSkinDatabaseDirectory(), String.valueOf(equipmentId));
        return SkinIOUtils.loadSkinFromFile(file);
    }
    
    private class QueueMessage {
        
        public final int skinId;
        public final String fileName;
        public final EntityPlayerMP player;
        public final boolean useId;
        
        public QueueMessage(int skinId, EntityPlayerMP player) {
            this.skinId = skinId;
            this.fileName = null;
            this.player = player;
            this.useId = true;
        }
        
        public QueueMessage(String fileName, EntityPlayerMP player) {
            this.skinId = -1;
            this.fileName = fileName;
            this.player = player;
            this.useId = false;
        }
    }
}
