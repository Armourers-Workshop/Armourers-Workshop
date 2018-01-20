package riskyken.armourersWorkshop.common.skin.cache;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.api.common.library.ILibraryFile;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinIdentifier;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.data.BidirectionalHashMap;
import riskyken.armourersWorkshop.common.data.ExpiringHashMap;
import riskyken.armourersWorkshop.common.data.ExpiringHashMap.IExpiringMapCallback;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSendSkinData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinIdentifier;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

/**
 * Holds a cache of equipment data on the server that will be sent to clients if
 * they request it.
 * 
 * @author RiskyKen
 *
 */
public final class CommonSkinCache implements Runnable, IExpiringMapCallback<Skin> {
    
    public static final CommonSkinCache INSTANCE = new CommonSkinCache();
    
    /** Cache of skins that are in memory. */
    private final ExpiringHashMap<Integer, Skin> cacheMapDatabase;
    private final BidirectionalHashMap<ILibraryFile, Integer> cacheMapFileLink;
    private final BidirectionalHashMap<Integer, Integer> cacheMapGlobalLink;
    
    /** A list of skin that need to be loaded. */
    private ArrayList<Integer> skinLoadQueueDatabase = new ArrayList<Integer>();
    private ArrayList<ILibraryFile> skinLoadQueueFile = new ArrayList<ILibraryFile>();
    
    private volatile Thread serverSkinThread = null;
    private ArrayList<QueueMessage> messageQueue = new ArrayList<QueueMessage>();
    private ArrayList<QueueMessage> messageWaitQueue = new ArrayList<QueueMessage>();
    private long lastMessageSendTick;
    private boolean madeDatabase = false;
    
