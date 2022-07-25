package moe.plushie.armourers_workshop.library.data.global.auth;

import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.client.User;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MinecraftAuth {

    // Based on
    // https://github.com/kihira/Tails/blob/7196d6156d3eaae5725e38e04e6dbb6f0b9ba705/src/main/java/uk/kihira/tails/client/CloudManager.java

    private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";
    private static final Object MC_AUTH_LOCK = new Object();
    private static long lastAuthTime;

    public static boolean checkAndRefeshAuth(User session, String serverId) {
        synchronized (MC_AUTH_LOCK) {
            if (lastAuthTime + 30000L > System.currentTimeMillis()) {
                ModLog.debug("skipping mc auth");
                return true;
            }
            ModLog.info("MC Auth start");
            HttpURLConnection conn = null;
            String data = "{\"accessToken\":\"" + session.getAccessToken() + "\", \"serverId\":\"" + serverId + "\", \"selectedProfile\":\"" + session.getUuid() + "\"}";

            try {
                String result = performPostRequest(new URL(JOIN_URL), data, "application/json");
                lastAuthTime = System.currentTimeMillis();
                return true;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }

        // returns non 204 if error occurred
    }

    private static String performPostRequest(URL url, String post, String contentType) throws IOException {
        Validate.notNull(url);
        Validate.notNull(post);
        Validate.notNull(contentType);
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
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }
}
