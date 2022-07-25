package moe.plushie.armourers_workshop.library.data.global.task;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.library.data.global.MultipartForm;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;

import java.net.URLEncoder;

public class GlobalTaskSkinSearch extends GlobalTask<JsonObject> {

    private static final String URL = "skin-search-page.php?search=%s&maxFileVersion=%d&pageIndex=%d&pageSize=%d";

    private final String searchText;
    private final String searchTypes;
    private final int pageIndex;
    private final int pageSize;
    private SearchColumnType searchOrderColumn = SearchColumnType.DATE_CREATED;
    private SearchOrderType searchOrder = SearchOrderType.DESC;

    public GlobalTaskSkinSearch(String searchText, String searchTypes, int pageIndex, int pageSize) {
        super(PermissionSystem.PlushieAction.SKIN_SEARCH, false);
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
            return "skin_search_column.armourers_workshop." + toString().toLowerCase();
        }
    }

    public enum SearchOrderType {
        DESC, ASC;

        public String getLangKey() {
            return "skin_search_order.armourers_workshop:" + toString().toLowerCase();
        }
    }
}
