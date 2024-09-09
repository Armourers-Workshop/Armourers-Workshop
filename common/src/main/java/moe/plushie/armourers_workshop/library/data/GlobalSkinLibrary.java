package moe.plushie.armourers_workshop.library.data;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.library.data.impl.MinecraftAuth;
import moe.plushie.armourers_workshop.library.data.impl.ReportFilter;
import moe.plushie.armourers_workshop.library.data.impl.ReportResult;
import moe.plushie.armourers_workshop.library.data.impl.SearchColumnType;
import moe.plushie.armourers_workshop.library.data.impl.SearchOrderType;
import moe.plushie.armourers_workshop.library.data.impl.SearchResult;
import moe.plushie.armourers_workshop.library.data.impl.ServerPermission;
import moe.plushie.armourers_workshop.library.data.impl.ServerPermissions;
import moe.plushie.armourers_workshop.library.data.impl.ServerRequest;
import moe.plushie.armourers_workshop.library.data.impl.ServerSession;
import moe.plushie.armourers_workshop.library.data.impl.ServerSkin;
import moe.plushie.armourers_workshop.library.data.impl.ServerStatus;
import moe.plushie.armourers_workshop.library.data.impl.ServerToken;
import moe.plushie.armourers_workshop.library.data.impl.ServerUser;
import moe.plushie.armourers_workshop.utils.SkinFileStreamUtils;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class GlobalSkinLibrary extends ServerSession {

    private static final int TOKEN_UPDATE_TIME = 60 * 1000;
    private static final GlobalSkinLibrary INSTANCE = new GlobalSkinLibrary();

    private State state = new State(new ArrayList<>());

    public static GlobalSkinLibrary getInstance() {
        return INSTANCE;
    }

    public void executor(Executor executor) {
        this.notifier = executor;
    }

    public void connect(GameProfile profile, Consumer<Exception> consumer) {
        resolveState();
        if (state.connecting || state.connected) {
            return;
        }
        state.currentUser = new ServerUser(profile.getId(), profile.getName());
        if (!isValidJavaVersion()) {
            state.connected = true;
            // consumer.accept(new RuntimeException("invalid java version"));
            return;
        }
        state.connecting = true;
        request("/connect", a2m("uuid", profile.getId()), ServerUser::fromJSON, (result, exception) -> {
            state.connecting = false;
            state.connected = true;
            updateUser(result);
        });
    }

    public void disconnect() {
        state = new State(new ArrayList<>());
    }

    public void auth() throws Exception {
        var user = getUser();
        if (user.isAuthenticated()) {
            return;
        }
        var parameters = authenticationFromMinecraft();
        var accessToken = request("/user/auth", parameters, ServerToken::new);
        user.setAccessToken(accessToken);
    }

    public void auth2() {
        var accessToken = getUser().getAccessToken();
        if (accessToken == null || accessToken.getRemainingTime() < 0 || accessToken.getRemainingTime() > TOKEN_UPDATE_TIME || state.updatingToken) {
            return;
        }
        ModLog.debug("Getting new token. Time left: {}", accessToken.getRemainingTime() / 1000);
        state.updatingToken = true;
        request("/user/auth2", null, ServerToken::new, (result, exception) -> {
            state.updatingToken = false;
            if (result != null) {
                getUser().setAccessToken(result);
            }
        });
    }

    public void join(IResultHandler<Void> handlerIn) {
        submit(handlerIn, handlerOut -> {
            try {
                HashMap<String, Object> parameters = authenticationFromMinecraft();
                request("/user/join", parameters, o -> o);
                updateUser(request("/connect", a2m("uuid", parameters.get("uuid")), ServerUser::fromJSON));
                handlerOut.accept(null);
            } catch (Exception exception1) {
                handlerOut.throwing(exception1);
            }
        });
    }

    public void searchSkin(String keyword, int pageIndex, int pageSize, SearchColumnType searchOrderColumn, SearchOrderType searchOrder, ISkinType searchType, IResultHandler<SearchResult> handler) {
        var parameters = new HashMap<String, Object>();
        parameters.put("search", keyword);
        parameters.put("pageIndex", pageIndex);
        parameters.put("pageSize", pageSize);
        parameters.put("searchTypes", buildSearchTypes(searchType));
        if (searchOrderColumn != null) {
            parameters.put("searchOrderColumn", searchOrderColumn.toString().toLowerCase());
        }
        if (searchOrder != null) {
            parameters.put("searchOrder", searchOrder.toString());
        }
        request("/skin/search", parameters, SearchResult::fromJSON, handler);
    }

    public void getUserSkinList(String userId, int pageIndex, int pageSize, ISkinType searchType, IResultHandler<SearchResult> handler) {
        var parameters = new HashMap<String, Object>();
        parameters.put("userId", userId);
        parameters.put("pageIndex", pageIndex);
        parameters.put("pageSize", pageSize);
        parameters.put("searchTypes", buildSearchTypes(searchType));
        request("/skin/user", parameters, SearchResult::fromJSON, handler);
    }

    public void getSkin(String skinId, IResultHandler<ServerSkin> handler) {
        var parameters = new HashMap<String, Object>();
        parameters.put("skinid", skinId);
        parameters.put("searchTypes", buildSearchTypes(SkinTypes.UNKNOWN));
        request("/skin/info", parameters, SearchResult::fromJSON, (result, exception) -> {
            if (result != null) {
                if (!result.getSkins().isEmpty()) {
                    handler.accept(result.getSkins().get(0));
                } else {
                    handler.throwing(new RuntimeException("can't found the skin " + skinId));
                }
            } else {
                handler.throwing(exception);
            }
        });
    }

    public void uploadSkin(String name, String desc, Skin skin, IResultHandler<Void> handler) {
        var parameters = new HashMap<String, Object>();
        parameters.put("name", name);
        parameters.put("description", desc);
        parameters.put("fileToUpload", new ServerRequest.MultipartFormFile(name, () -> {
            try (var outputStream = new ByteArrayOutputStream()) {
                SkinFileStreamUtils.saveSkinToStream(outputStream, skin);
                return Unpooled.wrappedBuffer(outputStream.toByteArray());
            }
        }));
        request("/skin/upload", parameters, null, handler);
    }

    public InputStream downloadPreviewSkin(String skinId) throws Exception {
        var parameters = new HashMap<String, Object>();
        parameters.put("skinid", skinId);
        parameters.put("skinFileName", "");
        return buildTask("/skin/preview", parameters).call();
    }

    public InputStream downloadSkin(String skinId) throws Exception {
        var parameters = new HashMap<String, Object>();
        parameters.put("skinid", skinId);
        parameters.put("skinFileName", "");
        return buildTask("/skin/download", parameters).call();
    }

    public void downloadSkin(String skinId, File target, IResultHandler<File> handlerIn) {
        submit(handlerIn, handlerOut -> {
            try {
                InputStream inputStream = downloadSkin(skinId);
                SkinFileUtils.copyInputStreamToFile(inputStream, target);
                handlerOut.accept(target);
            } catch (Exception exception) {
                handlerOut.throwing(exception);
            }
        });
    }


    public void getUser(String userId, IResultHandler<ServerUser> handler) {
        request("/user/info", a2m("userId", userId), ServerUser::fromJSON, handler);
    }

    public void getReportList(int pageIndex, int pageSize, ReportFilter filter, IResultHandler<ReportResult> handler) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("page", pageIndex);
        parameters.put("size", pageSize);
        parameters.put("filter", filter.toString().toLowerCase());
        request("/skin/reports", parameters, ReportResult::fromJSON, handler);
    }

    public void getServerStatus(IResultHandler<ServerStatus> handler) {
        request("/stats", null, ServerStatus::new, handler);
    }

    @Override
    protected void checkRequest(ServerRequest request, @Nullable Map<String, ?> parameters) throws Exception {
        ServerUser user = getUser();
        // when request permission is specified, we need to check it.
        ServerPermission permission = resolvePermission(request, parameters);
        if (permission != null && !user.hasPermission(permission)) {
            throw new RuntimeException("insufficient permissions");
        }
        // when accessToken is required of the request, we need to auto-login this user in gsl.
        if (request.has("accessToken")) {
            auth();
        }
    }

    @Override
    protected HashMap<String, Object> defaultParameters() {
        var parameters = super.defaultParameters();
        parameters.put("maxFileVersion", SkinSerializer.Versions.V13);
        var user = getUser();
        if (user.getId() != null) {
            parameters.put("userId", user.getId());
        }
        var accessToken = user.getAccessToken();
        if (accessToken != null && accessToken.getValue() != null) {
            parameters.put("accessToken", accessToken.getValue());
        }
        return parameters;
    }

    @Override
    protected ArrayList<String> getBaseURLs() {
        var customURLs = ModConfig.Common.customSkinServerURLs;
        if (!customURLs.isEmpty()) {
            return customURLs;
        }
        return super.getBaseURLs();
    }

    public ServerUser getUser() {
        return state.currentUser;
    }

    @Nullable
    public ServerUser getUserById(String userId) {
        synchronized (state.users) {
            ServerUser user = state.users.get(userId);
            if (user != null) {
                return user;
            }
        }
        ServerUser user = new ServerUser(userId, UUID.randomUUID(), "", ServerPermissions.NO_LOGIN);
        state.users.put(userId, user);
        if (!state.downloaded.contains(userId)) {
            state.downloaded.add(userId);
            getUser(userId, (realUser, exception) -> {
                synchronized (state.users) {
                    state.users.put(userId, realUser);
                }
            });
        }
        return user;
    }

    public boolean isConnected() {
        return state.connected;
    }

    public boolean isValidJavaVersion() {
        String[] javaVersion = getJavaVersion();
        int[] targetVersion = new int[]{8, 0, 101};
        for (int i = 0; i < javaVersion.length || i < targetVersion.length; ++i) {
            int sv = 0, dv = 0;
            if (i < javaVersion.length) {
                try {
                    sv = Integer.parseInt(javaVersion[i]);
                } catch (Exception ignored) {
                }
            }
            if (i < targetVersion.length) {
                dv = targetVersion[i];
            }
            if (sv < dv) {
                return false;
            } else if (sv > dv) {
                return true;
            }
        }
        return true;
    }

    // Java 8 or lower: 1.6.0_23, 1.7.0, 1.7.0_80, 1.8.0_211
    // Java 9 or higher: 9.0.1, 11.0.4, 12, 12.0.1
    public String[] getJavaVersion() {
        try {
            String[] version = System.getProperty("java.version").split("[._]");
            // modify the version to aligned format.
            if (Objects.equals(version[0], "1")) {
                return Arrays.copyOfRange(version, 1, version.length);
            }
            return version;
        } catch (Exception ignored) {
        }
        return new String[]{"6", "0"};
    }

    private String buildSearchTypes(ISkinType skinType) {
        if (skinType != null && skinType != SkinTypes.UNKNOWN) {
            return skinType.getRegistryName().toString();
        }
        var searchTypesBuilder = new StringBuilder();
        for (var skinType1 : SkinTypes.values()) {
            var registryName = skinType1.getRegistryName();
            if (skinType1 != SkinTypes.UNKNOWN && registryName != null) {
                if (searchTypesBuilder.length() != 0) {
                    searchTypesBuilder.append(";");
                }
                searchTypesBuilder.append(registryName);
            }
        }
        return searchTypesBuilder.toString();
    }

    private HashMap<String, Object> authenticationFromMinecraft() {
        ServerUser user = getUser();
        String serverId = String.valueOf(defaultBaseURL().hashCode());
        if (!MinecraftAuth.checkAndRefeshAuth(serverId)) {
            Exception error = MinecraftAuth.getLastError();
            ModLog.debug("MC Auth Failed");
            error.printStackTrace();
            throw new RuntimeException(error.getMessage());
        }
        ModLog.debug("MC Auth Done");
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("username", user.getName());
        parameters.put("uuid", user.getUUID());
        parameters.put("serverId", serverId);
        return parameters;
    }

    private Map<String, Object> a2m(String m, Object o) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(m, o);
        return map;
    }

    private void updateUser(ServerUser user) {
        if (user != null) {
            state.currentUser = user;
            state.users.put(user.getId(), user);
        }
    }

    private ServerPermission resolvePermission(ServerRequest request, @Nullable Map<String, ?> parameters) {
        ServerUser user = getUser();
        ServerPermission permission = request.getPermission();
        // if current user is not the owner, we need required the mod permission.
        Object value = null;
        if (parameters != null) {
            value = parameters.get("skinOwner");
        }
        if (value == null || user.getId().equals(value)) {
            return permission;
        }
        // this is a simple mapping.
        return switch (permission) {
            case SKIN_OWNER_DELETE -> ServerPermission.SKIN_MOD_DELETE;
            case SKIN_OWNER_EDIT -> ServerPermission.SKIN_MOD_EDIT;
            default -> permission;
        };
    }

    private void resolveState() {
        ArrayList<String> customURLs = ModConfig.Common.customSkinServerURLs;
        if (!customURLs.equals(state.hosts)) {
            state = new State(customURLs);
        }
    }

    private static class State {

        ServerUser currentUser = new ServerUser(UUID.randomUUID(), "");

        boolean updatingToken = false;
        boolean connecting = false;
        boolean connected = false;

        final HashMap<String, ServerUser> users = new HashMap<>();
        final HashSet<String> downloaded = new HashSet<>();

        final ArrayList<String> hosts;

        private State(ArrayList<String> hosts) {
            this.hosts = hosts;
        }
    }
}
