package riskyken.armourersWorkshop.common.library.global.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.google.gson.JsonArray;

import riskyken.armourersWorkshop.common.library.global.DownloadUtils.DownloadJsonCallable;

public class PlushieAuth {
    
    private static final Executor JCON_DOWNLOAD_EXECUTOR = Executors.newFixedThreadPool(1);
    
    private static final String AUTH_URL = "https://plushie.moe/armourers_workshop/authentication.php";
    private static final String BETA_CHECK_URL = "https://plushie.moe/armourers_workshop/beta-check.php";
    
    public static FutureTask<JsonArray> isPlayerInBeta(UUID uuid) {
        String searchUrl;
        try {
            searchUrl = BETA_CHECK_URL + "?uuid=" + URLEncoder.encode(uuid.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        FutureTask<JsonArray> futureTask = new FutureTask<JsonArray>(new DownloadJsonCallable(searchUrl));
        JCON_DOWNLOAD_EXECUTOR.execute(futureTask);
        return futureTask;
    }
}
