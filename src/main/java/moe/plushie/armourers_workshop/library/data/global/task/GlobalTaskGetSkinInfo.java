package moe.plushie.armourers_workshop.library.data.global.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.library.data.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.library.data.global.MultipartForm;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;

public class GlobalTaskGetSkinInfo extends GlobalTask<JsonObject> {

    private static final String URL = "skin-info.php?skinid=%d&maxFileVersion=%d";

    private final int skinId;
    private final String searchTypes;

    public GlobalTaskGetSkinInfo(int skinId) {
        super(PermissionSystem.PlushieAction.GET_SKIN_INFO, false);
        this.skinId = skinId;
        this.searchTypes = GlobalSkinLibraryUtils.allSearchTypes();
    }

    @Override
    public JsonObject call() throws Exception {
        String url = getBaseUrl() + URL;
        url = String.format(url, skinId, SkinSerializer.MAX_FILE_VERSION);
        MultipartForm multipartForm = new MultipartForm(url);
        multipartForm.addText("searchTypes", searchTypes);
        String data = multipartForm.upload();
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        return jsonObject.getAsJsonArray("results").get(0).getAsJsonObject();
    }
}
