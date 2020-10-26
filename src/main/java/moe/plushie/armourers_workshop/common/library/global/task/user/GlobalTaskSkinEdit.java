package moe.plushie.armourers_workshop.common.library.global.task.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;

import moe.plushie.armourers_workshop.common.library.global.MultipartForm;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTask;

public class GlobalTaskSkinEdit extends GlobalTask<JsonObject> {

    private final String URL = "user-skin-edit.php?userId=%d&accessToken=%s&skinId=%d";

    private final int skinID;
    private final boolean moderator;
    private final String name;
    private final String description;

    public GlobalTaskSkinEdit(int skinID, String name, String description, boolean moderator) {
        super(PlushieAction.SKIN_OWNER_EDIT, true);
        this.skinID = skinID;
        this.name = name;
        this.description = description;
        this.moderator = moderator;
    }

    @Override
    public PlushieAction getAction() {
        if (moderator) {
            return PlushieAction.SKIN_MOD_EDIT;
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
        MultipartForm multipartForm = new MultipartForm(url);
        multipartForm.addText("name", name);
        multipartForm.addText("description", description);
        return new JsonParser().parse(multipartForm.upload()).getAsJsonObject();
    }
}
