package riskyken.armourersWorkshop.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.ArmourersWorkshop;

public final class GameProfileUtils {
    
    private static final ExecutorService profileDownloader = Executors.newFixedThreadPool(2);
    private static final HashMap<String, GameProfile> downloadedCache = new HashMap<String, GameProfile>();
    private static final Cache<String, Boolean> submitted = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
    
    public static GameProfile getGameProfile(String name, IGameProfileCallback callback) {
        if (StringUtils.isNullOrEmpty(name)) {
            return null;
        }
        return getGameProfile(new GameProfile(null, name), callback);
    }
    
    public static GameProfile getGameProfile(GameProfile gameProfile, IGameProfileCallback callback) {
        if (gameProfile == null) {
            return null;
        }
        if (StringUtils.isNullOrEmpty(gameProfile.getName())) {
            return null;
        }
        
        GameProfile cachedProfile = null;
        synchronized (downloadedCache) {
            cachedProfile = downloadedCache.get(gameProfile.getName());
        }
        if (cachedProfile != null) {
            return cachedProfile;
        }
        
        synchronized (submitted) {
            if (!submitted.asMap().containsKey(gameProfile.getName())) {
                submitted.put(gameProfile.getName(), true);
                profileDownloader.submit(new ProfileDownloadThread(gameProfile, callback));
            }
        }

        return null;
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
            }
        }
        
        private GameProfile fillProfileProperties(GameProfile gameProfile) {
            if (!gameProfile.isComplete()) {
                gameProfile = ArmourersWorkshop.getProxy().getServer().func_152358_ax().func_152655_a(gameProfile.getName());
            }
            
            if (gameProfile == null) {
                return null;
            }
            
            if (gameProfile.isComplete()) {
                Minecraft minecraft = Minecraft.getMinecraft();
                Map map = minecraft.func_152342_ad().func_152788_a(gameProfile);
                if (!gameProfile.getProperties().containsKey("textures")) {
                    Property property = (Property)Iterables.getFirst(gameProfile.getProperties().get("textures"), (Object)null);
                    if (property == null) {
                        gameProfile = ArmourersWorkshop.getProxy().getServer().func_147130_as().fillProfileProperties(gameProfile, false);
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
