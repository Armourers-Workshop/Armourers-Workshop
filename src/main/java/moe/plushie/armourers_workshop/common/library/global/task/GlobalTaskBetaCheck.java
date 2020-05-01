package moe.plushie.armourers_workshop.common.library.global.task;

import java.net.URLEncoder;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.common.library.global.PlushieUser;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;

public class GlobalTaskBetaCheck extends GlobalTask<PlushieUser> {

    private static final String URL = "beta-check.php?uuid=%s";

    private final UUID uuid;

    public GlobalTaskBetaCheck(UUID uuid) {
        super(PlushieAction.BETA_CHECK, false);
        this.uuid = uuid;
    }

    @Override
    public PlushieUser call() throws Exception {
        permissionCheck();
        String url = String.format(getBaseUrl() + URL, URLEncoder.encode(uuid.toString(), "UTF-8"));
        JsonObject jsonObject = new JsonParser().parse(downloadString(url)).getAsJsonObject();
        PlushieUser plushieUser = PlushieUser.readPlushieUser(jsonObject);
        if (plushieUser == null) {
            throw new Exception("Remote user check error.\n\n" + jsonObject.toString());
        }
        return plushieUser;
    }
}
