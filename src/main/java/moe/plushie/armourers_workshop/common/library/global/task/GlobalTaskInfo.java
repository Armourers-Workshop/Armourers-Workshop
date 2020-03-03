package moe.plushie.armourers_workshop.common.library.global.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskInfo.TaskData;

public class GlobalTaskInfo extends GlobalTask<TaskData> {

    private final String URL = "stats.php";

    public GlobalTaskInfo() {
        super(PlushieAction.SERVER_VIEW_STATS, false);
    }

    @Override
    public TaskData call() throws Exception {
        permissionCheck();
        String url = getBaseUrl() + URL;
        String data = downloadString(url);
        JsonObject json = new JsonParser().parse(data).getAsJsonObject();

        String[] sqlData = null;
        int totalSkin = 0;
        int downloadsLastHour = 0;
        int downloadsLastDay = 0;
        int downloadsLastWeek = 0;
        float memUse = 0F;
        float cpuUse = 0F;

        if (json.has("my_sql")) {
            JsonArray array = json.get("my_sql").getAsJsonArray();
            sqlData = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                sqlData[i] = array.get(i).getAsString();
            }
        }
        if (json.has("total_skins")) {
            totalSkin = json.get("total_skins").getAsInt();
        }
        if (json.has("downloads_last_hour")) {
            downloadsLastHour = json.get("downloads_last_hour").getAsInt();
        }
        if (json.has("downloads_last_day")) {
            downloadsLastDay = json.get("downloads_last_day").getAsInt();
        }
        if (json.has("downloads_last_week")) {
            downloadsLastWeek = json.get("downloads_last_week").getAsInt();
        }
        if (json.has("mem_usage")) {
            memUse = json.get("mem_usage").getAsFloat();
        }
        if (json.has("cpu_usage")) {
            cpuUse = json.get("cpu_usage").getAsFloat();
        }

        return new TaskData(sqlData, totalSkin, downloadsLastHour, downloadsLastDay, downloadsLastWeek, memUse, cpuUse);
    }

    public class TaskData {

        private final String[] sqlData;
        private final int totalSkin;
        private final int downloadsLastHour;
        private final int downloadsLastDay;
        private final int downloadsLastWeek;
        private final float memUse;
        private final float cpuUse;

        public TaskData(String[] sqlData, int totalSkin, int downloadsLastHour, int downloadsLastDay, int downloadsLastWeek, float memUse, float cpuUse) {
            this.sqlData = sqlData;
            this.totalSkin = totalSkin;
            this.downloadsLastHour = downloadsLastHour;
            this.downloadsLastDay = downloadsLastDay;
            this.downloadsLastWeek = downloadsLastWeek;
            this.memUse = memUse;
            this.cpuUse = cpuUse;
        }

        public String[] getSqlData() {
            return sqlData;
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
}
