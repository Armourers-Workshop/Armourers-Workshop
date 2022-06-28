package moe.plushie.armourers_workshop.library.data.global;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class DownloadUtils {
    
    private DownloadUtils() {
    }
    
    public static String downloadString(String url) {
        InputStream in = null;
        String data = null;
        try {
            in = new URL(url).openStream();
            data = StreamUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(in);
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
            ModLog.debug(data);
            e.printStackTrace();
            return null;
        }
        return json;
    }
}
