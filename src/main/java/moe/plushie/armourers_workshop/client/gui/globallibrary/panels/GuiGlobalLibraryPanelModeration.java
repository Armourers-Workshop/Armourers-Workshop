package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.common.util.concurrent.FutureCallback;

import moe.plushie.armourers_workshop.client.gui.controls.GuiControlDetailList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.common.library.global.task.mod.GlobalTaskGetReportList;
import moe.plushie.armourers_workshop.common.library.global.task.mod.GlobalTaskGetReportList.Filter;
import moe.plushie.armourers_workshop.common.library.global.task.user.GlobalTaskSkinReport.SkinReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiGlobalLibraryPanelModeration extends GuiPanel {

    private final String guiName;
    private final GuiControlDetailList listReports;

    private int pageIndex = 0;;
    private Filter filter = Filter.OPEN;

    private GlobalTaskGetReportList.Result result = null;

    public GuiGlobalLibraryPanelModeration(GuiScreen parent) {
        super(parent, 0, 0, 1, 1);
        guiName = ((GuiGlobalLibrary) parent).getGuiName() + ".panel.info";
        
        
        listReports = new GuiControlDetailList(x + 5, y + 20, width - 10, height - 25);
        listReports.addColumn("date", 106);
        listReports.addColumn("userId", 40);
        listReports.addColumn("skinId", 40);
        listReports.addColumn("reportType", 72);
        listReports.addColumn("message", -1);
        
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        listReports.setPosAndSize(x + 5, y + 20, width - 10, height - 25);
        buttonList.add(listReports);
    }

    @Override
    public GuiPanel setVisible(boolean visible) {
        if (visible & !this.visible) {
            pageIndex = 0;
            getReportList();
        }
        return super.setVisible(visible);
    }

    private int getMaxPerPage() {
        return 20;
    }

    private void getReportList() {
        result = null;
        new GlobalTaskGetReportList(pageIndex, getMaxPerPage(), filter).createTaskAndRun(new FutureCallback<GlobalTaskGetReportList.Result>() {

            @Override
            public void onSuccess(GlobalTaskGetReportList.Result result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        GuiGlobalLibraryPanelModeration.this.result = result;
                        listReports.clearItems();
                        ArrayList<String> names = new ArrayList<String>();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd MM:dd:HH");
                        for (SkinReport skinReport : result.getSkinReports()) {
                            names.add(sdf.format(skinReport.getDate()));
                            names.add(String.valueOf(skinReport.getUserId()));
                            names.add(String.valueOf(skinReport.getSkinId()));
                            names.add(I18n.format(skinReport.getReportType().getLangKey()));
                            names.add(skinReport.getMessage());
                            listReports.addItem(names.toArray(new String[names.size()]));
                            names.clear();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.draw(mouseX, mouseY, partialTickTime);
    }
}
