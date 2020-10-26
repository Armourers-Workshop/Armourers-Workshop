package moe.plushie.armourers_workshop.common.library.global.task.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTask;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskResult;
import moe.plushie.armourers_workshop.common.library.global.task.user.GlobalTaskUserSkinRating.UserSkinRatingResult;
import moe.plushie.armourers_workshop.utils.ModLogger;

public class GlobalTaskUserSkinRating extends GlobalTask<UserSkinRatingResult> {

    private static final String URL = "user-skin-action.php?userId=%d&accessToken=%s&action=%s&skinId=%d";

    private final int skinID;

    public GlobalTaskUserSkinRating(int skinID) {
        super(PlushieAction.SKIN_GET_RATED, true);
        this.skinID = skinID;
    }

    @Override
    public UserSkinRatingResult call() throws Exception {
        permissionCheck();
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        String url = String.format(getBaseUrl() + URL, plushieSession.getServerId(), "", "get_rating", skinID);
        String data = downloadString(url);
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();

        if (!jsonObject.has("valid")) {
            return new UserSkinRatingResult(GlobalTaskResult.FAILED, data, 0);
        }

        boolean valid = jsonObject.get("valid").getAsBoolean();
        if (!valid) {
            if (jsonObject.has("reason")) {
                return new UserSkinRatingResult(GlobalTaskResult.FAILED, jsonObject.get("reason").getAsString(), 0);
            }
            return new UserSkinRatingResult(GlobalTaskResult.FAILED, jsonObject.toString(), 0);
        }

        int rating = jsonObject.get("rating").getAsInt();
        ModLogger.log("rating: " + rating);

        return new UserSkinRatingResult(GlobalTaskResult.SUCCESS, jsonObject.toString(), rating);
    }

    public class UserSkinRatingResult {

        private GlobalTaskResult result;
        private String message;
        private int rating;

        public UserSkinRatingResult(GlobalTaskResult result, String message, int rating) {
            this.result = result;
            this.message = message;
            this.rating = rating;
        }

        public GlobalTaskResult getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }

        public int getRating() {
            return rating;
        }
    }
}
