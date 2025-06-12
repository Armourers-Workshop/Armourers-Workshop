package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;

public class ServerStatus {

    private final int totalSkin;
    private final int downloadsLastHour;
    private final int downloadsLastDay;
    private final int downloadsLastWeek;
    private final float memUse;
    private final float cpuUse;

    public ServerStatus(IODataObject json) {
        // json.get("my_sql").allValues();
        totalSkin = json.get("total_skins").intValue();
        downloadsLastHour = json.get("downloads_last_hour").intValue();
        downloadsLastDay = json.get("downloads_last_day").intValue();
        downloadsLastWeek = json.get("downloads_last_week").intValue();
        memUse = json.get("mem_usage").floatValue();
        cpuUse = json.get("cpu_usage").floatValue();
    }

    public int totalSkin() {
        return totalSkin;
    }

    public int downloadsLastHour() {
        return downloadsLastHour;
    }

    public int downloadsLastDay() {
        return downloadsLastDay;
    }

    public int downloadsLastWeek() {
        return downloadsLastWeek;
    }

    public float memUse() {
        return memUse;
    }

    public float cpuUse() {
        return cpuUse;
    }
}
