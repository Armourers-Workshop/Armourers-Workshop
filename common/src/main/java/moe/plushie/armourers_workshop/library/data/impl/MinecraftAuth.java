package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import moe.plushie.armourers_workshop.init.ModLog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * https://minecraft.wiki/w/Mojang_API
 */
public class MinecraftAuth {

    // Based on
    // https://github.com/kihira/Tails/blob/7196d6156d3eaae5725e38e04e6dbb6f0b9ba705/src/main/java/uk/kihira/tails/client/CloudManager.java

    private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";
    private static final Object MC_AUTH_LOCK = new Object();

    private static long lastAuthTime;
    private static Exception lastAuthError;

    private static UserProvider USER_PROVIDER;

    public static void init(UserProvider userProvider) {
        USER_PROVIDER = userProvider;
    }

    public static boolean checkAndRefeshAuth(String serverId) {
        synchronized (MC_AUTH_LOCK) {
            if (lastAuthTime + 30000L > System.currentTimeMillis()) {
                ModLog.debug("skipping mc auth");
                return true;
            }
            if (USER_PROVIDER == null) {
                ModLog.debug("pls call init before!!!");
                lastAuthError = new RuntimeException("pls call init before!!!");
                return false;
            }
            ModLog.info("MC Auth Start");
            var data = "{\"accessToken\":\"" + USER_PROVIDER.accessToken() + "\", \"serverId\":\"" + serverId + "\", \"selectedProfile\":\"" + USER_PROVIDER.id() + "\"}";

            try {
                // returns non 204 if error occurred
                var result = performPostRequest(new URL(JOIN_URL), data, "application/json");
                if (result != null && !result.isEmpty()) {
                    var object = JsonSerializer.readFromString(result);
                    if (object.get("error") != null) {
                        throw new RuntimeException(object.get("error").stringValue());
                    }
                }
                lastAuthTime = System.currentTimeMillis();
                return true;
            } catch (Exception e) {
                lastAuthError = e;
                return false;
            }
        }
    }

    public static Exception getLastError() {
        return lastAuthError;
    }

    private static String performPostRequest(URL url, String post, String contentType) throws IOException {
        var connection = createUrlConnection(url);
        var postAsBytes = post.getBytes(StandardCharsets.UTF_8);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
        connection.setDoOutput(true);

        try (var outputStream = connection.getOutputStream()) {
            outputStream.write(postAsBytes);
        }

        try (var inputStream = connection.getInputStream()) {
            return StreamUtils.readStreamToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            var inputStream = connection.getErrorStream();
            if (inputStream != null) {
                return StreamUtils.readStreamToString(inputStream, StandardCharsets.UTF_8);
            } else {
                throw e;
            }
        }
    }

    private static HttpURLConnection createUrlConnection(URL url) throws IOException {
        var connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    public interface UserProvider {

        String id();

        String name();

        String accessToken();
    }
}
