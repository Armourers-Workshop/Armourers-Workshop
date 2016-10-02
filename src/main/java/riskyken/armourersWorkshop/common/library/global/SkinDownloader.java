package riskyken.armourersWorkshop.common.library.global;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

public final class SkinDownloader implements Runnable  {
    
    private IDownloadListCallback listCallback = null;
    private IDownloadSkinCallback skinCallback = null;
    private ArrayList<String> downloadedModels = new ArrayList<String>();
    private String url;
    private JsonArray json;
    
    private SkinDownloader(IDownloadListCallback callback, String url) {
        this.listCallback = callback;
        this.url = url;
    }
    
    private SkinDownloader(IDownloadSkinCallback callback, JsonArray json) {
        this.skinCallback = callback;
        this.json = json;
    }
    
    public static void downloadJson(IDownloadListCallback callback, String url) {
        (new Thread(new SkinDownloader(callback, url), LibModInfo.NAME + " download thread.")).start();
    }
    
    public static void downloadSkins(IDownloadSkinCallback callback, JsonArray json) {
        (new Thread(new SkinDownloader(callback, json), LibModInfo.NAME + " download thread.")).start();
    }
    
    private void downloadSkinList() {
        ModLogger.log("Download Test Started");
        InputStream in = null;
        String data = null;
        try {
            in = new URL(url).openStream();
            data = IOUtils.toString(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        
        if (data == null) {
            return;
        }
        
        JsonArray json = null;
        try {
            json = (JsonArray) new JsonParser().parse(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (json == null) {
            return;
        }
        
        if (json != null & listCallback != null) {
            listCallback.listDownloadFinished(json);
        }
        //ModLogger.log(json);
        
        ModLogger.log("Download Test Finished");
    }
    
    private void downloadSkins() {
        for (int i = 0; i < json.size(); i++) {
            JsonObject obj = json.get(i).getAsJsonObject();
            String name = obj.get("file_name").getAsString();
            int serverid = obj.get("id").getAsInt();
            if (!downloadedSkin(name)) {
                downloadSkin(name, serverid);
            }
        }
    }
    
    private boolean downloadedSkin(String name) {
        for (int i = 0; i < downloadedModels.size(); i++) {
            if (downloadedModels.get(i).equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    private void downloadSkin(String name, int serverId) {
        downloadedModels.add(name);
       
        
        Skin skin = null;
        
        //Check if we already have the skin in the cache.
        skin = ClientSkinCache.INSTANCE.getSkinFromServerId(serverId);
        if (skin != null) {
            skin.serverId = serverId;
            if (skinCallback != null) {
                skinCallback.skinDownloaded(skin, new SkinPointer(skin));
            }
            return;
        }
        
        ModLogger.log(String.format("Downloading skin %s", name));
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
                ModLogger.log("Downloaded skin " + skin.lightHash());
                skinCallback.skinDownloaded(skin, new SkinPointer(skin));
            }
        } else {
            ModLogger.log("Failed to download skin.");
        }
    }
    
    @Override
    public void run() {
        if (listCallback != null) {
            downloadSkinList();
        }
        if (skinCallback != null & json != null) {
            downloadSkins();
        }
    }
    
    public static interface IDownloadListCallback {
        public void listDownloadFinished(JsonArray json);
    }
    
    public static interface IDownloadSkinCallback {
        public void skinDownloaded(Skin skin, SkinPointer skinPointer);
    }
}
