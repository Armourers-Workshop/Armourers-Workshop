package moe.plushie.armourers_workshop.common.library.global.task;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.common.library.global.MultipartForm;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.skin.data.serialize.SkinSerializer;

public class GlobalTaskSkinListUser extends GlobalTask<JsonObject> {
    
    private static final String URL = "user-skins-page.php?userId=%d&maxFileVersion=%d&pageIndex=%d&pageSize=%d";

    private final int userID;
    private final String searchTypes;
    private final int pageIndex;
    private final int pageSize;

    public GlobalTaskSkinListUser(int userID, String searchTypes, int pageIndex, int pageSize) {
        super(PlushieAction.SKIN_LIST_USER, false);
        this.userID = userID;
        this.searchTypes = searchTypes;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    @Override
    public JsonObject call() throws Exception {
        permissionCheck();
        String url = String.format(getBaseUrl() + URL, userID, SkinSerializer.MAX_FILE_VERSION, pageIndex, pageSize);
        MultipartForm multipartForm = new MultipartForm(url);
        multipartForm.addText("searchTypes", searchTypes);
        String data = multipartForm.upload();
        return new JsonParser().parse(data).getAsJsonObject();
    }
}
