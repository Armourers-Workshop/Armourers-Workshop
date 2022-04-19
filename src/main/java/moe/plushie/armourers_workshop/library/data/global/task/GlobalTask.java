package moe.plushie.armourers_workshop.library.data.global.task;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class GlobalTask<V> implements Callable<V> {

    private static final Executor GLOBAL_TASK_EXECUTOR = Executors.newFixedThreadPool(2);

    private static final String URL_BASE = "plushie.moe/armourers_workshop/";
    private static final String URL_NORMAL = "http://" + URL_BASE;
    private static final String URL_SECURE = "https://" + URL_BASE;

    private final PermissionSystem.PlushieAction plushieAction;
    private final boolean needsSecure;

    public GlobalTask(PermissionSystem.PlushieAction plushieAction, boolean needsSecure) {
        this.plushieAction = plushieAction;
        this.needsSecure = needsSecure;
    }

    public boolean isNeedsSecure() {
        return needsSecure;
    }

    public PermissionSystem.PlushieAction getAction() {
        return plushieAction;
    }
    
    public boolean havePermission() {
        return PlushieAuth.PLUSHIE_SESSION.hasPermission(getAction());
    }
    
    protected void permissionCheck() throws PermissionSystem.InsufficientPermissionsException {
        if (!havePermission()) {
            throw new PermissionSystem.InsufficientPermissionsException(getAction());
        }
    }

    public String getBaseUrl() {
        if (isNeedsSecure()) {
            return URL_SECURE;
        } else {
            return URL_NORMAL;
        }
    }

    protected static JsonElement downloadJson(String url) throws Exception {
        String data = downloadString(url);
        if (data == null) {
            return null;
        }
        JsonElement json = null;
        try {
            json = new JsonParser().parse(data);
        } catch (Exception e) {
            ModLog.debug(data);
            e.printStackTrace();
            return null;
        }
        return json;
    }

    protected static String downloadString(String url) throws Exception {
        InputStream in = null;
        String data = null;
        try {
            in = new URL(url).openStream();
            data = IOUtils.toString(in, Charsets.UTF_8);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return data;
    }
    
    protected static synchronized boolean authenticateUser () {
        GameProfile gameProfile = Minecraft.getInstance().getUser().getGameProfile();
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        if (!plushieSession.isAuthenticated()) {
            JsonObject jsonObject = PlushieAuth.authenticateUser(gameProfile.getName(), gameProfile.getId().toString());
            plushieSession.authenticate(jsonObject);
        }
        
        if (!plushieSession.isAuthenticated()) {
            ModLog.error("Authentication failed.");
            return false;
        }
        return true;
    }
    
    public ListenableFutureTask<V> createTaskAndRun(FutureCallback<V> callback) {
        ListenableFutureTask<V> futureTask = createTask();
        if (callback != null) {
            Futures.addCallback(futureTask, callback);
        }
        GLOBAL_TASK_EXECUTOR.execute(futureTask);
        return futureTask;
    }
    
    public ListenableFutureTask<V> createTask(FutureCallback<V> callback) {
        ListenableFutureTask<V> futureTask = createTask();
        Futures.addCallback(futureTask, callback);
        return futureTask;
    }

    public ListenableFutureTask<V> createTask() {
        return ListenableFutureTask.create(this);
    }

    public void runTask(ListenableFutureTask<V> futureTask) {
        GLOBAL_TASK_EXECUTOR.execute(futureTask);
    }
}
