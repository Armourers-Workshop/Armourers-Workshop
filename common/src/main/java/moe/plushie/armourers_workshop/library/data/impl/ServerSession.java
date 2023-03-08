package moe.plushie.armourers_workshop.library.data.impl;

import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ServerSession {

    private static final ExecutorService workThread = ThreadUtils.newFixedThreadPool(1, "AW-NET/G-ED");

    private static final ArrayList<String> DEFAULT_URLs = new ArrayList<>();
    private static final Map<String, ServerRequest> REQUESTS = new HashMap<>();

    protected Executor notifier = Runnable::run;

    protected <T> void request(String path, @Nullable Map<String, ?> parameters, Function<IDataPackObject, T> deserializer, IResultHandler<T> handlerIn) {
        // we need to switch to a background thread to make sure everything is running correctly.
        submit(handlerIn, handlerOut -> {
            try {
                T value = request(path, parameters, deserializer);
                handlerOut.accept(value);
            } catch (Exception exception1) {
                handlerOut.reject(exception1);
            }
        });
    }

    protected <T> T request(String path, @Nullable Map<String, ?> parameters, Function<IDataPackObject, T> deserializer) throws Exception {
        try {
            Callable<InputStream> task = buildTask(path, parameters);
            byte[] bytes = StreamUtils.toByteArray(task.call());
            JsonObject object = StreamUtils.fromJson(new ByteArrayInputStream(bytes), JsonObject.class);
            if (object == null) {
                throw new RuntimeException("can't parse the object from json");
            }
            IDataPackObject responseData = IDataPackObject.of(object);
            ServerResponse response = new ServerResponse(responseData);
            if (!response.isValid()) {
                throw new RuntimeException(response.getMessage());
            }
            if (deserializer == null) {
                return null;
            }
            return deserializer.apply(responseData);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    protected Callable<InputStream> buildTask(String path, @Nullable Map<String, ?> parameters) throws Exception {
        ServerRequest request = loadAPI(path);
        checkRequest(request, parameters);
        return request.build(buildRequestURL(request), m2m(parameters));
    }

    protected String buildRequestURL(ServerRequest request) {
        // when the request required authorization,
        // we will try to switch to the https channel.
        if (request.has("accessToken")) {
            for (String baseURL : getBaseURLs()) {
                if (baseURL.startsWith("https://")) {
                    return baseURL;
                }
            }
        }
        return defaultBaseURL();
    }

    protected void checkRequest(ServerRequest request, @Nullable Map<String, ?> parameters) throws Exception {
    }

    protected String defaultBaseURL() {
        ArrayList<String> baseURLs = getBaseURLs();
        if (!baseURLs.isEmpty()) {
            return baseURLs.get(0);
        }
        return "";
    }

    protected ArrayList<String> getBaseURLs() {
        // must load once.
        if (DEFAULT_URLs.isEmpty()) {
            loadAPIs();
        }
        return DEFAULT_URLs;
    }

    protected HashMap<String, Object> defaultParameters() {
        return new HashMap<>();
    }

    protected <T> void submit(IResultHandler<T> handler, Consumer<IResultHandler<T>> task) {
        workThread.submit(() -> task.accept((result, exception) -> notify(() -> handler.apply(result, exception))));
    }

    private void notify(Runnable handler) {
        notifier.execute(handler);
    }

    private ServerRequest loadAPI(String path) throws Exception {
        ServerRequest request = loadAPIs().get(path);
        if (request != null) {
            return request;
        }
        throw new RuntimeException("missing request from " + path);
    }

    private Map<String, ServerRequest> loadAPIs() {
        if (!REQUESTS.isEmpty()) {
            return REQUESTS;
        }
        InputStream inputStream = getClass().getResourceAsStream("/data/armourers_workshop/skin/library/gsl.json");
        JsonObject jsonObject = StreamUtils.fromJson(inputStream, JsonObject.class);
        if (jsonObject == null) {
            throw new RuntimeException("missing gsl.json in data pack!");
        }
        IDataPackObject root = IDataPackObject.of(jsonObject);
        IDataPackObject server = root.get("server");
        server.entrySet().forEach(it -> {
            if (it.getKey().equals("/host")) {
                it.getValue().allValues().forEach(url -> DEFAULT_URLs.add(url.stringValue()));
                return;
            }
            ServerRequest req = ServerRequest.fromJSON(it.getValue());
            if (req != null) {
                req.setPermission(ServerPermission.byId(it.getKey()));
                REQUESTS.put(it.getKey(), req);
            }
        });
        return REQUESTS;
    }

    private Map<String, Object> m2m(Map<String, ?> m) {
        HashMap<String, Object> map = defaultParameters();
        if (m != null) {
            map.putAll(m);
        }
        return map;
    }
}
