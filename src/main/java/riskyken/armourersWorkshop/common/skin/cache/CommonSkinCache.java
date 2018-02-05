package riskyken.armourersWorkshop.common.skin.cache;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.api.common.library.ILibraryFile;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinIdentifier;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.data.BidirectionalHashMap;
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
    
    private final SkinCacheLocalDatabase cacheLocalDatabase;
    
    /** Cache of skins that are in memory. */
    private final BidirectionalHashMap<ILibraryFile, Integer> cacheMapFileLink;
    private final BidirectionalHashMap<Integer, Integer> cacheMapGlobalLink;
    
    /** A list of skin that need to be loaded. */
    private ArrayList<ILibraryFile> skinLoadQueueFile = new ArrayList<ILibraryFile>();
    
    private volatile Thread serverSkinThread = null;
    private ArrayList<SkinRequestMessage> messageQueue = new ArrayList<SkinRequestMessage>();
    private ArrayList<SkinRequestMessage> messageWaitQueue = new ArrayList<SkinRequestMessage>();
    private long lastMessageSendTick;
    private boolean madeDatabase = false;
    
    public CommonSkinCache() {
        cacheLocalDatabase = new SkinCacheLocalDatabase(this);
        
        cacheMapFileLink = new BidirectionalHashMap<ILibraryFile, Integer>();
        cacheMapGlobalLink = new BidirectionalHashMap<Integer, Integer>();
    }
    
    public void clearAll() {
        cacheLocalDatabase.clear();
        synchronized (cacheMapFileLink) {
            synchronized (cacheMapGlobalLink) {
                cacheMapFileLink.clear();
                cacheMapGlobalLink.clear();
                messageQueue.clear();
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
    
    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        ModLogger.log("Starting server skin thread.");
        while (serverSkinThread == thisThread) {
            try {
                thisThread.sleep(10);
            } catch (InterruptedException e) {
            }
            cacheLocalDatabase.doSkinLoading();
            processMessageQueue();
        }
        ModLogger.log("Stopped server skin thread.");
    }
    
    public void clientRequestEquipmentData(ISkinIdentifier skinIdentifier, EntityPlayerMP player) {
        SkinRequestMessage queueMessage = new SkinRequestMessage(skinIdentifier, player);
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
    
    private void processMessage(SkinRequestMessage queueMessage) {
        ISkinIdentifier identifier = queueMessage.getSkinIdentifier();
        EntityPlayerMP player = queueMessage.getPlayer();
        if (identifier.hasLocalId()) {
            sendLocalDatabaseSkinToClient(queueMessage);
        } else if (identifier.hasLibraryFile()) {
            sendLocalFileSkinToClient(queueMessage);
        } else if (identifier.hasGlobalId()) {
            sendGlobalDatabaseSkinToClient(queueMessage);
        } else {
            ModLogger.log(Level.ERROR, "Player " + player.getCommandSenderName() + " requested a skin with no vaid ID:" + identifier.toString());
        }
    }
    
    private void sendLocalDatabaseSkinToClient(SkinRequestMessage requestMessage) {
        Skin skin = cacheLocalDatabase.get(requestMessage, true);
        if (skin != null) {
            SkinIdentifier identifier = (SkinIdentifier) requestMessage.getSkinIdentifier();
            skin.requestId = identifier;
            PacketHandler.networkWrapper.sendTo(new MessageServerSendSkinData(identifier, getFullIdentifier(skin, identifier), skin), requestMessage.getPlayer());
        } else {
            messageWaitQueue.add(requestMessage);
        }
    }
    
    private void sendLocalFileSkinToClient(SkinRequestMessage requestMessage) {
        ISkinIdentifier identifier = requestMessage.getSkinIdentifier();
        synchronized (cacheMapFileLink) {
            if (!cacheMapFileLink.containsKey(identifier.getSkinLibraryFile())) {
                Skin skin = null;
                skin = SkinIOUtils.loadSkinFromFileName(identifier.getSkinLibraryFile().getFullName() + SkinIOUtils.SKIN_FILE_EXTENSION);
                if (skin != null) {
                    cacheLocalDatabase.add(skin);
                    cacheMapFileLink.put(identifier.getSkinLibraryFile(), skin.lightHash());
                } else {
                    ModLogger.log(Level.ERROR, String.format("Failed to load skin %s from disk. ", String.valueOf(identifier.getSkinLibraryFile().getFullName() + SkinIOUtils.SKIN_FILE_EXTENSION)));
                }
            }
            
            if (cacheMapFileLink.containsKey(identifier.getSkinLibraryFile())) {
                int id = cacheMapFileLink.get(identifier.getSkinLibraryFile());
                SkinIdentifier newIdentifier = new SkinIdentifier(id, requestMessage.getSkinIdentifier().getSkinLibraryFile(), 0, requestMessage.getSkinIdentifier().getSkinType());
                Skin skin = cacheLocalDatabase.get(newIdentifier, false);
                if (skin != null) {
                    skin.requestId = (SkinIdentifier) identifier;
                    PacketHandler.networkWrapper.sendTo(new MessageServerSendSkinData((SkinIdentifier) identifier, getFullIdentifier(skin, identifier), skin), requestMessage.getPlayer());
                } else {
                    ModLogger.log(Level.WARN, "Somehow failed to load a skin that we should have. ID was " + id);
                }
            }
        }
    }
    
    public void onLocalDatabaseSkinLoaded(Skin skin, SkinRequestMessage requestMessage) {
        if (requestMessage.getPlayer() == null) {
            return;
        }
        SkinIdentifier requestIdentifier = (SkinIdentifier) requestMessage.getSkinIdentifier();
        for (int i = 0; i < messageWaitQueue.size(); i++) {
            if (messageWaitQueue.get(i).getSkinIdentifier() == requestMessage.getSkinIdentifier()) {
                skin.requestId = requestIdentifier;
                PacketHandler.networkWrapper.sendTo(new MessageServerSendSkinData(requestIdentifier, getFullIdentifier(skin, requestIdentifier), skin), requestMessage.getPlayer());
                messageWaitQueue.remove(i);
            }
        }
    }
    
    public void onGlobalSkinDownload(Skin skin, int globalId) {
        ModLogger.log(String.format("Skin downloaded: %d", globalId));
        
        //ModLogger.log("Locking A");
        //ModLogger.log("Locked A");
        //ModLogger.log("Locking B");
        synchronized (cacheMapGlobalLink) {
            //ModLogger.log("Locked B");
            if (skin != null) {
                    cacheLocalDatabase.add(skin);
                    cacheMapGlobalLink.put(globalId, skin.lightHash());
            } else {
                ModLogger.log(Level.ERROR, String.format("Failed to load skin %s from global database.", String.valueOf(globalId)));
            }
        }
        
        //ModLogger.log("Locking C");
        //ModLogger.log("Locked C");
        //ModLogger.log("Locking D");
        synchronized (cacheMapGlobalLink) {
            //ModLogger.log("Locked D");
            for (int i = 0; i < messageWaitQueue.size(); i++) {
                if (messageWaitQueue.get(i).getSkinIdentifier().getSkinGlobalId() == globalId) {
                    sendGlobalSkinToPlayer(messageWaitQueue.get(i));
                    messageWaitQueue.remove(i);
                }
            }
        }
    }
    
    private void sendGlobalDatabaseSkinToClient(SkinRequestMessage requestMessage) {
        ISkinIdentifier identifier = requestMessage.getSkinIdentifier();
        synchronized (cacheMapGlobalLink) {
            if (!cacheMapGlobalLink.containsKey(identifier.getSkinGlobalId())) {
                messageWaitQueue.add(requestMessage);
                SkinCacheGlobal.INSTANCE.downloadSkin(identifier);
            } else {
                sendGlobalSkinToPlayer(requestMessage);
            }
        }
    }
    
    private void sendGlobalSkinToPlayer(SkinRequestMessage requestMessage) {
        //ModLogger.log("Sending skin to player");
        ISkinIdentifier identifier = requestMessage.getSkinIdentifier();
        int id = cacheMapGlobalLink.get(identifier.getSkinGlobalId());
        Skin skin = cacheLocalDatabase.get(requestMessage, false);
        if (skin != null) {
            skin.requestId = (SkinIdentifier) identifier;
            PacketHandler.networkWrapper.sendTo(new MessageServerSendSkinData((SkinIdentifier) identifier, getFullIdentifier(skin, identifier), skin), requestMessage.getPlayer());
        } else {
            ModLogger.log(Level.WARN, "Somehow failed to load a skin that we should have. ID was " + id);
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
        cacheLocalDatabase.add(skin);
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
    
    public Skin getSkin(ISkinPointer skinPointer) {
        return getSkin(skinPointer.getIdentifier());
    }
    
    public Skin getSkin(ISkinIdentifier identifier) {
        return cacheLocalDatabase.get(identifier, false);
    }
    
    /** Returns a skin if it is in the cache. If not the skin will be loaded by another thread. */
    public Skin softGetSkin(ISkinIdentifier identifier) {
        return cacheLocalDatabase.get(identifier, true);
    }
    
    public int size() {
        return cacheLocalDatabase.size();
    }
    
    public int fileLinkSize() {
        synchronized (cacheMapFileLink) {
            return cacheMapFileLink.size();
        }
    }
    
    public int globalLinkSize() {
        synchronized (cacheMapGlobalLink) {
            return cacheMapGlobalLink.size();
        }
    }

    @Override
    public void itemExpired(Skin mapItem) {
        if (mapItem == null) {
            return;
        }
        synchronized (cacheMapFileLink) {
            synchronized (cacheMapGlobalLink) {
                int skinId = mapItem.lightHash();
                //ModLogger.log("Removing local cache skin. " + skinId);
                if (cacheMapFileLink.containsValue(skinId)) {
                    ILibraryFile libraryFile = cacheMapFileLink.getBackward(skinId);
                    //ModLogger.log("Removing library cache skin. " + libraryFile.getFullName());
                    cacheMapFileLink.remove(libraryFile);
                }
                if (cacheMapGlobalLink.containsValue(skinId)) {
                    int globalId = cacheMapGlobalLink.getBackward(skinId);
                    //ModLogger.log("Removing global cache skin. " + globalId);
                    cacheMapGlobalLink.remove(globalId);
                }
            }
        }
    }
}
