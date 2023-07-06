package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.core.client.gui.notification.UserNotificationCenter;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.client.gui.widget.ReportList;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.impl.Report;
import moe.plushie.armourers_workshop.library.data.impl.ReportFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ModerationLibraryPanel extends AbstractLibraryPanel implements ReportList.IEventListener {

    private final ReportList listReports = buildReportList();
    private final ArrayList<Report> skinReports = new ArrayList<>();
    private int pageIndex = 0;
    private boolean isRequesting = true;
    private final ReportFilter filter = ReportFilter.OPEN;

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
        Report report = skinReports.get(index);
        GlobalSkinLibrary.getInstance().getSkin(report.getSkinId(), (result, exception) -> {
            if (exception != null) {
                UserNotificationCenter.showToast(exception, "Error", null);
                return;
            }
            if (result != null) {
                router.showSkinDetail(result, GlobalSkinLibraryWindow.Page.LIBRARY_MODERATION);
            }
        });
    }

    @Override
    public void listDidScroll(ReportList reportList, CGPoint contentOffset) {
        if (isRequesting) {
            return;
        }
        if (contentOffset.getY() + reportList.frame().getHeight() * 1.5f >= reportList.getContentHeight()) {
            pageIndex += getMaxPerPage();
            loadReportList();
        }
    }

    private void loadReportList() {
        int pageIndex = this.pageIndex;
        isRequesting = true;
        GlobalSkinLibrary.getInstance().getReportList(pageIndex, getMaxPerPage(), filter, (result, exception) -> {
            if (result != null) {
                onPageLoad(pageIndex, result.getReports());
            }
        });
    }

    private void onPageLoad(int pageIndex, ArrayList<Report> reports) {
        if (pageIndex == 0) {
            listReports.clearItems();
        }
        ArrayList<String> names = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd MM:dd:HH");
        for (Report skinReport : reports) {
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
