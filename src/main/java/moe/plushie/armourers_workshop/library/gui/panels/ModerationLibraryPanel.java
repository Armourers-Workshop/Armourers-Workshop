package moe.plushie.armourers_workshop.library.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskGetSkinInfo;
import moe.plushie.armourers_workshop.library.data.global.task.mod.GlobalTaskGetReportList;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinReport;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen.Page;
import moe.plushie.armourers_workshop.library.gui.widget.ReportList;
import moe.plushie.armourers_workshop.library.gui.widget.SkinFileList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ModerationLibraryPanel extends AbstractLibraryPanel implements ReportList.IEventListener {

    private int pageIndex = 0;
    private boolean isRequesting = true;

    private final ReportList listReports = buildReportList();
    private final ArrayList<GlobalTaskSkinReport.SkinReport> skinReports = new ArrayList<>();

    private GlobalTaskGetReportList.Filter filter = GlobalTaskGetReportList.Filter.OPEN;

    public ModerationLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.panel.info", Page.LIBRARY_MODERATION::equals);
    }

    @Override
    protected void init() {
        super.init();
        listReports.setFrame(leftPos + 5, topPos + 20, width - 10, height - 25);
        addButton(listReports);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
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
                    SkinFileList.Entry entry = new SkinFileList.Entry(result);
                    Minecraft.getInstance().execute(() -> router.showSkinDetail(entry, Page.LIBRARY_MODERATION));
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
        if (contentOffset + reportList.getHeight() * 1.5f >= reportList.getContentHeight()) {
            pageIndex += getMaxPerPage();
            loadReportList();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && skinReports.isEmpty()) {
            pageIndex = 0;
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
        if (reports.size() == getMaxPerPage()) {
            isRequesting = false;
        }
    }

    private ReportList buildReportList() {
        ReportList reportList = new ReportList(0, 0, 240, 120);
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
