package moe.plushie.armourers_workshop.library.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.library.data.global.task.mod.GlobalTaskGetReportList;
import moe.plushie.armourers_workshop.library.data.global.task.user.GlobalTaskSkinReport;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen.Page;
import moe.plushie.armourers_workshop.library.gui.widget.ReportList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ModerationLibraryPanel extends AbstractLibraryPanel {

    private int pageIndex = 0;

    private ReportList listReports;
    private GlobalTaskGetReportList.Filter filter = GlobalTaskGetReportList.Filter.OPEN;

    public ModerationLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.panel.info", Page.LIBRARY_MODERATION::equals);
    }

    @Override
    protected void init() {
        super.init();

        listReports = new ReportList(leftPos + 5, topPos + 20, width - 10, height - 25);
        listReports.addColumn("date", 106);
        listReports.addColumn("userId", 40);
        listReports.addColumn("skinId", 40);
        listReports.addColumn("reportType", 72);
        listReports.addColumn("message", -1);
        addButton(listReports);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            pageIndex = 0;
            loadReportList();
        }
    }

    private void loadReportList() {
        new GlobalTaskGetReportList(pageIndex, getMaxPerPage(), filter).createTaskAndRun(new FutureCallback<GlobalTaskGetReportList.Result>() {

            @Override
            public void onSuccess(GlobalTaskGetReportList.Result result1) {
                Minecraft.getInstance().execute(() -> onPageLoad(result1.getSkinReports()));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void onPageLoad(ArrayList<GlobalTaskSkinReport.SkinReport> reports) {
        listReports.clearItems();
        ArrayList<String> names = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd MM:dd:HH");
        for (GlobalTaskSkinReport.SkinReport skinReport : reports) {
            names.add(sdf.format(skinReport.getDate()));
            names.add(String.valueOf(skinReport.getUserId()));
            names.add(String.valueOf(skinReport.getSkinId()));
            names.add(I18n.get(skinReport.getReportType().getLangKey()));
            names.add(skinReport.getMessage());
            listReports.addItem(names.toArray(new String[5]));
        }
    }

    private int getMaxPerPage() {
        return 20;
    }
}
