package moe.plushie.armourers_workshop.common.library.global;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.common.library.global.DownloadUtils.DownloadJsonCallable;
import moe.plushie.armourers_workshop.common.library.global.DownloadUtils.DownloadJsonObjectCallable;
import moe.plushie.armourers_workshop.common.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.utils.ModLogger;

public final class GlobalSkinLibraryUtils {
    
    private static final String BASE_URL = "http://plushie.moe/armourers_workshop/";
    private static final String USER_INFO_URL = BASE_URL + "user-info.php";
    private static final String USER_SKINS_URL = BASE_URL + "user-skins.php";
    private static final String USER_SKIN_EDIT_URL = BASE_URL + "user-skin-edit.php";
    private static final String USER_SKIN_DELETE_URL = BASE_URL + "user-skin-delete.php";
    
    private static final Executor JSON_DOWNLOAD_EXECUTOR = Executors.newFixedThreadPool(1);
    private static final HashMap<Integer, PlushieUser> USERS = new HashMap<Integer, PlushieUser>();
    private static final HashSet<Integer> DOWNLOADED_USERS = new HashSet<Integer>();
    
    private GlobalSkinLibraryUtils() {}
    
    public static FutureTask<JsonArray> getUserSkinsList(Executor executor, int userId) {
        Validate.notNull(executor);
        Validate.notNull(userId);
        String searchUrl = USER_SKINS_URL + "?userId=" + String.valueOf(userId) + "&maxFileVersion=" + String.valueOf(SkinSerializer.MAX_FILE_VERSION);
        FutureTask<JsonArray> futureTask = new FutureTask<JsonArray>(new DownloadJsonCallable(searchUrl));
        executor.execute(futureTask);
        return futureTask;
    }
    
    public static FutureTask<JsonObject> deleteSkin(Executor executor, int userId, String accessToken, int skinId) {
        Validate.notNull(executor);
        Validate.notNull(userId);
        Validate.notNull(accessToken);
        String searchUrl = USER_SKIN_DELETE_URL + "?userId=" + String.valueOf(userId) + "&accessToken=" + accessToken + "&skinId=" + String.valueOf(skinId);
        FutureTask<JsonObject> futureTask = new FutureTask<JsonObject>(new DownloadJsonObjectCallable(searchUrl));
        executor.execute(futureTask);
        return futureTask;
    }
    
    public static FutureTask<JsonObject> editSkin(Executor executor, int userId, String accessToken, int skinId, String name, String description) {
        Validate.notNull(executor);
        Validate.notNull(userId);
        Validate.notNull(accessToken);
        String searchUrl = USER_SKIN_EDIT_URL + "?userId=" + String.valueOf(userId) + "&accessToken=" + accessToken + "&skinId=" + String.valueOf(skinId);
        MultipartForm multipartForm = new MultipartForm(searchUrl);
        multipartForm.addText("name", name);
        multipartForm.addText("description", description);
        FutureTask<JsonObject> futureTask = new FutureTask<JsonObject>(new PostMultipartFormCallable(multipartForm));
        executor.execute(futureTask);
        return futureTask;
    }
    
    public static class PostMultipartFormCallable implements Callable<JsonObject> {

        private final MultipartForm multipartForm;
        
        public PostMultipartFormCallable(MultipartForm multipartForm) {
            this.multipartForm = multipartForm;
        }
        
        @Override
        public JsonObject call() throws Exception {
            JsonObject result = null;
            try {
                String downloadData = multipartForm.upload();
                ModLogger.log(downloadData);
                result = (JsonObject) new JsonParser().parse(downloadData);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    
    public static PlushieUser getUserInfo(int userId) {
        synchronized (USERS) {
            if (USERS.containsKey(userId)) {
                return USERS.get(userId);
            }
        }
        if (!DOWNLOADED_USERS.contains(userId)) {
            DOWNLOADED_USERS.add(userId);
            JSON_DOWNLOAD_EXECUTOR.execute(new DownloadUserCallable(userId));
        }
        return null;
    }
    
    public static int[] getJavaVersion() {
        int[] version = new int[] {6, 0};
        try {
            String java = System.getProperty("java.version");
            String[] javaSplit = java.split("_");
            int javaVersion = Integer.valueOf(javaSplit[1]);
            version[1] = javaVersion;
            javaSplit = javaSplit[0].split("\\.");
            version[0] = Integer.valueOf(javaSplit[1]);
        } catch (Exception e) {
        }
        return version;
    }
    
    public static boolean isValidJavaVersion(int[] javaVersion) {
        if (javaVersion[0] < 8) {
            return false;
        }
        if (javaVersion[1] < 101) {
            return false;
        }
        return true;
    }
    
    public static boolean isValidJavaVersion() {
        int[] javaVersion = getJavaVersion();
        if (javaVersion[0] < 8 & javaVersion[1] < 101) {
            return false;
        }
        return true;
    }
    
    
    public static class DownloadUserCallable implements Runnable {

        private final int userId;
        
        public DownloadUserCallable(int userId) {
            this.userId = userId;
        }

        @Override
        public void run() {
            JsonObject json = DownloadUtils.downloadJsonObject(USER_INFO_URL + "?userId=" + userId);
            PlushieUser plushieUser = PlushieUser.readPlushieUser(json);
            if (plushieUser != null) {
                synchronized (USERS) {
                    USERS.put(userId, plushieUser);
                }
            } else {
                ModLogger.log(Level.ERROR, "Failed downloading info for user id: " + userId);
            }
        }
    }
    
    public static String performPostRequest(URL url, String post, String contentType) throws IOException {
        Validate.notNull(url);
        Validate.notNull(post);
        Validate.notNull(contentType);
        HttpURLConnection connection = createUrlConnection(url);
        byte[] postAsBytes = post.getBytes(Charsets.UTF_8);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
        connection.setDoOutput(true);
        
        OutputStream outputStream = null;
        try {
            outputStream = connection.getOutputStream();
            IOUtils.write(postAsBytes, outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            String result = IOUtils.toString(inputStream, Charsets.UTF_8);
            return result;
        } catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null) {
                String result = IOUtils.toString(inputStream, Charsets.UTF_8);
                return result;
            } else {
                throw e;
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    private static HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }
}
