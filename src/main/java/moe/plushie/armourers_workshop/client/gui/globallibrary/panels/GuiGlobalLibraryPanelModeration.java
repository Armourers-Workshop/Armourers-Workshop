package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import com.google.common.util.concurrent.FutureCallback;

import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.common.library.global.task.mod.GlobalTaskGetReportList;
import moe.plushie.armourers_workshop.common.library.global.task.mod.GlobalTaskGetReportList.Filter;
import moe.plushie.armourers_workshop.common.library.global.task.mod.GlobalTaskGetReportList.Result;
import moe.plushie.armourers_workshop.common.library.global.task.user.GlobalTaskSkinReport.SkinReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiGlobalLibraryPanelModeration extends GuiPanel {

    private final String guiName;

    private int pageIndex = 0;;
    private Filter filter = Filter.OPEN;

    private Result result = null;

    public GuiGlobalLibraryPanelModeration(GuiScreen parent) {
        super(parent, 0, 0, 1, 1);
        guiName = ((GuiGlobalLibrary) parent).getGuiName() + ".panel.info";
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
            public void onSuccess(Result result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        GuiGlobalLibraryPanelModeration.this.result = result;
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
        
        if (result != null) {
            for (int i = 0; i < result.getSkinReports().size(); i++) {
                SkinReport skinReport = result.getSkinReports().get(i);
                String reportText = "skin id: " + skinReport.getSkinId() + " - user id: " + skinReport.getUserId() + " - type: " + skinReport.getReportType() + " - message: " + skinReport.getMessage();
                fontRenderer.drawString(reportText, x + 5, y + 5 + 10 * i, 0xFFFFFFFF);
            }
        }
    }
}
