package moe.plushie.armourers_workshop.library.data.global.task;

import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.data.global.PlushieUser;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;

public class GlobalTaskUserInfo extends GlobalTask<PlushieUser> {

    private static final String URL = "user-info.php?userId=%d";

    private final int userID;

    public GlobalTaskUserInfo(int userID) {
        super(PermissionSystem.PlushieAction.USER_INFO, false);
        this.userID = userID;
    }

    @Override
    public PlushieUser call() throws Exception {
        permissionCheck();
        String url = getBaseUrl() + URL;
        url = String.format(url, userID);
        JsonObject json = downloadJson(url).getAsJsonObject();
        PlushieUser plushieUser = PlushieUser.readPlushieUser(json);
        if (plushieUser == null) {
            ModLog.error("Failed downloading info for user id: " + userID);
            throw new Exception("Remote user check error.\n\n" + json.toString());
        }
        return plushieUser;
    }
}
