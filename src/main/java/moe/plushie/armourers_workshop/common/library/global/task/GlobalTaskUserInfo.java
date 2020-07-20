package moe.plushie.armourers_workshop.common.library.global.task;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.common.library.global.PlushieUser;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.utils.ModLogger;

public class GlobalTaskUserInfo extends GlobalTask<PlushieUser> {

    private static final String URL = "user-info.php?userId=%d";

    private final int userID;

    public GlobalTaskUserInfo(int userID) {
        super(PlushieAction.USER_INFO, false);
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
            ModLogger.log(Level.ERROR, "Failed downloading info for user id: " + userID);
            throw new Exception("Remote user check error.\n\n" + json.toString());
        }
        return plushieUser;
    }
}
