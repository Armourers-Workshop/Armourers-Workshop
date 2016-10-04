package riskyken.armourersWorkshop.common.library.global;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

public final class SkinDownloader implements Runnable  {
    
    private IDownloadSkinCallback skinCallback = null;
    private JsonArray json;
    
    private SkinDownloader(IDownloadSkinCallback callback, JsonArray json) {
        this.skinCallback = callback;
        this.json = json;
    }
    
    public static void downloadSkins(IDownloadSkinCallback callback, JsonArray json) {
        (new Thread(new SkinDownloader(callback, json), LibModInfo.NAME + " download thread.")).start();
    }
    
    private void downloadSkins() {
        for (int i = 0; i < json.size(); i++) {
            JsonObject obj = json.get(i).getAsJsonObject();
            String name = obj.get("file_name").getAsString();
            int serverid = obj.get("id").getAsInt();
            downloadSkin(name, serverid);
        }
    }
    
    private void downloadSkin(String name, int serverId) {
        Skin skin = ClientSkinCache.INSTANCE.getSkinFromServerId(serverId);
        
        //Check if we already have the skin in the cache.
        if (skin != null) {
            skin.serverId = serverId;
            if (skinCallback != null) {
                skinCallback.skinDownloaded(skin, new SkinPointer(skin));
            }
            return;
        }
        
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
        
        if (skin != null) {
            skin.serverId = serverId;
            if (skinCallback != null) {
                ModLogger.log(String.format("Downloaded skin:  %s - id: %d", name, skin.lightHash()));
                skinCallback.skinDownloaded(skin, new SkinPointer(skin));
            }
        } else {
            ModLogger.log("Failed to download skin.");
        }
    }
    
    @Override
    public void run() {
        if (skinCallback != null & json != null) {
            downloadSkins();
        }
    }
    
    public class DownloadSkinCallback implements Callable<Skin> {

        private final String url;
        
        public DownloadSkinCallback(String url) {
            this.url = url;
        }
        
        @Override
        public Skin call() throws Exception {
            ModLogger.log(Thread.currentThread().getName());
            //downloadSkinList(url);
            return null;
        }
    }
    
    public static interface IDownloadSkinCallback {
        public void skinDownloaded(Skin skin, SkinPointer skinPointer);
    }
}
