package moe.plushie.armourers_workshop.common.library.global.task;

import java.net.URLEncoder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.library.global.MultipartForm;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.skin.data.serialize.SkinSerializer;

public class GlobalTaskSkinSearch extends GlobalTask<JsonObject> {

    private static final String URL = "skin-search-page.php?search=%s&maxFileVersion=%d&pageIndex=%d&pageSize=%d";

    private final String searchText;
    private final String searchTypes;
    private final int pageIndex;
    private final int pageSize;
    private SearchColumnType searchOrderColumn = SearchColumnType.DATE_CREATED;
    private SearchOrderType searchOrder = SearchOrderType.DESC;

    public GlobalTaskSkinSearch(String searchText, String searchTypes, int pageIndex, int pageSize) {
        super(PlushieAction.SKIN_SEARCH, false);
        this.searchText = searchText;
        this.searchTypes = searchTypes;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public GlobalTaskSkinSearch setSearchOrderColumn(SearchColumnType searchOrderColumn) {
        this.searchOrderColumn = searchOrderColumn;
        return this;
    }

    public GlobalTaskSkinSearch setSearchOrder(SearchOrderType searchOrder) {
        this.searchOrder = searchOrder;
        return this;
    }

    @Override
    public JsonObject call() throws Exception {
        permissionCheck();
        String url = String.format(getBaseUrl() + URL, URLEncoder.encode(searchText, "UTF-8"), SkinSerializer.MAX_FILE_VERSION, pageIndex, pageSize);
        MultipartForm multipartForm = new MultipartForm(url);
        multipartForm.addText("searchTypes", searchTypes);
        if (searchOrderColumn != null) {
            multipartForm.addText("searchOrderColumn", searchOrderColumn.toString().toLowerCase());
        }
        if (searchOrder != null) {
            multipartForm.addText("searchOrder", searchOrder.toString());
        }
        String data = multipartForm.upload();
        return new JsonParser().parse(data).getAsJsonObject();
    }

    public enum SearchColumnType {
        ID, USER_ID, NAME, DESCRIPTION, DATE_CREATED, SKIN_TYPE, DOWNLOADS, RATING, RATING_COUNT;
        
        public String getLangKey() {
            return "skin_search_column." + LibModInfo.ID + ":" + toString().toLowerCase();
        }
    }

    public enum SearchOrderType {
        DESC, ASC;
        
        public String getLangKey() {
            return "skin_search_order." + LibModInfo.ID + ":" + toString().toLowerCase();
        }
    }
}
