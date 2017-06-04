package riskyken.armourersWorkshop.common.library.global;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

public final class SkinDownloader {
    
    public static void downloadSkins(CompletionService<Skin> skinCompletion, JsonArray json) {
        for (int i = 0; i < json.size(); i++) {
            JsonObject obj = json.get(i).getAsJsonObject();
            String name = obj.get("file_name").getAsString();
            int serverId = obj.get("id").getAsInt();
            skinCompletion.submit(new DownloadSkinCallable(name, serverId));
        }
    }
    
    /**
     * Downloads a fresh skins from the server.
     * @param name
     * @param serverId
     * @return
     */
    public static Skin downloadSkin(String fileName, int serverId) {
        Skin skin = null;
        
        long startTime = System.currentTimeMillis();
        long maxRate = 100;
        
        ModLogger.log(String.format("Downloading skin: %s", fileName));
        InputStream in = null;
        String data = null;
        try {
            in = new URL(String.format("http://plushie.moe/armourers_workshop/download-skin.php?skinid=%d&skinFileName=%s", serverId, fileName)).openStream();
            skin = SkinIOUtils.loadSkinFromStream(new BufferedInputStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        
        long waitTime = maxRate - (System.currentTimeMillis() - startTime);
        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (skin != null) {
            skin.serverId = serverId;
        } else {
            ModLogger.log("Failed to download skin.");
        }
        return skin;
    }
    
    public static class DownloadSkinCallable implements Callable<Skin> {
        
        private final String name;
        private final int serverId;
        
        public DownloadSkinCallable(String name, int serverId) {
            this.name = name;
            this.serverId = serverId;
        }
        
        @Override
        public Skin call() throws Exception {
            Skin skin = downloadSkin(name, serverId);
            return skin;
        }
        
        private Skin downloadSkin(String name, int serverId) throws InterruptedException {
            //Check if we already have the skin in the cache.
            Skin skin = ClientSkinCache.INSTANCE.getSkinFromServerId(serverId);
            if (skin != null) {
                skin.serverId = serverId;
                return skin;
            }
            
            long startTime = System.currentTimeMillis();
            long maxRate = 50;
            
            ModLogger.log(String.format("Downloading skin: %s", name));
            InputStream in = null;
            String data = null;
            try {
                in = new URL("http://plushie.moe/armourers_workshop/skins/" + name).openStream();
                skin = SkinIOUtils.loadSkinFromStream(new BufferedInputStream(in));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(in);
            }
            
            long waitTime = maxRate - (System.currentTimeMillis() - startTime);
            if (waitTime > 0) {
                Thread.sleep(waitTime);
            }
            
            if (skin != null) {
                skin.serverId = serverId;
            } else {
                ModLogger.log("Failed to download skin.");
            }
            return skin;
        }
    }
}
