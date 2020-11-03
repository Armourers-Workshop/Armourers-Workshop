package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import com.google.common.util.concurrent.FutureCallback;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomLabel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskInfo;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskInfo.TaskData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelInfo extends GuiPanel {

    private static final String URL_DISCORD = "https://discord.gg/5Z3KKvU";
    private static final String URL_GITHUB = "https://github.com/RiskyKen/Armourers-Workshop";
    private static final String URL_REDDIT = "https://www.reddit.com/r/ArmourersWorkshop/";
    private static final String URL_DONATION = "https://ko-fi.com/riskyken";

    private final String guiName;

    private GuiCustomLabel statsText;
    private TaskData stats = null;

    private String failMessage = null;

    public GuiGlobalLibraryPanelInfo(GuiScreen parent) {
        super(parent, 0, 0, 1, 1);
        guiName = ((GuiGlobalLibrary) parent).getGuiName() + ".panel.info";
    }

    @Override
    public void initGui() {
        super.initGui();
        statsText = new GuiCustomLabel(fontRenderer, x + 2, y + 2, width - 4, height - 4);
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
    }

    public void updateInfo() {
        stats = null;
        failMessage = null;
        new GlobalTaskInfo().createTaskAndRun(new FutureCallback<GlobalTaskInfo.TaskData>() {

            @Override
            public void onSuccess(TaskData result) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        stats = result;
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                failMessage = t.getMessage();
            }
        });
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible | !enabled) {
            return false;
        }
        statsText.mouseClick(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        if (!this.visible) {
            return;
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableBlend();

        statsText.clearText();
        statsText.addLine(GuiHelper.getLocalizedControlName(guiName, "name"));
        statsText.addNewLine();

        if (stats != null) {
            statsText.addLine(GuiHelper.getLocalizedControlName(guiName, "total_skins", stats.getTotalSkin()));
            // statsText.addNewLine();
            // statsText.addLine(stats.getSqlData()[7]);
            statsText.addNewLine();
            statsText.addLine(GuiHelper.getLocalizedControlName(guiName, "download_count", stats.getDownloadsLastHour(), stats.getDownloadsLastDay(), stats.getDownloadsLastWeek()));
            statsText.addNewLine();
        } else {
            if (failMessage != null) {
                statsText.addLine(GuiHelper.getLocalizedControlName(guiName, "error_getting_stats"));
                statsText.addLine(failMessage);
            } else {
                statsText.addLine(GuiHelper.getLocalizedControlName(guiName, "loading"));
                // statsText.addNewLine();
                // statsText.addNewLine();
                statsText.addNewLine();
                statsText.addNewLine();
                statsText.addNewLine();
            }
        }

        statsText.addNewLine();
        statsText.addText(GuiHelper.getLocalizedControlName(guiName, "links"));
        statsText.addNewLine();
        statsText.addNewLine();

        statsText.addText(GuiHelper.getLocalizedControlName(guiName, "link.discord") + " ");
        statsText.addUrl(URL_DISCORD);
        statsText.addNewLine();
        statsText.addNewLine();

        statsText.addText(GuiHelper.getLocalizedControlName(guiName, "link.github") + " ");
        statsText.addUrl(URL_GITHUB);
        statsText.addNewLine();
        statsText.addNewLine();

        statsText.addText(GuiHelper.getLocalizedControlName(guiName, "link.reddit") + " ");
        statsText.addUrl(URL_REDDIT);
        statsText.addNewLine();
        statsText.addNewLine();

        statsText.addNewLine();

        statsText.addText(GuiHelper.getLocalizedControlName(guiName, "link.donation") + " ");
        statsText.addUrl(URL_DONATION);
        statsText.addNewLine();
        statsText.addNewLine();

        statsText.draw(mouseX, mouseY);
    }
}
