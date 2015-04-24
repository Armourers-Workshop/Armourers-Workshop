package riskyken.armourersWorkshop.utils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public final class GameProfileUtils {
    
    public static void updateProfileData(GameProfile gameProfile, IGameProfileCallback callback) {
        Thread t = (new Thread(new ProfileUpdateThread(gameProfile, callback),LibModInfo.NAME + " profile update thread."));
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }
    
    public static GameProfile getGameProfileForUserName(String userName) {
        GameProfile gameProfile;
        gameProfile = MinecraftServer.getServer().func_152358_ax().func_152655_a(userName);
        if (gameProfile == null) {
            ModLogger.log("profile was null");
            gameProfile = new GameProfile(null, userName);
        }
        return gameProfile;
    }
    
    public static class ProfileUpdateThread implements Runnable {

        private GameProfile gameProfile;
        private IGameProfileCallback callback;
        
        public ProfileUpdateThread(GameProfile gameProfile, IGameProfileCallback callback) {
            this.gameProfile = gameProfile;
            this.callback = callback;
        }
        
        @Override
        public void run() {
            if (this.gameProfile != null && !StringUtils.isNullOrEmpty(this.gameProfile.getName())) {
                if (!this.gameProfile.isComplete() || !this.gameProfile.getProperties().containsKey("textures")) {
                    GameProfile newGameProfile = MinecraftServer.getServer().func_152358_ax().func_152655_a(this.gameProfile.getName());
                    if (newGameProfile != null) {
                        Property property = (Property)Iterables.getFirst(newGameProfile.getProperties().get("textures"), (Object)null);
                        if (property == null) {
                            newGameProfile = MinecraftServer.getServer().func_147130_as().fillProfileProperties(newGameProfile, true);
                        }
                        if (callback != null) {
                            callback.profileUpdated(newGameProfile);
                        }
                    }
                }
            }
        }
    }
    
    public interface IGameProfileCallback {
        
        public void profileUpdated(GameProfile gameProfile);
    }
}
