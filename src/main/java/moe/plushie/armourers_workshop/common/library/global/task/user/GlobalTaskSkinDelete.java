package moe.plushie.armourers_workshop.common.library.global.task.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;

import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTask;

public class GlobalTaskSkinDelete extends GlobalTask<JsonObject> {

    private final String URL = "user-skin-delete.php?userId=%d&accessToken=%s&skinId=%d";

    private final int skinID;
    private final boolean moderator;

    public GlobalTaskSkinDelete(int skinID, boolean moderator) {
        super(PlushieAction.SKIN_OWNER_DELETE, true);
        this.skinID = skinID;
        this.moderator = moderator;
    }

    @Override
    public PlushieAction getAction() {
        if (moderator) {
            return PlushieAction.SKIN_MOD_DELETE;
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
