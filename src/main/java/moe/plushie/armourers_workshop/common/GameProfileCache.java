package moe.plushie.armourers_workshop.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientRequestGameProfile;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerGameProfile;
import moe.plushie.armourers_workshop.proxies.CommonProxy;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StringUtils;

public final class GameProfileCache {
    
    private static final ExecutorService profileDownloader = Executors.newFixedThreadPool(2);
    private static final HashMap<String, GameProfile> downloadedCache = new HashMap<String, GameProfile>();
    private static final Cache<String, Boolean> submitted = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    private static final ArrayList<WaitingClient> waitingClients = new ArrayList<WaitingClient>();
    
    private static final Cache<String, GameProfile> clientProfileCache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    private static final Cache<String, Boolean> clientRequests = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
    
    public static GameProfile getGameProfileClient(String name, IGameProfileCallback callback) {
        if (StringUtils.isNullOrEmpty(name)) {
            return null;
        }
        synchronized (clientProfileCache) {
            GameProfile gameProfile = clientProfileCache.getIfPresent(name);
            if (gameProfile != null) {
                return gameProfile;
            }
        }
        synchronized (clientRequests) {
            if (!clientRequests.asMap().containsKey(name)) {
                clientRequests.put(name, true);
                PacketHandler.networkWrapper.sendToServer(new MessageClientRequestGameProfile(new GameProfile(null, name)));
            }
        }
        return null;
    }
    
    public static GameProfile getGameProfile(GameProfile gameProfile, IGameProfileCallback callback) {
        if (gameProfile == null) {
            return null;
        }
        if (StringUtils.isNullOrEmpty(gameProfile.getName())) {
            return null;
        }
        
        CommonProxy proxy = ArmourersWorkshop.getProxy();
        if (proxy.isLocalPlayer(gameProfile.getName())) {
            if (proxy.haveFullLocalProfile()) {
                if (callback == null) {
                    return proxy.getLocalGameProfile();
                } else {
                    callback.profileDownloaded(proxy.getLocalGameProfile());
                    return null;
                }
            }
            return null;
        }
        
        GameProfile cachedProfile = null;
        synchronized (downloadedCache) {
            cachedProfile = downloadedCache.get(gameProfile.getName());
        }
        if (cachedProfile != null) {
            if (callback == null) {
                return cachedProfile;
            } else {
                callback.profileDownloaded(cachedProfile);
            }
        }
        
        synchronized (submitted) {
            if (!submitted.asMap().containsKey(gameProfile.getName())) {
                submitted.put(gameProfile.getName(), true);
                profileDownloader.submit(new ProfileDownloadThread(gameProfile, callback));
            }
        }

        return null;
    }
    
    public static void onServerSentProfile(GameProfile gameProfile) {
        synchronized (clientProfileCache) {
            clientProfileCache.put(gameProfile.getName(), gameProfile);
        }
    }
    
    public static void onClientRequstProfile(EntityPlayerMP playerEntity, GameProfile gameProfile) {
        synchronized (waitingClients) {
            GameProfile cacheProfile = downloadedCache.get(gameProfile.getName());
            if (cacheProfile != null) {
                sendProfileToClient(playerEntity, cacheProfile);
                return;
            } else {
                waitingClients.add(new WaitingClient(playerEntity, gameProfile.getName()));
            }
        }
        getGameProfile(gameProfile, null);
    }
    
    private static void sendProfileToClient(EntityPlayerMP playerEntity, GameProfile gameProfile) {
        PacketHandler.networkWrapper.sendTo(new MessageServerGameProfile(gameProfile), playerEntity);
    }
    
    public static class WaitingClient {
        
        private EntityPlayerMP entityPlayer;
        private String profileName;
        
        public WaitingClient(EntityPlayerMP entityPlayer, String profileName) {
            this.entityPlayer = entityPlayer;
            this.profileName = profileName;
        }
        
        public EntityPlayerMP getEntityPlayer() {
            return entityPlayer;
        }
        
        public String getProfileName() {
            return profileName;
        }
    }
    
    public static class ProfileDownloadThread implements Runnable {

        private GameProfile gameProfile;
        private IGameProfileCallback callback;
        
        public ProfileDownloadThread(GameProfile gameProfile, IGameProfileCallback callback) {
            this.gameProfile = gameProfile;
            this.callback = callback;
        }
        
        @Override
        public void run() {
            GameProfile newProfile = fillProfileProperties(gameProfile);
            if (newProfile != null) {
                synchronized (downloadedCache) {
                    downloadedCache.put(newProfile.getName(), newProfile);
                }
                if (callback != null) {
                    callback.profileDownloaded(newProfile);
                }
                synchronized (waitingClients) {
                    for (int i = 0; i < waitingClients.size(); i++) {
                        WaitingClient wc = waitingClients.get(i);
                        if (wc.getProfileName().equals(newProfile.getName())) {
                            sendProfileToClient(wc.getEntityPlayer(), newProfile);
                            waitingClients.remove(i);
                        }
                    }
                }
            }
        }
        
        private GameProfile fillProfileProperties(GameProfile gameProfile) {
            
            if (!gameProfile.isComplete()) {
                gameProfile = ArmourersWorkshop.getProxy().getServer().getPlayerProfileCache().getGameProfileForUsername(gameProfile.getName());
            }
            if (gameProfile == null) {
                return null;
            }
            
            if (gameProfile.isComplete()) {
                if (!gameProfile.getProperties().containsKey("textures")) {
                    Property property = (Property)Iterables.getFirst(gameProfile.getProperties().get("textures"), (Object)null);
                    if (property == null) {
                        gameProfile = ArmourersWorkshop.getProxy().getServer().getMinecraftSessionService().fillProfileProperties(gameProfile, false);
                        return gameProfile;
                    }
                }
            }
            return gameProfile;
        }
    }
    
    public interface IGameProfileCallback {
        public void profileDownloaded(GameProfile gameProfile);
    }
}
