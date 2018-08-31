package riskyken.armourers_workshop.common.library.global.auth;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.Level;

import net.minecraft.util.Session;
import riskyken.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import riskyken.armourers_workshop.utils.ModLogger;

public class MinecraftAuth {
    
    //Based on https://github.com/kihira/Tails/blob/7196d6156d3eaae5725e38e04e6dbb6f0b9ba705/src/main/java/uk/kihira/tails/client/CloudManager.java
    
    private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";
    
    private static long lastAuthTime;
    
    public static boolean checkAndRefeshAuth(Session session, String serverId) {
        if (lastAuthTime + 30000L > System.currentTimeMillis()) {
            ModLogger.log("skipping mc auth");
            return true;
        }
        ModLogger.log(Level.INFO, "MC Auth start");
        HttpURLConnection conn = null;
        String data = "{\"accessToken\":\"" + session.getToken() + "\", \"serverId\":\"" + serverId + "\", \"selectedProfile\":\"" + session.getPlayerID() + "\"}";
        
        try {
            String result = GlobalSkinLibraryUtils.performPostRequest(new URL(JOIN_URL), data, "application/json");
            lastAuthTime = System.currentTimeMillis();
            return true;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
        // returns non 204 if error occurred
    }
    
}
