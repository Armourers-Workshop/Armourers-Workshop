package moe.plushie.armourers_workshop.common.library.global.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.common.library.global.DownloadUtils;
import moe.plushie.armourers_workshop.common.library.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.common.library.global.PlushieUser;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaCheck;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;

public class PlushieAuth {

    private static final Executor JSON_DOWNLOAD_EXECUTOR = Executors.newFixedThreadPool(1);
    public static final PlushieSession PLUSHIE_SESSION = new PlushieSession();

    private static final String BASE_URL = "https://plushie.moe/armourers_workshop/";
    private static final String AUTH_URL = BASE_URL + "authentication.php";
    private static final String USER_TOKEN_UPDATE = BASE_URL + "user-token-update.php?userId=%d&accessToken=%s";

    private static boolean doneRemoteUserCheck = false;
    private static boolean startedRemoteUserCheck = false;
    private static boolean isRemoteUser = false;

    private static final int TOKEN_UPDATE_TIME = 60 * 1000;
    private static volatile boolean updatingToken = false;

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
        if (!GlobalSkinLibraryUtils.isValidJavaVersion()) {
            isRemoteUser = false;
            doneRemoteUserCheck = true;
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        GameProfile gameProfile = mc.player.getGameProfile();
        new GlobalTaskBetaCheck(gameProfile.getId()).createTaskAndRun(new FutureCallback<PlushieUser>() {

            @Override
            public void onSuccess(PlushieUser result) {
                mc.addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        PLUSHIE_SESSION.setServerId(result.getId());
                        PLUSHIE_SESSION.setPermission_group_id(result.getPermissionGroupId());
                        isRemoteUser = true;
                        doneRemoteUserCheck = true;
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                mc.addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        isRemoteUser = false;
                        doneRemoteUserCheck = true;
                    }
                });
            }
        });
    }

    public static JsonObject authenticateUser(String username, String uuid) {
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

    public static void updateAccessToken() {
        if (PLUSHIE_SESSION.isAuthenticated() & !updatingToken & PLUSHIE_SESSION.getTokenExpiryTime() < TOKEN_UPDATE_TIME) {
            updatingToken = true;
            try {
                ModLogger.log("Getting new token. Time left: " + (PLUSHIE_SESSION.getTokenExpiryTime() / 1000));
                String url = String.format(USER_TOKEN_UPDATE, PLUSHIE_SESSION.getServerId(), PLUSHIE_SESSION.getAccessToken());
                new Thread(new TokenUpdate(url)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class TokenUpdate implements Runnable {

        private final String url;

        public TokenUpdate(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                JsonObject json = DownloadUtils.downloadJsonObject(url);
                ModLogger.log("update token: " + json);
                PLUSHIE_SESSION.updateToken(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            updatingToken = false;
        }

    }
}
