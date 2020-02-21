package moe.plushie.armourers_workshop.common.library.global.task;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;

public class GlobalTaskUserSkinRating extends GlobalTask<JsonObject> {

    private static final String URL = "user-skin-action.php?userId=%d&accessToken=%s&action=%s&skinId=%d";
    
    private final int skinID;
    
    public GlobalTaskUserSkinRating(int skinID) {
        super(PlushieAction.SKIN_GET_RATED, true);
        this.skinID = skinID;
    }

    @Override
    public JsonObject call() throws Exception {
        permissionCheck();
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        String url = String.format(getBaseUrl() + URL, plushieSession.getServerId(), "", "hasLike", skinID);
        String data = downloadString(url);
        return new JsonParser().parse(data).getAsJsonObject();
    }
}
