package moe.plushie.armourers_workshop.common.library.global.task;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;

import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;

public class GlobalTaskUserSkinRate extends GlobalTask<JsonObject> {
    
    private static final String URL = "user-skin-action.php?userId=%d&accessToken=%s&action=%s&skinId=%d";
    
    private final int skinID;
    private final int rating;
    
    public GlobalTaskUserSkinRate(int skinID, int rating) {
        super(PlushieAction.SKIN_RATE, true);
        this.skinID = skinID;
        this.rating = rating;
    }

    @Override
    public JsonObject call() throws Exception {
        permissionCheck();
        if (!authenticateUser()) {
            throw new AuthenticationException();
        }
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        boolean like = rating > 0;
        String action = "";
        if (like) {
            action = "like";
        } else {
            action = "unlike";
        }
        String url = String.format(getBaseUrl() + URL, plushieSession.getServerId(), plushieSession.getAccessToken(), action, skinID);
        String data = downloadString(url);
        return new JsonParser().parse(data).getAsJsonObject();
    }
}
