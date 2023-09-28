package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class MinecraftAuth {

    // Based on
    // https://github.com/kihira/Tails/blob/7196d6156d3eaae5725e38e04e6dbb6f0b9ba705/src/main/java/uk/kihira/tails/client/CloudManager.java

    private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";
    private static final Object MC_AUTH_LOCK = new Object();

    private static long lastAuthTime;
    private static Exception lastAuthError;

    private static Supplier<String> USER_ID_PROVIDER;
    private static Supplier<String> USER_ACCESS_TOKEN_PROVIDER;

    public static void init(Supplier<String> idProvider, Supplier<String> accessTokenProvider) {
        USER_ID_PROVIDER = idProvider;
        USER_ACCESS_TOKEN_PROVIDER = accessTokenProvider;
    }

    public static boolean checkAndRefeshAuth(String serverId) {
        synchronized (MC_AUTH_LOCK) {
            if (lastAuthTime + 30000L > System.currentTimeMillis()) {
                ModLog.debug("skipping mc auth");
                return true;
            }
            if (USER_ID_PROVIDER == null || USER_ACCESS_TOKEN_PROVIDER == null) {
                ModLog.debug("pls call init before!!!");
                lastAuthError = new RuntimeException("pls call init before!!!");
                return false;
            }
            ModLog.info("MC Auth start");
            HttpURLConnection conn = null;
            String data = "{\"accessToken\":\"" + USER_ACCESS_TOKEN_PROVIDER.get() + "\", \"serverId\":\"" + serverId + "\", \"selectedProfile\":\"" + USER_ID_PROVIDER.get() + "\"}";

            try {
                // returns non 204 if error occurred
                String result = performPostRequest(new URL(JOIN_URL), data, "application/json");
                IDataPackObject object = StreamUtils.fromPackObject(result);
                if (object != null && object.get("error") != null) {
                    throw new RuntimeException(object.get("error").stringValue());
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
        HttpURLConnection connection = createUrlConnection(url);
        byte[] postAsBytes = post.getBytes(StandardCharsets.UTF_8);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
        connection.setDoOutput(true);

        OutputStream outputStream = null;
        try {
            outputStream = connection.getOutputStream();
            outputStream.write(postAsBytes);
        } finally {
            StreamUtils.closeQuietly(outputStream);
        }

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            return StreamUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            StreamUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null) {
                return StreamUtils.toString(inputStream, StandardCharsets.UTF_8);
            } else {
                throw e;
            }
        } finally {
            StreamUtils.closeQuietly(inputStream);
        }
    }

    private static HttpURLConnection createUrlConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }
}
