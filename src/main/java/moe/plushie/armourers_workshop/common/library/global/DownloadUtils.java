package moe.plushie.armourers_workshop.common.library.global;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.utils.ModLogger;

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
    
    public static JsonObject downloadJsonObject(String url) {
        String data = downloadString(url);
        if (data == null) {
            return null;
        }
        JsonObject json = null;
        try {
            json = (JsonObject) new JsonParser().parse(data);
        } catch (Exception e) {
            ModLogger.log(data);
            e.printStackTrace();
            return null;
        }
        return json;
    }
}
