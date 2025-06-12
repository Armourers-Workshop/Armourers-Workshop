package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;

import java.util.ArrayList;

public class ReportResult {

    private int totalPages;
    private int totalResults;
    private final ArrayList<Report> reports = new ArrayList<>();

    public static ReportResult fromJSON(IODataObject json) {
        var result = new ReportResult();
        json.get("results").allValues().forEach(it -> result.reports.add(new Report(it)));
        result.totalPages = json.get("totalPages").intValue();
        result.totalResults = json.get("totalResults").intValue();
        return result;
    }

    public ArrayList<Report> reports() {
        return reports;
    }

    public int totalPages() {
        return totalPages;
    }

    public int totalResults() {
        return totalResults;
    }
}
