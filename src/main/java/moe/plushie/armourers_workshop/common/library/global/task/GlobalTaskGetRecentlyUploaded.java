package moe.plushie.armourers_workshop.common.library.global.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.common.library.global.MultipartForm;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.skin.data.serialize.SkinSerializer;

public class GlobalTaskGetRecentlyUploaded extends GlobalTask<JsonArray> {

    private static final String URL = "recently-uploaded.php?limit=%d&maxFileVersion=%d";

    private final int limit;
    private final String searchTypes;

    public GlobalTaskGetRecentlyUploaded(int limit, String searchTypes) {
        super(PlushieAction.GET_RECENTLY_UPLOADED, false);
        this.limit = limit;
        this.searchTypes = searchTypes;
    }

    @Override
    public JsonArray call() throws Exception {
        permissionCheck();
        String url = getBaseUrl() + URL;
        url = String.format(url, limit, SkinSerializer.MAX_FILE_VERSION);
        MultipartForm multipartForm = new MultipartForm(url);
        multipartForm.addText("searchTypes", searchTypes);
        String data = multipartForm.upload();
        JsonArray jsonArray = new JsonParser().parse(data).getAsJsonArray();
        return jsonArray;
    }
}
