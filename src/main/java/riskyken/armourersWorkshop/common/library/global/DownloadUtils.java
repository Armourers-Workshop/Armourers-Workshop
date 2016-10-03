package riskyken.armourersWorkshop.common.library.global;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public final class DownloadUtils {
    
    private DownloadUtils() {
    }
    
    public static String downloadString(String url) {
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
        
        return data;
    }
    
    public static JsonArray downloadJsonArray(String url) {
        String data = downloadString(url);
        if (data == null) {
            return null;
        }
        JsonArray json = null;
        try {
            json = (JsonArray) new JsonParser().parse(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
}
