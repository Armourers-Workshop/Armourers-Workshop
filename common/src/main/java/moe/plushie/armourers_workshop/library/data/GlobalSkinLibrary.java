package moe.plushie.armourers_workshop.library.data;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.library.data.impl.*;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class GlobalSkinLibrary extends ServerSession {

    private static final int TOKEN_UPDATE_TIME = 60 * 1000;
    private static final GlobalSkinLibrary INSTANCE = new GlobalSkinLibrary();

    private boolean updatingToken = false;
    private boolean connecting = false;
    private boolean connected = false;

    private ServerUser currentUser = new ServerUser(UUID.randomUUID(), "");

    private final HashMap<String, ServerUser> users = new HashMap<>();
    private final HashSet<String> downloaded = new HashSet<>();

    public static GlobalSkinLibrary getInstance() {
        return INSTANCE;
    }

    public void executor(Executor executor) {
        this.notifier = executor;
    }

    public void connect(GameProfile profile, Consumer<Exception> consumer) {
        if (connecting || connected) {
            return;
        }
        currentUser = new ServerUser(profile.getId(), profile.getName());
        if (!isValidJavaVersion()) {
            connected = true;
            // consumer.accept(new RuntimeException("invalid java version"));
            return;
        }
        connecting = true;
        request("/connect", a2m("uuid", profile.getId()), ServerUser::fromJSON, (result, exception) -> {
            connecting = false;
            connected = true;
            if (result != null) {
                currentUser = result;
                users.put(result.getId(), result);
            }
        });
    }

    public void auth() throws Exception {
        ServerUser user = getUser();
        if (user.isAuthenticated()) {
            return;
        }
        HashMap<String, Object> parameters = buildAuthFromMinecraft();
        ServerToken accessToken = request("/user/auth", parameters, ServerToken::new);
        user.setAccessToken(accessToken);
    }

    public void auth2() {
        ServerToken accessToken = getUser().getAccessToken();
        if (accessToken == null || accessToken.getRemainingTime() < 0 || accessToken.getRemainingTime() > TOKEN_UPDATE_TIME || updatingToken) {
            return;
        }
        ModLog.debug("Getting new token. Time left: {}", accessToken.getRemainingTime() / 1000);
        updatingToken = true;
        request("/user/auth2", null, ServerToken::new, (result, exception) -> {
            updatingToken = false;
            if (result != null) {
                getUser().setAccessToken(result);
            }
        });
    }

    public void join(IResultHandler<Void> handlerIn) {
        submit(handlerIn, handlerOut -> {
            try {
                HashMap<String, Object> parameters = buildAuthFromMinecraft();
                request("/user/join", parameters, o -> o);
                handlerOut.accept(null);
            } catch (Exception exception1) {
                handlerOut.reject(exception1);
            }
        });
    }

    public void searchSkin(String keyword, int pageIndex, int pageSize, SearchColumnType searchOrderColumn, SearchOrderType searchOrder, ISkinType searchType, IResultHandler<SearchResult> handler) {
        HashMap<String, Object> parameters = new HashMap<>();
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
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("pageIndex", pageIndex);
        parameters.put("pageSize", pageSize);
        parameters.put("searchTypes", buildSearchTypes(searchType));
        request("/skin/user", parameters, SearchResult::fromJSON, handler);
    }

    public void getSkin(String skinId, IResultHandler<ServerSkin> handler) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("skinid", skinId);
        parameters.put("searchTypes", buildSearchTypes(SkinTypes.UNKNOWN));
        request("/skin/info", parameters, SearchResult::fromJSON, (result, exception) -> {
            if (result != null) {
                if (!result.getSkins().isEmpty()) {
                    handler.accept(result.getSkins().get(0));
                } else {
                    handler.reject(new RuntimeException("can't found the skin " + skinId));
                }
            } else {
                handler.reject(exception);
            }
        });
    }

    public void uploadSkin(String name, String desc, Skin skin, IResultHandler<Void> handler) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("description", desc);
        parameters.put("fileToUpload", new ServerRequest.MultipartFormFile(name, () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            SkinIOUtils.saveSkinToStream(outputStream, skin);
            byte[] fileBytes = outputStream.toByteArray();
            StreamUtils.closeQuietly(outputStream);
            return Unpooled.wrappedBuffer(fileBytes);
        }));
        request("/skin/upload", parameters, null, handler);
    }

    public InputStream downloadPreviewSkin(String skinId) throws Exception {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("skinid", skinId);
        parameters.put("skinFileName", "");
        return buildTask("/skin/preview", parameters).call();
    }

    public InputStream downloadSkin(String skinId) throws Exception {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("skinid", skinId);
        parameters.put("skinFileName", "");
        return buildTask("/skin/download", parameters).call();
    }

    public void downloadSkin(String skinId, File target, IResultHandler<File> handlerIn) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("skinid", skinId);
        parameters.put("skinFileName", "");
        submit(handlerIn, handlerOut -> {
            try {
                InputStream inputStream = buildTask("/skin/download", parameters).call();
                SkinFileUtils.copyInputStreamToFile(inputStream, target);
                handlerOut.accept(target);
            } catch (Exception exception) {
                handlerOut.reject(exception);
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
        HashMap<String, Object> parameters = super.defaultParameters();
        ServerUser user = getUser();
        ServerToken accessToken = user.getAccessToken();
        parameters.put("maxFileVersion", SkinSerializer.MAX_FILE_VERSION);
        if (user.getId() != null) {
            parameters.put("userId", user.getId());
        }
        if (accessToken != null && accessToken.getValue() != null) {
            parameters.put("accessToken", accessToken.getValue());
        }
        return parameters;
    }

    public ServerUser getUser() {
        return currentUser;
    }

    @Nullable
    public ServerUser getUserById(String userId) {
        synchronized (users) {
            ServerUser user = users.get(userId);
            if (user != null) {
                return user;
            }
        }
        ServerUser user = new ServerUser(userId, UUID.randomUUID(), "", ServerPermissions.NO_LOGIN);
        users.put(userId, user);
        if (!downloaded.contains(userId)) {
            downloaded.add(userId);
            getUser(userId, (realUser, exception) -> {
                synchronized (users) {
                    users.put(userId, realUser);
                }
            });
        }
        return user;
    }

    public boolean isConnected() {
        return connected;
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
        StringBuilder searchTypesBuilder = new StringBuilder();
        for (ISkinType skinType1 : SkinTypes.values()) {
            ResourceLocation registryName = skinType1.getRegistryName();
            if (skinType1 != SkinTypes.UNKNOWN && registryName != null) {
                if (searchTypesBuilder.length() != 0) {
                    searchTypesBuilder.append(";");
                }
                searchTypesBuilder.append(registryName);
            }
        }
        return searchTypesBuilder.toString();
    }

    private HashMap<String, Object> buildAuthFromMinecraft() {
        ServerUser user = getUser();
        String serverId = String.valueOf(defaultBaseURL().hashCode());
        if (!MinecraftAuth.checkAndRefeshAuth(serverId)) {
            ModLog.debug("Failed MC Auth");
            throw new RuntimeException("Failed MC Auth");
        }
        ModLog.debug("MC Auth Done");
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("username", user.getName());
        parameters.put("uuid", user.getUUID());
        parameters.put("serverId", serverId);
        return parameters;
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
        switch (permission) {
            case SKIN_OWNER_DELETE: {
                return ServerPermission.SKIN_MOD_DELETE;
            }
            case SKIN_OWNER_EDIT: {
                return ServerPermission.SKIN_MOD_EDIT;
            }
            default: {
                return permission;
            }
        }
    }
}
