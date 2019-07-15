package riskyken.armourersWorkshop.common.library.global;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import riskyken.armourersWorkshop.utils.ModLogger;

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

    public static JsonElement downloadJson(String url) {
        String data = downloadString(url);
        if (data == null) {
            return null;
        }
        JsonElement json = null;
        try {
            json = new JsonParser().parse(data);
        } catch (Exception e) {
            ModLogger.log(data);
            e.printStackTrace();
            return null;
        }
        return json;
    }

    public static class DownloadJsonCallable implements Callable<JsonArray> {

        private final String url;

        public DownloadJsonCallable(String url) {
            this.url = url;
        }

        @Override
        public JsonArray call() throws Exception {
            JsonArray array = downloadJsonArray(url);
            return array;
        }
    }

    public static class DownloadJsonObjectCallable implements Callable<JsonObject> {

        private final String url;

        public DownloadJsonObjectCallable(String url) {
            this.url = url;
        }

        @Override
        public JsonObject call() throws Exception {
            JsonObject array = downloadJsonObject(url);
            return array;
        }
    }

    public static class DownloadJsonMultipartForm implements Callable<JsonObject> {

        private final MultipartForm multipartForm;

        public DownloadJsonMultipartForm(MultipartForm multipartForm) {
            this.multipartForm = multipartForm;
        }

        @Override
        public JsonObject call() throws Exception {
            String data = multipartForm.upload();
            if (StringUtils.isEmpty(data)) {
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

    public static class DownloadJsonArrayMultipartForm implements Callable<JsonArray> {

        private final MultipartForm multipartForm;

        public DownloadJsonArrayMultipartForm(MultipartForm multipartForm) {
            this.multipartForm = multipartForm;
        }

        @Override
        public JsonArray call() throws Exception {
            String data = multipartForm.upload();
            if (StringUtils.isEmpty(data)) {
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

    public static class DownloadStringCallable implements Callable<String> {

        private final String url;

        public DownloadStringCallable(String url) {
            this.url = url;
        }

        @Override
        public String call() throws Exception {
            String download = downloadString(url);
            return download;
        }
    }
}