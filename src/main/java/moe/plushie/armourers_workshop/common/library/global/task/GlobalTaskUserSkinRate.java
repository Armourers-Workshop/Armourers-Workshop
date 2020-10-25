package moe.plushie.armourers_workshop.common.library.global.task;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;

import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskUserSkinRate.UserSkinRateResult;

public class GlobalTaskUserSkinRate extends GlobalTask<UserSkinRateResult> {

    private static final String URL = "user-skin-action.php?userId=%d&accessToken=%s&action=%s&skinId=%d&rating%d";

    private final int skinID;
    private final int rating;

    public GlobalTaskUserSkinRate(int skinID, int rating) {
        super(PlushieAction.SKIN_RATE, true);
        this.skinID = skinID;
        this.rating = rating;
    }

    @Override
    public UserSkinRateResult call() throws Exception {
        permissionCheck();
        if (!authenticateUser()) {
            throw new AuthenticationException();
        }
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        String url = String.format(getBaseUrl() + URL, plushieSession.getServerId(), plushieSession.getAccessToken(), "rate", skinID, rating);
        String data = downloadString(url);
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();

        if (!jsonObject.has("valid")) {
            return new UserSkinRateResult(GlobalTaskResult.FAILED, data);
        }

        boolean valid = jsonObject.get("valid").getAsBoolean();
        if (!valid) {
            if (jsonObject.has("reason")) {
                return new UserSkinRateResult(GlobalTaskResult.FAILED, jsonObject.get("reason").getAsString());
            }
            return new UserSkinRateResult(GlobalTaskResult.FAILED, jsonObject.toString());
        }

        int rating = jsonObject.get("rating").getAsInt();

        return new UserSkinRateResult(GlobalTaskResult.SUCCESS, jsonObject.get("reason").getAsString());
    }

    public class UserSkinRateResult {

        private GlobalTaskResult result;
        private String message;

        public UserSkinRateResult(GlobalTaskResult result, String message) {
            this.result = result;
            this.message = message;
        }

        public GlobalTaskResult getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }
}
