package moe.plushie.armourers_workshop.common.library.global.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.library.global.MultipartForm;
import moe.plushie.armourers_workshop.common.skin.data.serialize.SkinSerializer;

public class GlobalTaskRecentlyUploaded extends GlobalTask<JsonArray> {

    private static final String USER_INFO_URL = "recently-uploaded.php?limit=%d&maxFileVersion=%d";
    
    private final int limit;
    private final String searchTypes;
    
    public GlobalTaskRecentlyUploaded(int limit, String searchTypes) {
        super(ArmourersWorkshop.getProxy().getPermissionSystem().groupNoLogin, false);
        this.limit = limit;
        this.searchTypes = searchTypes;
    }

    @Override
    public JsonArray call() throws Exception {
        String url = getBaseUrl() + USER_INFO_URL;
        url = String.format(url, limit, SkinSerializer.MAX_FILE_VERSION);
        MultipartForm multipartFormRecentlyUploaded = new MultipartForm(url);
        multipartFormRecentlyUploaded.addText("searchTypes", searchTypes);
        String data = multipartFormRecentlyUploaded.upload();
        JsonArray jsonArray = new JsonParser().parse(data).getAsJsonArray();
        return jsonArray;
    }
}
