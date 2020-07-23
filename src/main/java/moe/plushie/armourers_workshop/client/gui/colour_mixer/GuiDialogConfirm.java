package moe.plushie.armourers_workshop.client.gui.colour_mixer;

import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDialogConfirm extends AbstractGuiDialog {

    private GuiButtonExt buttonOK;
    private GuiButtonExt buttonCancel;
    private String message;

    public GuiDialogConfirm(GuiScreen parent, String name, IDialogCallback callback, String message) {
        super(parent, name, callback, 240, 120);
        this.message = message;
        slotHandler = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonOK = new GuiButtonExt(-1, x + width - 220, y + height - 30, 100, 20, I18n.format(LibGuiResources.Controls.BUTTON_OK));
        buttonList.add(buttonOK);

        buttonCancel = new GuiButtonExt(-1, x + width - 110, y + height - 30, 100, 20, I18n.format(LibGuiResources.Controls.BUTTON_CANCEL));
        buttonList.add(buttonCancel);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonOK) {
            returnDialogResult(DialogResult.OK);
        }
        if (button == buttonCancel) {
            returnDialogResult(DialogResult.CANCEL);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        fontRenderer.drawSplitString(message, x + 10, y + 30, width - 20, 0xFF404040);
        drawTitle();
    }
}
