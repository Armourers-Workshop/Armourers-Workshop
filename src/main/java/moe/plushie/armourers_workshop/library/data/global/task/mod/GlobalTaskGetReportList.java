package moe.plushie.armourers_workshop.library.data.global.task.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.data.global.MultipartForm;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.library.data.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.library.data.global.permission.PermissionSystem;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTask;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskResult;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GlobalTaskGetReportList extends GlobalTask<GlobalTaskGetReportList.Result> {

    private static final String URL = "mod-get-skin-reports.php";

    private final int page;
    private final int count;
    private final Filter filter;

    public GlobalTaskGetReportList(int page, int count, Filter filter) {
        super(PermissionSystem.PlushieAction.GET_REPORT_LIST, true);
        this.page = page;
        this.count = count;
        this.filter = filter;
    }

    @Override
    public Result call() throws Exception {
        permissionCheck();
        if (!authenticateUser()) {
            throw new AuthenticationException();
        }

        PlushieSession plushieSession = PlushieAuth.PLUSHIE_SESSION;
        MultipartForm multipartForm = new MultipartForm(getBaseUrl() + URL);
        multipartForm.addText("userId", String.valueOf(plushieSession.getServerId()));
        multipartForm.addText("accessToken", plushieSession.getAccessToken());

        multipartForm.addText("page", String.valueOf(page));
        multipartForm.addText("size", String.valueOf(count));
        multipartForm.addText("filter", filter.toString().toLowerCase());

        String downloadString = multipartForm.upload();

        ModLog.debug(downloadString);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        JsonObject jsonObject = new JsonParser().parse(downloadString).getAsJsonObject();
        if (jsonObject.has("valid")) {
            boolean valid = jsonObject.get("valid").getAsBoolean();
            if (valid) {
                ArrayList<GlobalTaskSkinReport.SkinReport> skinReports = new ArrayList<>();
                JsonArray results = jsonObject.get("results").getAsJsonArray();
                for (int i = 0; i < results.size(); i++) {
                    JsonObject result = results.get(i).getAsJsonObject();
                    int userId = result.get("userId").getAsInt();
                    int reportSkinId = result.get("reportSkinId").getAsInt();

                    GlobalTaskSkinReport.SkinReport.SkinReportType skinReportType = GlobalTaskSkinReport.SkinReport.SkinReportType.OTHER;
                    try {
                        skinReportType = GlobalTaskSkinReport.SkinReport.SkinReportType.valueOf(result.get("reportType").getAsString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String reportMessage = result.get("reportMessage").getAsString();
                    String dateCreated = result.get("date_created").getAsString();
                    Date date = null;
                    try {
                        date = sdf.parse(dateCreated);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    skinReports.add(new GlobalTaskSkinReport.SkinReport(userId, reportSkinId, skinReportType, reportMessage, date));
                }
                return new Result(GlobalTaskResult.SUCCESS, jsonObject.toString(), skinReports);
            }
            if (jsonObject.has("reason")) {
                return new Result(GlobalTaskResult.FAILED, jsonObject.get("reason").getAsString(), new ArrayList<>());
            }
        }

        return new Result(GlobalTaskResult.FAILED, downloadString, new ArrayList<>());
    }

    public enum Filter {
        OPEN, CLOSED, ALL
    }

    public static class Result {

        private final GlobalTaskResult result;
        private final String message;
        private final ArrayList<GlobalTaskSkinReport.SkinReport> skinReports;

        public Result(GlobalTaskResult result, String message, ArrayList<GlobalTaskSkinReport.SkinReport> skinReports) {
            this.result = result;
            this.message = message;
            this.skinReports = skinReports;
        }

        public GlobalTaskResult getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }

        public ArrayList<GlobalTaskSkinReport.SkinReport> getSkinReports() {
            return skinReports;
        }
    }
}
