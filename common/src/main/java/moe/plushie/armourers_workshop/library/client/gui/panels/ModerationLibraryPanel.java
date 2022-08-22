package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGRect;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.client.gui.widget.ReportList;
import moe.plushie.armourers_workshop.library.client.gui.widget.SkinItemList;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskGetSkinInfo;
import moe.plushie.armourers_workshop.library.data.global.task.mod.GlobalTaskGetReportList;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinReport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class ModerationLibraryPanel extends AbstractLibraryPanel implements ReportList.IEventListener {

    private final ReportList listReports = buildReportList();
    private final ArrayList<GlobalTaskSkinReport.SkinReport> skinReports = new ArrayList<>();
    private int pageIndex = 0;
    private boolean isRequesting = true;
    private final GlobalTaskGetReportList.Filter filter = GlobalTaskGetReportList.Filter.OPEN;

    public ModerationLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.panel.info", GlobalSkinLibraryWindow.Page.LIBRARY_MODERATION::equals);
        this.listReports.setFrame(bounds().insetBy(5, 5, 5, 5));
        this.listReports.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        this.addSubview(listReports);
    }

    @Override
    public void refresh() {
        super.refresh();
        if (skinReports.isEmpty()) {
            pageIndex = 0;
            loadReportList();
        }
    }

    @Override
    public void listDidSelect(ReportList reportList, int index) {
        if (index < 0 || index >= skinReports.size()) {
            return;
        }
        GlobalTaskSkinReport.SkinReport report = skinReports.get(index);
        new GlobalTaskGetSkinInfo(report.getSkinId()).createTaskAndRun(new FutureCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject result) {
                if (result != null) {
                    SkinItemList.Entry entry = new SkinItemList.Entry(result);
                    Minecraft.getInstance().execute(() -> router.showSkinDetail(entry, GlobalSkinLibraryWindow.Page.LIBRARY_MODERATION));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void listDidScroll(ReportList reportList, int contentOffset) {
        if (isRequesting) {
            return;
        }
        if (contentOffset + reportList.frame().getHeight() * 1.5f >= reportList.getContentHeight()) {
            pageIndex += getMaxPerPage();
            loadReportList();
        }
    }

    private void loadReportList() {
        int pageIndex = this.pageIndex;
        isRequesting = true;
        new GlobalTaskGetReportList(pageIndex, getMaxPerPage(), filter).createTaskAndRun(new FutureCallback<GlobalTaskGetReportList.Result>() {

            @Override
            public void onSuccess(GlobalTaskGetReportList.Result result1) {
                Minecraft.getInstance().execute(() -> onPageLoad(pageIndex, result1.getSkinReports()));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void onPageLoad(int pageIndex, ArrayList<GlobalTaskSkinReport.SkinReport> reports) {
        if (pageIndex == 0) {
            listReports.clearItems();
        }
        ArrayList<String> names = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd MM:dd:HH");
        for (GlobalTaskSkinReport.SkinReport skinReport : reports) {
            names.add(sdf.format(skinReport.getDate()));
            names.add(String.valueOf(skinReport.getUserId()));
            names.add(String.valueOf(skinReport.getSkinId()));
            names.add(I18n.get(skinReport.getReportType().getLangKey()));
            names.add(skinReport.getMessage());
            listReports.addItem(names.toArray(new String[5]));
            names.clear();
        }
        skinReports.addAll(reports);
        if (reports.size() >= getMaxPerPage()) {
            isRequesting = false;
        }
    }

    private ReportList buildReportList() {
        ReportList reportList = new ReportList(new CGRect(0, 0, 240, 120));
        reportList.addColumn("date", 106);
        reportList.addColumn("userId", 40);
        reportList.addColumn("skinId", 40);
        reportList.addColumn("reportType", 72);
        reportList.addColumn("message", -1);
        reportList.setListener(this);
        return reportList;
    }

    private int getMaxPerPage() {
        return 50;
    }
}
