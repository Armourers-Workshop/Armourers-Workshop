package moe.plushie.armourers_workshop.common.library.global.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.util.Session;

public class MinecraftAuth {

    // Based on
    // https://github.com/kihira/Tails/blob/7196d6156d3eaae5725e38e04e6dbb6f0b9ba705/src/main/java/uk/kihira/tails/client/CloudManager.java

    private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";

    private static long lastAuthTime;
    
    private static final Object MC_AUTH_LOCK = new Object();

    public static boolean checkAndRefeshAuth(Session session, String serverId) {
        synchronized (MC_AUTH_LOCK) {
            if (lastAuthTime + 30000L > System.currentTimeMillis()) {
                ModLogger.log("skipping mc auth");
                return true;
            }
            ModLogger.log(Level.INFO, "MC Auth start");
            HttpURLConnection conn = null;
            String data = "{\"accessToken\":\"" + session.getToken() + "\", \"serverId\":\"" + serverId + "\", \"selectedProfile\":\"" + session.getPlayerID() + "\"}";

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
