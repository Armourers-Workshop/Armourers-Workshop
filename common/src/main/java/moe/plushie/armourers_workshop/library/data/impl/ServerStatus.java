package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.api.data.IDataPackObject;

public class ServerStatus {

    private final int totalSkin;
    private final int downloadsLastHour;
    private final int downloadsLastDay;
    private final int downloadsLastWeek;
    private final float memUse;
    private final float cpuUse;

    public ServerStatus(IDataPackObject json) {
        // json.get("my_sql").allValues();
        totalSkin = json.get("total_skins").intValue();
        downloadsLastHour = json.get("downloads_last_hour").intValue();
        downloadsLastDay = json.get("downloads_last_day").intValue();
        downloadsLastWeek = json.get("downloads_last_week").intValue();
        memUse = json.get("mem_usage").floatValue();
        cpuUse = json.get("cpu_usage").floatValue();
    }

    public int getTotalSkin() {
        return totalSkin;
    }

    public int getDownloadsLastHour() {
        return downloadsLastHour;
    }

    public int getDownloadsLastDay() {
        return downloadsLastDay;
    }

    public int getDownloadsLastWeek() {
        return downloadsLastWeek;
    }

    public float getMemUse() {
        return memUse;
    }

    public float getCpuUse() {
        return cpuUse;
    }
}
