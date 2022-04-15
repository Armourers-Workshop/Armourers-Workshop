package moe.plushie.armourers_workshop.library.data.global.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.library.data.global.MultipartForm;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;

public class GlobalTaskGetRecentlyUploaded extends GlobalTask<JsonArray> {

    private static final String URL = "recently-uploaded.php?limit=%d&maxFileVersion=%d";

    private final int limit;
    private final String searchTypes;

    public GlobalTaskGetRecentlyUploaded(int limit, String searchTypes) {
        super(PermissionSystem.PlushieAction.GET_RECENTLY_UPLOADED, false);
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
