package moe.plushie.armourers_workshop.common.library.global.task.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;

import moe.plushie.armourers_workshop.common.library.global.MultipartForm;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTask;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskResult;

public class GlobalTaskSkinUpload extends GlobalTask<GlobalTaskSkinUpload.Result> {

    private static final String URL = "user-skin-upload.php";

    private final byte[] file;
    private final String name;
    private final String description;

    public GlobalTaskSkinUpload(byte[] file, String name, String description) {
        super(PlushieAction.SKIN_UPLOAD, true);
        this.file = file;
        this.name = name;
        this.description = description;
    }

    @Override
    public Result call() throws Exception {
        permissionCheck();
        if (!authenticateUser()) {
            throw new AuthenticationException();
        }
        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;

        MultipartForm multipartForm = new MultipartForm(getBaseUrl() + URL);
        multipartForm.addText("userId", String.valueOf(plushieSession.getServerId()));
        multipartForm.addText("accessToken", plushieSession.getAccessToken());

        multipartForm.addText("name", name);
        multipartForm.addText("description", description);
        multipartForm.addFile("fileToUpload", name, file);

        String downloadString = multipartForm.upload();
        JsonObject json = new JsonParser().parse(downloadString).getAsJsonObject();

        if (json.has("valid") & json.has("action")) {
            String action = json.get("action").getAsString();
            boolean valid = json.get("valid").getAsBoolean();
            if (valid & action.equals("skin-upload")) {
                return new Result(GlobalTaskResult.SUCCESS, downloadString);
            }
        } else {
            if (json.has("reason")) {
                String reason = json.get("reason").getAsString();
                return new Result(GlobalTaskResult.FAILED, reason);
            }
        }

        return new Result(GlobalTaskResult.FAILED, downloadString);
    }

    public class Result {

        private GlobalTaskResult result;
        private String message;

        public Result(GlobalTaskResult result, String message) {
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
