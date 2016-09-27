package riskyken.armourersWorkshop.common.library.global;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.ModLogger;

public final class SkinDownloader implements Runnable  {
    
    private IDownloadListCallback callback;
    
    private SkinDownloader(IDownloadListCallback callback) {
        this.callback = callback;
    }
    
    public static void downloadSkinList(IDownloadListCallback callback) {
        (new Thread(new SkinDownloader(callback), LibModInfo.NAME + " download thread.")).start();
    }
    
    @Override
    public void run() {
        ModLogger.log("Download Test Started");
        InputStream in = null;
        String data = null;
        try {
            in = new URL("http://plushie.moe/armourers_workshop/skin-list.php").openStream();
            data = IOUtils.toString( in );
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
        
        if (json != null & callback != null) {
            callback.listDownloadFinished(json);
        }
        ModLogger.log(json);
        
        ModLogger.log("Download Test Finished");
    }
    
    public interface IDownloadListCallback {
        public void listDownloadFinished(JsonObject json);
    }
}
