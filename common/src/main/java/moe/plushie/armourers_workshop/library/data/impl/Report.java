package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Report {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private final String userId;
    private final String skinId;

    private final ReportType reportType;
    private final String message;
    private final Date date;

    public Report(IODataObject object) {
        this.userId = object.get("userId").stringValue();
        this.skinId = object.get("reportSkinId").stringValue();
        this.reportType = ReportType.byName(object.get("reportType").stringValue());
        this.message = object.get("reportMessage").stringValue();
        this.date = toDate(object.get("date_created").stringValue());
    }

    private Date toDate(String value) {
        try {
            return SDF.parse(value);
        } catch (Exception exception) {
            return null;
        }
    }

    public String userId() {
        return userId;
    }

    public String skinId() {
        return skinId;
    }

    public ReportType reportType() {
        return reportType;
    }

    public String message() {
        return message;
    }

    public Date date() {
        return date;
    }

    @Override
    public String toString() {
        return "SkinReport[userId=" + userId + ", skinId=" + skinId + ", reportType=" + reportType + ", message=" + message + ", date=" + date + "]";
    }

}
