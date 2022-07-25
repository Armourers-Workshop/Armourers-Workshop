package moe.plushie.armourers_workshop.library.data.global.task.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTask;

public class GlobalTaskSkinDelete extends GlobalTask<JsonObject> {

    private final String URL = "user-skin-delete.php?userId=%d&accessToken=%s&skinId=%d";

    private final int skinID;
    private final boolean moderator;

    public GlobalTaskSkinDelete(int skinID, boolean moderator) {
        super(PermissionSystem.PlushieAction.SKIN_OWNER_DELETE, true);
        this.skinID = skinID;
        this.moderator = moderator;
    }

    @Override
    public PermissionSystem.PlushieAction getAction() {
        if (moderator) {
            return PermissionSystem.PlushieAction.SKIN_MOD_DELETE;
        }
        return super.getAction();
    }

    @Override
    public JsonObject call() throws Exception {
        permissionCheck();
        if (!authenticateUser()) {
            throw new AuthenticationException();
        }
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        String url = String.format(getBaseUrl() + URL, plushieSession.getServerId(), plushieSession.getAccessToken(), skinID);
        String data = downloadString(url);
        return new JsonParser().parse(data).getAsJsonObject();
    }
}