    public CommonSkinCache() {
        cacheMapDatabase = new ExpiringHashMap<Integer, Skin>(ConfigHandler.serverModelCacheTime);
        cacheMapFileLink = new BidirectionalHashMap<ILibraryFile, Integer>();
        cacheMapGlobalLink = new BidirectionalHashMap<Integer, Integer>();
        
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void clearAll() {
        synchronized (cacheMapDatabase) {
            synchronized (cacheMapFileLink) {
                synchronized (cacheMapGlobalLink) {
                    cacheMapDatabase.clear();
                    cacheMapFileLink.clear();
                    cacheMapGlobalLink.clear();
                    messageQueue.clear();
                }
            }
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
            cacheMapDatabase.cleanupCheck();
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
    
    public void clientRequestEquipmentData(ISkinIdentifier skinIdentifier, EntityPlayerMP player) {
        QueueMessage queueMessage = new QueueMessage(skinIdentifier, player);
        synchronized (messageQueue) {
            messageQueue.add(queueMessage);
        }
    }
    
    private void processMessageQueue() {
        if (ConfigHandler.serverSkinSendRate > 1) {
            long curTick = System.currentTimeMillis();
            if (curTick >= lastMessageSendTick + (60000 / ConfigHandler.serverSkinSendRate)) {
                lastMessageSendTick = curTick;
                processNextMessage();
            }
        } else {
            processNextMessage();
        }
    }
    
    private void loadSkinQueue() {
        if (skinLoadQueueDatabase.size() > 0) {
            int skinId = skinLoadQueueDatabase.get(0);
            synchronized (cacheMapDatabase) {
                if (haveEquipmentOnDisk(skinId)) {
                    Skin skin = loadEquipmentFromDisk(skinId);
                    addEquipmentDataToCache(skin, skinId);
                }
            }
            synchronized (skinLoadQueueDatabase) {
                skinLoadQueueDatabase.remove(0);
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
            addEquipmentDataToCache(skin, (LibraryFile)null);
            return skin;
        }
        return null;
    }
    
    private void processMessage(QueueMessage queueMessage) {
        ISkinIdentifier identifier = queueMessage.skinIdentifier;
        EntityPlayerMP player = queueMessage.player;
        if (identifier.hasLocalId()) {
            sendLocalDatabaseSkinToClient(identifier, player);
        } else if (identifier.hasLibraryFile()) {
            sendLocalFileSkinToClient(identifier, player);
        } else if (identifier.hasGlobalId()) {
            sendGlobalDatabaseSkinToClient(queueMessage);
        } else {
            ModLogger.log(Level.ERROR, "Player " + player.getCommandSenderName() + " requested a skin with no vaid ID:" + identifier.toString());
        }
    }
    
    private void sendLocalDatabaseSkinToClient(ISkinIdentifier identifier, EntityPlayerMP player) {
        synchronized (cacheMapDatabase) {
            if (!cacheMapDatabase.containsKey(identifier.getSkinLocalId())) {
                
                if (haveEquipmentOnDisk(identifier.getSkinLocalId())) {
                    Skin skin;
                    skin = loadEquipmentFromDisk(identifier.getSkinLocalId());
                    if (skin != null) {
                        addEquipmentDataToCache(skin, identifier.getSkinLocalId());
                        if (skin.hashCode() != identifier.getSkinLocalId()) {
                            addEquipmentDataToCache(skin, skin.hashCode());
                        }
                    } else {
                        ModLogger.log(Level.ERROR, String.format("Failed to load skin id:%s from disk.", String.valueOf(identifier.getSkinLocalId())));
                    }
                }
                
            }
            
            if (cacheMapDatabase.containsKey(identifier.getSkinLocalId())) {
                Skin skin = cacheMapDatabase.get(identifier.getSkinLocalId());
                skin.requestId = (SkinIdentifier) identifier;
                PacketHandler.networkWrapper.sendTo(new MessageServerSendSkinData((SkinIdentifier) identifier, getFullIdentifier(skin, identifier), skin), player);
            } else {
                ModLogger.log(Level.ERROR, "Equipment id:" + identifier.getSkinLocalId() +" was requested by " + player.getCommandSenderName() + " but was not found.");
            }
        }
    }
    
    private void sendLocalFileSkinToClient(ISkinIdentifier identifier, EntityPlayerMP player) {
        synchronized (cacheMapFileLink) {
            if (!cacheMapFileLink.containsKey(identifier.getSkinLibraryFile())) {
                Skin skin = null;
                skin = SkinIOUtils.loadSkinFromFileName(identifier.getSkinLibraryFile().getFullName() + SkinIOUtils.SKIN_FILE_EXTENSION);
                if (skin != null) {
                    synchronized (cacheMapDatabase) {
                        addEquipmentDataToCache(skin, skin.lightHash());
                        cacheMapFileLink.put(identifier.getSkinLibraryFile(), skin.lightHash());
                    }
                } else {
                    ModLogger.log(Level.ERROR, String.format("Failed to load skin %s from disk. ", String.valueOf(identifier.getSkinLibraryFile().getFullName() + SkinIOUtils.SKIN_FILE_EXTENSION)));
                }
            }
            
            if (cacheMapFileLink.containsKey(identifier.getSkinLibraryFile())) {
                synchronized (cacheMapDatabase) {
                    int id = cacheMapFileLink.get(identifier.getSkinLibraryFile());
                    Skin skin = cacheMapDatabase.get(id);
                    if (skin != null) {
                        skin.requestId = (SkinIdentifier) identifier;
                        PacketHandler.networkWrapper.sendTo(new MessageServerSendSkinData((SkinIdentifier) identifier, getFullIdentifier(skin, identifier), skin), player);
                    } else {
                        ModLogger.log(Level.WARN, "Somehow failed to load a skin that we should have. ID was " + id);
                    }
                }
            }
        }
    }
    
    public void onGlobalSkinDownload(Skin skin, int globalId) {
        ModLogger.log("Skin downloaded.");
        synchronized (cacheMapGlobalLink) {
            if (skin != null) {
                synchronized (cacheMapDatabase) {
                    addEquipmentDataToCache(skin, skin.lightHash());
                    cacheMapGlobalLink.put(globalId, skin.lightHash());
                }
            } else {
                ModLogger.log(Level.ERROR, String.format("Failed to load skin %s from global database. ", String.valueOf(globalId)));
            }
        }
        for (int i = 0; i < messageWaitQueue.size(); i++) {
            if (messageWaitQueue.get(i).skinIdentifier.getSkinGlobalId() == globalId) {
                sendGlobalSkinToPlayer(messageWaitQueue.get(i).skinIdentifier, messageWaitQueue.get(i).player);
                messageWaitQueue.remove(i);
            }
        }
    }
    
    private void sendGlobalDatabaseSkinToClient(QueueMessage queueMessage) {
        ISkinIdentifier identifier = queueMessage.skinIdentifier;
        EntityPlayerMP player = queueMessage.player;
        synchronized (cacheMapGlobalLink) {
            if (!cacheMapGlobalLink.containsKey(identifier.getSkinGlobalId())) {
                messageWaitQueue.add(queueMessage);
                SkinCacheGlobal.INSTANCE.downloadSkin(identifier);
            } else {
                sendGlobalSkinToPlayer(identifier, player);
            }
        }
    }
    
    private void sendGlobalSkinToPlayer(ISkinIdentifier identifier, EntityPlayerMP player) {
        ModLogger.log("Sending skin to player");
        synchronized (cacheMapDatabase) {
            int id = cacheMapGlobalLink.get(identifier.getSkinGlobalId());
            Skin skin = cacheMapDatabase.get(id);
            if (skin != null) {
                skin.requestId = (SkinIdentifier) identifier;
                PacketHandler.networkWrapper.sendTo(new MessageServerSendSkinData((SkinIdentifier) identifier, getFullIdentifier(skin, identifier), skin), player);
            } else {
                ModLogger.log(Level.WARN, "Somehow failed to load a skin that we should have. ID was " + id);
            }
        }
    }
    
    public SkinIdentifier getFullIdentifier(Skin skin, ISkinIdentifier skinIdentifier) {
        int localId = skin.lightHash();
        ISkinType skinType = skin.getSkinType();
        ILibraryFile libraryFile = null;
        int globalId = 0;
        
        if (cacheMapFileLink.containsValue(skin.lightHash())) {
            libraryFile = cacheMapFileLink.getBackward(skin.lightHash());
        }
        
        if (cacheMapGlobalLink.containsValue(skin.lightHash())) {
            globalId = cacheMapGlobalLink.getBackward(skin.lightHash());
        }
        
        return new SkinIdentifier(localId, libraryFile, globalId, skinType);
    }
    
    public void addEquipmentDataToCache(Skin skin, LibraryFile file) {
        skin.lightHash();
        addEquipmentDataToCache(skin, skin.lightHash());
        if (file != null) {
            cacheMapFileLink.put(file, skin.lightHash());
        }
    }
    
    @Deprecated
    public void addEquipmentDataToCache(Skin skin, String fileName) {
        try {
            skin.lightHash();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, String.format("Unable to create ID for file %s.", fileName));
            return;
        }
        addEquipmentDataToCache(skin, new LibraryFile(fileName));
    }
    
    public void clearFileNameIdLink(LibraryFile file) {
        cacheMapFileLink.remove(file);
        //MessageServerSkinIdSend message = new MessageServerSkinIdSend(file.getFullName(), 0, true);
        //PacketHandler.networkWrapper.sendToAll(message);
    }
    
    private void addEquipmentDataToCache(Skin equipmentData, int equipmentId) {
        if (equipmentData == null) {
            return;
        }
        synchronized (cacheMapDatabase) {
            if (!cacheMapDatabase.containsKey(equipmentId)) {
                cacheMapDatabase.put(equipmentId, equipmentData);
                if (!haveEquipmentOnDisk(equipmentId)) {
                    saveEquipmentToDisk(equipmentData);
                }
            }
        }
    }
    
    public Skin getSkin(ISkinPointer  skinPointer) {
        return getSkin(skinPointer.getIdentifier());
    }
    
    public Skin getSkin(ISkinIdentifier skinIdentifier) {
        return getSkin(skinIdentifier.getSkinLocalId());
    }
    
    public Skin getSkin(int skinId) {
        synchronized (cacheMapDatabase) {
            if (!cacheMapDatabase.containsKey(skinId)) {
                if (haveEquipmentOnDisk(skinId)) {
                    Skin equipmentData;
                    equipmentData = loadEquipmentFromDisk(skinId);
                    addEquipmentDataToCache(equipmentData, skinId);
                }
            }
        }
        if (cacheMapDatabase.containsKey(skinId)) {
            Skin skin = cacheMapDatabase.get(skinId);
            return skin;
        }
        return null;
    }
    
    public Skin softGetSkin(ISkinIdentifier skinIdentifier) {
        return softGetSkin(skinIdentifier.getSkinLocalId());
    }
    
    /**
     * Returns a skin if it is in the cache. If not the skin will be loaded by another thread.
     * @param skinId
     * @return 
     */
    public Skin softGetSkin(int skinId) {
        if (cacheMapDatabase.containsKey(skinId)) {
            Skin skin = cacheMapDatabase.get(skinId);
            return skin;
        }
        synchronized (skinLoadQueueDatabase) {
            boolean inQueue = false;
            for (int i = 0; i < skinLoadQueueDatabase.size(); i++) {
                if (skinLoadQueueDatabase.get(i) == skinId) {
                    inQueue = true;
                    break;
                }
            }
            if (!inQueue) {
                skinLoadQueueDatabase.add(skinId);
            }
        }
        return null;
    }
    
    public int size() {
        synchronized (cacheMapDatabase) {
            return cacheMapDatabase.size();
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
    
    public static class QueueMessage {
        
        public final ISkinIdentifier skinIdentifier;
        public final EntityPlayerMP player;
        
        public QueueMessage(ISkinIdentifier skinIdentifier, EntityPlayerMP player) {
            this.skinIdentifier = skinIdentifier;
            this.player = player;
        }
    }

    @Override
    public void itemExpired(Skin mapItem) {
        synchronized (cacheMapFileLink) {
            synchronized (cacheMapGlobalLink) {
                int skinId = mapItem.lightHash();
                if (cacheMapFileLink.containsValue(skinId)) {
                    ILibraryFile libraryFile = cacheMapFileLink.getBackward(skinId);
                    cacheMapFileLink.remove(libraryFile);
                }
                if (cacheMapGlobalLink.containsValue(skinId)) {
                    int globalId = cacheMapGlobalLink.getBackward(skinId);
                    cacheMapGlobalLink.remove(globalId);
                }
            }
        }
    }
}
