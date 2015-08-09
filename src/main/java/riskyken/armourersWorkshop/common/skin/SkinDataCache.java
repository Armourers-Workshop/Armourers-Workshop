package riskyken.armourersWorkshop.common.skin;

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
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSkinDataSend;
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
public final class SkinDataCache implements Runnable {
    
    public static final SkinDataCache INSTANCE = new SkinDataCache();
    
    /** Cache of skins that are in memory. */
    private HashMap<Integer, Skin> skinDataCache = new HashMap<Integer, Skin>();
    
    private ArrayList<QueueMessage> messageQueue = new ArrayList<QueueMessage>();
    
    private volatile Thread serverSkinThread = null;
    
    private long lastSendTick;
    
    private boolean madeDatabase = false;
    
    public SkinDataCache() {
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void clearAll() {
        skinDataCache.clear();
        messageQueue.clear();
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
            checkForOldSkins();
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
        }
        ModLogger.log("Stopped server skin thread.");
    }
    
    public void clientRequestEquipmentData(int equipmentId, EntityPlayerMP player) {
        QueueMessage queueMessage = new QueueMessage(equipmentId, player);
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
    
    private void processNextMessage() {
        synchronized (messageQueue) {
            if (messageQueue.size() > 0) {
                processMessage(messageQueue.get(0));
                messageQueue.remove(0);
            }
        }
    }
    
    private void checkForOldSkins() {
        synchronized (skinDataCache) {
            Object[] keySet = skinDataCache.keySet().toArray();
            for (int i = 0; i < keySet.length; i++) {
                int key = (Integer) keySet[i];
                Skin skin = skinDataCache.get(key);
                skin.tick();
                if (skin.needsCleanup(ConfigHandler.serverModelCacheTime)) {
                    skinDataCache.remove(key);
                }
            }
        }
    }
    
    public Skin addSkinToCache(InputStream inputStream) {
        Skin skin = SkinIOUtils.loadSkinFromStream(inputStream);
        if (skin != null) {
            addEquipmentDataToCache(skin);
            return skin;
        }
        return null;
    }
    
    private void processMessage(QueueMessage queueMessage) {
        
        if (!skinDataCache.containsKey(queueMessage.equipmentId)) {
            if (haveEquipmentOnDisk(queueMessage.equipmentId)) {
                Skin skin;
                skin = loadEquipmentFromDisk(queueMessage.equipmentId);
                if (skin != null) {
                    addEquipmentDataToCache(skin, queueMessage.equipmentId);
                    if (skin.hashCode() != queueMessage.equipmentId) {
                        addEquipmentDataToCache(skin, skin.hashCode());
                    }
                } else {
                    ModLogger.log(Level.ERROR, String.format("Failed to load skin id:%s from disk.", String.valueOf(queueMessage.equipmentId)));
                }
            }
        }
        
        if (skinDataCache.containsKey(queueMessage.equipmentId)) {
            Skin skin = skinDataCache.get(queueMessage.equipmentId);
            skin.requestId = queueMessage.equipmentId;
            skin.onUsed();
            PacketHandler.networkWrapper.sendTo(new MessageServerSkinDataSend(skin), queueMessage.player);
        } else {
            ModLogger.log(Level.ERROR, "Equipment id:" + queueMessage.equipmentId +" was requested by "
        + queueMessage.player.getCommandSenderName() + " but was not found.");
        }
    }
    
    public void addEquipmentDataToCache(Skin equipmentData) {
        addEquipmentDataToCache(equipmentData, equipmentData.lightHash());
    }
    
    private void addEquipmentDataToCache(Skin equipmentData, int equipmentId) {
        if (equipmentData == null) {
            return;
        }
        if (!skinDataCache.containsKey(equipmentId)) {
            skinDataCache.put(equipmentId, equipmentData);
            if (!haveEquipmentOnDisk(equipmentId)) {
                saveEquipmentToDisk(equipmentData);
            }
        }
    }
    
    public Skin getEquipmentData(int equipmentId) {
        if (!skinDataCache.containsKey(equipmentId)) {
            if (haveEquipmentOnDisk(equipmentId)) {
                Skin equipmentData;
                equipmentData = loadEquipmentFromDisk(equipmentId);
                addEquipmentDataToCache(equipmentData, equipmentId);
            }
        }
        if (skinDataCache.containsKey(equipmentId)) {
            Skin skin = skinDataCache.get(equipmentId);
            skin.onUsed();
            return skin;
        }
        return null;
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
        
        public final int equipmentId;
        public final EntityPlayerMP player;
        
        public QueueMessage(int equipmentId, EntityPlayerMP player) {
            this.equipmentId = equipmentId;
            this.player = player;
        }
    }
}
