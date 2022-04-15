package moe.plushie.armourers_workshop.library.data.global.task;

import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;

public class GlobalTaskGetSkinInfo extends GlobalTask<JsonObject> {

    private static final String URL = "beta-check.php?uuid=%s";

    private final int skinId;

    public GlobalTaskGetSkinInfo(int skinId) {
        super(PermissionSystem.PlushieAction.GET_SKIN_INFO, false);
        this.skinId = skinId;
    }

    @Override
    public JsonObject call() throws Exception {
        permissionCheck();
        return null;
    }
}
