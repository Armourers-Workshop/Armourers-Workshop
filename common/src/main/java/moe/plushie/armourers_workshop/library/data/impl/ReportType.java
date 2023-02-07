package moe.plushie.armourers_workshop.library.data.impl;

public enum ReportType {
    SEXUAL, INAPPROPRIATE, STOLEN, SPAM, OTHER;

    public static ReportType byName(String name) {
        try {
            return ReportType.valueOf(name);
        } catch (Exception exception) {
            return ReportType.OTHER;
        }
    }

    public String getLangKey() {
        return "skin_report_type.armourers_workshop." + toString().toLowerCase();
    }
}

