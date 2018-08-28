package riskyken.armourersWorkshop.common.library.global.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import riskyken.armourersWorkshop.common.library.global.DownloadUtils;
import riskyken.armourersWorkshop.common.library.global.DownloadUtils.DownloadJsonObjectCallable;
import riskyken.armourersWorkshop.utils.ModLogger;

public class PlushieAuth {
    
    private static final Executor JSON_DOWNLOAD_EXECUTOR = Executors.newFixedThreadPool(1);
    public static final  PlushieSession PLUSHIE_SESSION = new PlushieSession();
    
    private static final String BASE_URL = "https://plushie.moe/armourers_workshop/";
    private static final String AUTH_URL = BASE_URL + "authentication.php";
    private static final String BETA_CHECK_URL = BASE_URL + "beta-check.php";
    private static final String BETA_JOIN_URL = BASE_URL + "beta-join.php";
    private static final String BETA_CODE_CHECK_URL = BASE_URL + "beta-code-check.php";
    
    private static boolean doneRemoteUserCheck = false;
    private static boolean startedRemoteUserCheck = false;
    private static boolean isRemoteUser = false;
    
    private static FutureTask<JsonObject> taskBetaCheck;
    
    public static boolean isRemoteUser() {
        return isRemoteUser;
    }
    
    public static boolean doneRemoteUserCheck() {
        return doneRemoteUserCheck;
    }
    
    public static boolean startedRemoteUserCheck() {
        return startedRemoteUserCheck;
    }
    
    public static void doRemoteUserCheck() {
        startedRemoteUserCheck = true;
        GameProfile gameProfile = Minecraft.getMinecraft().player.getGameProfile();
        taskBetaCheck = PlushieAuth.isPlayerInBeta(gameProfile.getId());
    }
    
    public static void taskCheck() {
        if (taskBetaCheck != null && taskBetaCheck.isDone()) {
            try {
                JsonObject jsonObject = taskBetaCheck.get();
                if (jsonObject.has("action") && jsonObject.get("action").getAsString().equals("beta-check")) {
                    if (jsonObject.has("valid") && jsonObject.get("valid").getAsBoolean() == true) {
                        if (jsonObject.has("id")) {
                            int serverId = jsonObject.get("id").getAsInt();
                            PLUSHIE_SESSION.setServerId(serverId);
                        }
                        if (jsonObject.has("permission_group_id")) {
                            PLUSHIE_SESSION.setPermission_group_id(jsonObject.get("permission_group_id").getAsInt());
                        }
                        isRemoteUser = true;
                    }
                }
            } catch (Exception e) {
                ModLogger.log("Failed beta check.");
                e.printStackTrace();
            }
            doneRemoteUserCheck = true;
            taskBetaCheck = null;
        }
    }
    
    public static FutureTask<JsonObject> isPlayerInBeta(UUID uuid) {
        String searchUrl;
        try {
            searchUrl = BETA_CHECK_URL + "?uuid=" + URLEncoder.encode(uuid.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        FutureTask<JsonObject> futureTask = new FutureTask<JsonObject>(new DownloadJsonObjectCallable(searchUrl));
        JSON_DOWNLOAD_EXECUTOR.execute(futureTask);
        return futureTask;
    }
    
    public static FutureTask<JsonObject> checkBetaCode(UUID uuid) {
        String searchUrl;
        try {
            searchUrl = BETA_CODE_CHECK_URL + "?code=" + URLEncoder.encode(uuid.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        FutureTask<JsonObject> futureTask = new FutureTask<JsonObject>(new DownloadJsonObjectCallable(searchUrl));
        JSON_DOWNLOAD_EXECUTOR.execute(futureTask);
        return futureTask;
    }
    
    public static FutureTask<JsonObject> joinBeta(String username, String uuid, String betaCode) {
        FutureTask<JsonObject> futureTask = new FutureTask<JsonObject>(new JoinBetaCallable(username, uuid, betaCode));
        JSON_DOWNLOAD_EXECUTOR.execute(futureTask);
        return futureTask;
    }
    
    public static class JoinBetaCallable implements Callable<JsonObject> {

        private String username;
        private String uuid;
        private String betaCode;
        
        public JoinBetaCallable(String username, String uuid, String betaCode) {
            this.username = username;
            this.uuid = uuid;
            this.betaCode = betaCode;
        }
        
        @Override
        public JsonObject call() throws Exception {
            String serverId = String.valueOf(BASE_URL.hashCode());
            
            if (!MinecraftAuth.checkAndRefeshAuth(Minecraft.getMinecraft().getSession(), serverId)) {
                ModLogger.log("Failed MC Auth");
                return null;
            } else {
                ModLogger.log("MC Auth Done");
            }
            
            String url;
            try {
                url = BETA_JOIN_URL + "?username=" + URLEncoder.encode(username, "UTF-8") + "&uuid=" + URLEncoder.encode(uuid, "UTF-8") + "&serverId=" + URLEncoder.encode(serverId, "UTF-8") + "&betaCode=" + URLEncoder.encode(betaCode, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
                return null;
            }
            
            try {
                ModLogger.log(url);
            } catch (Exception e) {
                // TODO: handle exception
            }
            String data = DownloadUtils.downloadString(url);
            JsonObject json = null;
            try {
                json = (JsonObject) new JsonParser().parse(data);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return json;
        }
    }
    
    public static JsonObject updateAccessToken(String username, String uuid) {
        String serverId = String.valueOf(BASE_URL.hashCode());
        
        if (!MinecraftAuth.checkAndRefeshAuth(Minecraft.getMinecraft().getSession(), serverId)) {
            ModLogger.log("Failed MC Auth");
            return null;
        } else {
            ModLogger.log("MC Auth Done");
        }
        
        
        
        String url;
        try {
            url = AUTH_URL + "?username=" + URLEncoder.encode(username, "UTF-8") + "&uuid=" + URLEncoder.encode(uuid, "UTF-8") + "&serverId=" + URLEncoder.encode(serverId, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
        
        try {
            ModLogger.log(url);
        } catch (Exception e) {
            // TODO: handle exception
        }
        String data = DownloadUtils.downloadString(url);
        ModLogger.log(data);
        JsonObject json = null;
        try {
            json = (JsonObject) new JsonParser().parse(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
}
