package moe.plushie.armourers_workshop.client.gui.globallibrary.dialog;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiGlobalLibraryDialogDelete extends AbstractGuiDialog {

    private GuiButtonExt buttonOk;
    private GuiButtonExt buttonCancel;

    public GuiGlobalLibraryDialogDelete(GuiScreen parent, String name, IDialogCallback callback, int width, int height) {
        super(parent, name, callback, width, height);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonOk = new GuiButtonExt(0, x + width - 160 - 20, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "ok"));
        buttonCancel = new GuiButtonExt(0, x + width - 80 - 10, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "cancel"));
        buttonList.add(buttonOk);
        buttonList.add(buttonCancel);
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
        drawTitle();
    }
}
