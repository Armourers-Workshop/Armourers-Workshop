package moe.plushie.armourers_workshop.common.library.global.task.mod;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;

import moe.plushie.armourers_workshop.common.library.global.MultipartForm;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTask;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskResult;
import moe.plushie.armourers_workshop.common.library.global.task.user.GlobalTaskSkinReport;
import moe.plushie.armourers_workshop.common.library.global.task.user.GlobalTaskSkinReport.SkinReport;
import moe.plushie.armourers_workshop.common.library.global.task.user.GlobalTaskSkinReport.SkinReport.SkinReportType;
import moe.plushie.armourers_workshop.utils.ModLogger;

public class GlobalTaskGetReportList extends GlobalTask<GlobalTaskGetReportList.Result> {

    private static final String URL = "mod-get-skin-reports.php";

    private final int page;
    private final int count;
    private final GlobalTaskGetReportList.Filter filter;

    public GlobalTaskGetReportList(int page, int count, GlobalTaskGetReportList.Filter filter) {
        super(PlushieAction.GET_REPORT_LIST, true);
        this.page = page;
        this.count = count;
        this.filter = filter;
    }

    @Override
    public GlobalTaskGetReportList.Result call() throws Exception {
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

        ModLogger.log(downloadString);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        JsonObject jsonObject = new JsonParser().parse(downloadString).getAsJsonObject();
        if (jsonObject.has("valid")) {
            boolean valid = jsonObject.get("valid").getAsBoolean();
            if (valid) {
                ArrayList<GlobalTaskSkinReport.SkinReport> skinReports = new ArrayList<GlobalTaskSkinReport.SkinReport>();
                JsonArray results = jsonObject.get("results").getAsJsonArray();
                for (int i = 0; i < results.size(); i++) {
                    JsonObject result = results.get(i).getAsJsonObject();
                    int userId = result.get("userId").getAsInt();
                    int reportSkinId = result.get("reportSkinId").getAsInt();

                    SkinReportType skinReportType = SkinReportType.OTHER;
                    try {
                        skinReportType = SkinReportType.valueOf(result.get("reportType").getAsString());
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

                    skinReports.add(new SkinReport(userId, reportSkinId, skinReportType, reportMessage, date));
                }
                return new GlobalTaskGetReportList.Result(GlobalTaskResult.SUCCESS, jsonObject.toString(), skinReports);
            }
            if (jsonObject.has("reason")) {
                return new GlobalTaskGetReportList.Result(GlobalTaskResult.FAILED, jsonObject.get("reason").getAsString(), new ArrayList<GlobalTaskSkinReport.SkinReport>());
            }
        }

        return new GlobalTaskGetReportList.Result(GlobalTaskResult.FAILED, downloadString, new ArrayList<GlobalTaskSkinReport.SkinReport>());
    }

    public enum Filter {
        OPEN, CLOSED, ALL
    }

    public class Result {

        private GlobalTaskResult result;
        private String message;
        private ArrayList<SkinReport> skinReports;

        public Result(GlobalTaskResult result, String message, ArrayList<SkinReport> skinReports) {
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

        public ArrayList<SkinReport> getSkinReports() {
            return skinReports;
        }
    }
}
