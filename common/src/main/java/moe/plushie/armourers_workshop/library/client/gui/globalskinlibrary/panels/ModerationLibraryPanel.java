package moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.panels;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import moe.plushie.armourers_workshop.core.client.gui.notification.UserNotificationCenter;
import moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary.GlobalSkinLibraryWindow;
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
        super("skin-library-global.panel.info", GlobalSkinLibraryWindow.Page.LIBRARY_MODERATION::equals);
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
        GlobalSkinLibrary.getInstance().getSkin(report.skinId(), (result, exception) -> {
            if (exception != null) {
                UserNotificationCenter.showToast(exception, NSString.localizedString("common.text.error"), null);
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
        if (contentOffset.y() + reportList.frame().height() * 1.5f >= reportList.contentHeight()) {
            pageIndex += maxPerPage();
            loadReportList();
        }
    }

    private void loadReportList() {
        int pageIndex = this.pageIndex;
        isRequesting = true;
        GlobalSkinLibrary.getInstance().getReportList(pageIndex, maxPerPage(), filter, (result, exception) -> {
            if (result != null) {
                onPageLoad(pageIndex, result.reports());
            }
        });
    }

    private void onPageLoad(int pageIndex, ArrayList<Report> reports) {
        if (pageIndex == 0) {
            listReports.clearItems();
        }
        var names = new ArrayList<String>();
        var sdf = new SimpleDateFormat("yyyy-MM-dd MM:dd:HH");
        for (var skinReport : reports) {
            names.add(sdf.format(skinReport.date()));
            names.add(String.valueOf(skinReport.userId()));
            names.add(String.valueOf(skinReport.skinId()));
            names.add(I18n.get(skinReport.reportType().toLangKey()));
            names.add(skinReport.message());
            listReports.addItem(names.toArray(new String[5]));
            names.clear();
        }
        skinReports.addAll(reports);
        if (reports.size() >= maxPerPage()) {
            isRequesting = false;
        }
    }

    private ReportList buildReportList() {
        var reportList = new ReportList(new CGRect(0, 0, 240, 120));
        reportList.addColumn("date", 106);
        reportList.addColumn("userId", 40);
        reportList.addColumn("skinId", 40);
        reportList.addColumn("reportType", 72);
        reportList.addColumn("message", -1);
        reportList.setListener(this);
        return reportList;
    }

    private int maxPerPage() {
        return 50;
    }
}
