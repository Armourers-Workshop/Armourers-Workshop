package riskyken.armourersWorkshop.common.skin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageServerSendEquipmentData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;

/**
 * Holds a cache of equipment data on the server that will be sent to clients if
 * they request it.
 * 
 * @author RiskyKen
 *
 */
public final class SkinDataCache {
    
    public static SkinDataCache INSTANCE = null;
    
    private HashMap<Integer, Skin> equipmentDataCache = new HashMap<Integer, Skin>();
    private ArrayList<QueueMessage> messageQueue = new ArrayList<QueueMessage>();
    private long lastTick;
    
    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new SkinDataCache();
        }
    }
    
    public SkinDataCache() {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        if (event.side == Side.SERVER && event.type == Type.SERVER && event.phase == Phase.END) {
            processMessageQueue();
        }
    }
    
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        SkinIOUtils.makeDatabaseDirectory();
    }
    
    public void processMessageQueue() {
        long curTick = System.currentTimeMillis();
        if (curTick >= lastTick + 20L) {
            lastTick = curTick;
            synchronized (messageQueue) {
                if (messageQueue.size() > 0) {
                    processMessage(messageQueue.get(0));
                    messageQueue.remove(0);
                }
            }
        }
    }
    
    public void processMessage(QueueMessage queueMessage) {
        
        if (!equipmentDataCache.containsKey(queueMessage.equipmentId)) {
            if (haveEquipmentOnDisk(queueMessage.equipmentId)) {
                Skin equipmentData;
                equipmentData = loadEquipmentFromDisk(queueMessage.equipmentId);
                equipmentData.requestId = queueMessage.equipmentId;
                addEquipmentDataToCache(equipmentData, queueMessage.equipmentId);
                if (equipmentData.hashCode() != queueMessage.equipmentId) {
                    addEquipmentDataToCache(equipmentData, equipmentData.hashCode());
                }
            }
        }
        
        if (equipmentDataCache.containsKey(queueMessage.equipmentId)) {
            Skin equpmentData = equipmentDataCache.get(queueMessage.equipmentId);
            PacketHandler.networkWrapper.sendTo(new MessageServerSendEquipmentData(equpmentData), queueMessage.player);
        } else {
            ModLogger.log(Level.ERROR, "Equipment id:" + queueMessage.equipmentId +" was requested by "
        + queueMessage.player.getCommandSenderName() + " but was not found.");
        }
    }
    
    public void addEquipmentDataToCache(Skin equipmentData) {
        addEquipmentDataToCache(equipmentData, equipmentData.hashCode());
    }
    
    public void addEquipmentDataToCache(Skin equipmentData, int equipmentId) {
        if (equipmentData == null) {
            return;
        }
        if (!equipmentDataCache.containsKey(equipmentId)) {
            equipmentDataCache.put(equipmentId, equipmentData);
            if (!haveEquipmentOnDisk(equipmentId)) {
                saveEquipmentToDisk(equipmentData);
            }
        }
    }
    
    public Skin getEquipmentData(int equipmentId) {
        if (!equipmentDataCache.containsKey(equipmentId)) {
            if (haveEquipmentOnDisk(equipmentId)) {
                Skin equipmentData;
                equipmentData = loadEquipmentFromDisk(equipmentId);
                addEquipmentDataToCache(equipmentData, equipmentId);
            }
        }
        if (equipmentDataCache.containsKey(equipmentId)) {
            return equipmentDataCache.get(equipmentId);
        }
        return null;
    }
    
    public void clientRequestEquipmentData(int equipmentId, EntityPlayerMP player) {
        QueueMessage queueMessage = new QueueMessage(equipmentId, player);
        synchronized (messageQueue) {
            messageQueue.add(queueMessage);
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
    
    public class QueueMessage {
        
        public final int equipmentId;
        public final EntityPlayerMP player;
        
        public QueueMessage(int equipmentId, EntityPlayerMP player) {
            this.equipmentId = equipmentId;
            this.player = player;
        }
    }
}
