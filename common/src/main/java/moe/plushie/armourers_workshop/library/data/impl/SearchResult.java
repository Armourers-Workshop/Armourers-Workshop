package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.api.data.IDataPackObject;

import java.util.ArrayList;

public class SearchResult {

    private int totalPages;
    private int totalResults;
    private final ArrayList<ServerSkin> skins = new ArrayList<>();

    public static SearchResult fromJSON(IDataPackObject json) {
        SearchResult result = new SearchResult();
        json.get("results").allValues().forEach(it -> result.skins.add(new ServerSkin(it)));
        result.totalPages = json.get("totalPages").intValue();
        result.totalResults = json.get("totalResults").intValue();
        return result;
    }

    public ArrayList<ServerSkin> getSkins() {
        return skins;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
