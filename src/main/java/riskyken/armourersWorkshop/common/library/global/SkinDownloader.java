package riskyken.armourersWorkshop.common.library.global;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

public final class SkinDownloader implements Runnable  {
    
    private IDownloadListCallback listCallback = null;
    private IDownloadSkinCallback skinCallback = null;
    private ArrayList<String> remoteSkins;
    private ArrayList<String> downloadedModels = new ArrayList<String>();
    
    private SkinDownloader(IDownloadListCallback callback) {
        this.listCallback = callback;
    }
    
    private SkinDownloader(IDownloadSkinCallback callback, ArrayList<String> remoteSkins) {
        this.skinCallback = callback;
        this.remoteSkins = remoteSkins;
    }
    
    public static void downloadSkinList(IDownloadListCallback callback) {
        (new Thread(new SkinDownloader(callback), LibModInfo.NAME + " download thread.")).start();
    }
    
    public static void downloadSkins(IDownloadSkinCallback callback, ArrayList<String> remoteSkins) {
        (new Thread(new SkinDownloader(callback, remoteSkins), LibModInfo.NAME + " download thread.")).start();
    }
    
    private void downloadSkinList() {
        ModLogger.log("Download Test Started");
        InputStream in = null;
        String data = null;
        try {
            in = new URL("http://plushie.moe/armourers_workshop/skin-list.php").openStream();
            data = IOUtils.toString(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        
        if (data == null) {
            return;
        }
        
        JsonObject json = null;
        try {
            json = (JsonObject) new JsonParser().parse(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (json == null) {
            return;
        }
        
        if (json != null & listCallback != null) {
            listCallback.listDownloadFinished(json);
        }
        ModLogger.log(json);
        
        ModLogger.log("Download Test Finished");
    }
    
    private void downloadSkins() {
        for (int i = 0; i < remoteSkins.size(); i++) {
            if (!downloadedSkin(remoteSkins.get(i))) {
                downloadSkin(remoteSkins.get(i));
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
    
    private void downloadSkin(String name) {
        downloadedModels.add(name);
        ModLogger.log(String.format("Downloading skin %s", name));
        
        InputStream in = null;
        String data = null;
        Skin skin = null;
        try {
            in = new URL("http://plushie.moe/armourers_workshop/" + name.replace(" ", "%20")).openStream();
            skin = SkinIOUtils.loadSkinFromStream(new BufferedInputStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        
        
        if (skin != null) {
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
        if (skinCallback != null & remoteSkins != null) {
            downloadSkins();
        }
    }
    
    public static interface IDownloadListCallback {
        public void listDownloadFinished(JsonObject json);
    }
    
    public static interface IDownloadSkinCallback {
        public void skinDownloaded(Skin skin, SkinPointer skinPointer);
    }
}
