package moe.plushie.armourers_workshop.client.gui.globallibrary.dialog;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTextFieldCustom;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.library.global.task.user.GlobalTaskSkinReport.SkinReport;
import moe.plushie.armourers_workshop.common.library.global.task.user.GlobalTaskSkinReport.SkinReport.SkinReportType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiGlobalLibraryDialogReportSkin extends AbstractGuiDialog {

    private final int skinId;

    private GuiButtonExt buttonOk;
    private GuiButtonExt buttonCancel;
    private GuiDropDownList dropDownReportType;
    private GuiTextFieldCustom textReportMessage;

    public GuiGlobalLibraryDialogReportSkin(GuiScreen parent, String name, IDialogCallback callback, int width, int height, int skinId) {
        super(parent, name, callback, width, height);
        this.skinId = skinId;
        slotHandler = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonOk = new GuiButtonExt(0, x + width - 160 - 20, y + height - 30, 80, 20, I18n.format(LibGuiResources.Controls.BUTTON_OK));
        buttonCancel = new GuiButtonExt(0, x + width - 80 - 10, y + height - 30, 80, 20, I18n.format(LibGuiResources.Controls.BUTTON_CANCEL));
        dropDownReportType = new GuiDropDownList(0, x + 10, y + 25, width - 20, "", null);
        for (SkinReportType reportType : SkinReportType.values()) {
            dropDownReportType.addListItem(I18n.format(reportType.getLangKey()), reportType.toString(), true);
        }
        dropDownReportType.setListSelectedIndex(0);

        textReportMessage = new GuiTextFieldCustom(x + 10, y + 45, width - 20, 80);
        textReportMessage.setEmptyLabel(GuiHelper.getLocalizedControlName(name, "optional_message"));
        textReportMessage.setMaxStringLength(255);

        buttonList.add(buttonOk);
        buttonList.add(buttonCancel);
        buttonList.add(dropDownReportType);
    }

    @Override
    public void update() {
        super.update();
        textReportMessage.updateCursorCounter();
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        boolean typed = false;
        if (!dropDownReportType.getIsDroppedDown()) {
            if (textReportMessage.keyTyped(c, keycode)) {
                return true;
            }
        }
        return super.keyTyped(c, keycode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        textReportMessage.mouseClicked(mouseX, mouseY, button);
        if (!dropDownReportType.getIsDroppedDown()) {
            if (button == 1 & textReportMessage.isFocused()) {
                textReportMessage.setText("");
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonCancel) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonOk) {
            returnDialogResult(DialogResult.OK);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        GlStateManager.disableLighting();
        drawTitle();
        textReportMessage.drawButton(mc, mouseX, mouseY, partialTickTime);
        dropDownReportType.drawForeground(mc, mouseX, mouseY, partialTickTime);
        fontRenderer.drawSplitString(GuiHelper.getLocalizedControlName(name, "label.report_warning"), x + 10, y + 130, width - 20, 0x7F0000);
    }

    public SkinReport getSkinReport() {
        return new SkinReport(skinId, getReportType(), getReportMessage());
    }

    private SkinReportType getReportType() {
        return SkinReportType.valueOf(dropDownReportType.getListSelectedItem().tag);
    }

    private String getReportMessage() {
        return textReportMessage.getText().trim();
    }
}
