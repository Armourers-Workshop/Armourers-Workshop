package moe.plushie.armourers_workshop.library.data.global.task.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTask;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskResult;

public class GlobalTaskUserSkinRate extends GlobalTask<GlobalTaskUserSkinRate.UserSkinRateResult> {

    private static final String URL = "user-skin-action.php?userId=%d&accessToken=%s&action=%s&skinId=%d&rating=%d";

    private final int skinID;
    private final int rating;

    public GlobalTaskUserSkinRate(int skinID, int rating) {
        super(PermissionSystem.PlushieAction.SKIN_RATE, true);
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
            return new UserSkinRateResult(GlobalTaskResult.FAILED, data, 0F);
        }

        boolean valid = jsonObject.get("valid").getAsBoolean();
        if (!valid) {
            if (jsonObject.has("reason")) {
                return new UserSkinRateResult(GlobalTaskResult.FAILED, jsonObject.get("reason").getAsString(), 0F);
            }
            return new UserSkinRateResult(GlobalTaskResult.FAILED, jsonObject.toString(), 0F);
        }

        float rating = jsonObject.get("rating").getAsFloat();

        return new UserSkinRateResult(GlobalTaskResult.SUCCESS, jsonObject.toString(), rating);
    }

    public class UserSkinRateResult {

        private GlobalTaskResult result;
        private String message;
        private float newRating;

        public UserSkinRateResult(GlobalTaskResult result, String message, float newRating) {
            this.result = result;
            this.message = message;
            this.newRating = newRating;
        }

        public GlobalTaskResult getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }

        public float getNewRating() {
            return newRating;
        }
    }
}
