package riskyken.armourersWorkshop.common.library.global;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gson.JsonArray;

import riskyken.armourersWorkshop.common.lib.LibModInfo;

public class SkinSearch implements Runnable {
    
    private static final String SEARCH_URL = "http://plushie.moe/armourers_workshop/skin-search.php";
    private String searchText;
    private ISearchResultsCallback callback;
    
    private SkinSearch(String searchText, ISearchResultsCallback callback) {
        this.searchText = searchText;
        this.callback = callback;
    }
    
    public static void downloadSearchResults(String searchText, ISearchResultsCallback callback) {
        Thread t = new Thread(new SkinSearch(searchText, callback));
        t.setName(LibModInfo.NAME + " search results download thread.");
        t.setDaemon(true);
        t.start();
    }
    
    public static interface ISearchResultsCallback {
        public void downloadSearchResultsFinished(JsonArray json);
    }

    @Override
    public void run() {
        try {
            JsonArray json = DownloadUtils.downloadJsonArray(SEARCH_URL + "?search=" + URLEncoder.encode(searchText, "UTF-8"));
            if (callback != null & json != null) {
                callback.downloadSearchResultsFinished(json);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
