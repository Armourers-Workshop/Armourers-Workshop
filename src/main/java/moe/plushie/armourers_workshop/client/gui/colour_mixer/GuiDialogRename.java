package moe.plushie.armourers_workshop.client.gui.colour_mixer;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.GuiLabeledTextField;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDialogRename extends AbstractGuiDialog {

    private GuiLabeledTextField textFieldName;
    private GuiButtonExt buttonOK;
    private GuiButtonExt buttonCancel;

    public GuiDialogRename(GuiScreen parent, String name, IDialogCallback callback, String oldName) {
        super(parent, name, callback, 240, 120);
        textFieldName = new GuiLabeledTextField(fontRenderer, x, y, width - 20, 20);
        textFieldName.setText(oldName);
        textFieldName.setEmptyLabel(GuiHelper.getLocalizedControlName(name, "enter_name"));
        slotHandler = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        textFieldName.x = x + 10;
        textFieldName.y = y + 50;

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
    public void update() {
        textFieldName.updateCursorCounter();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        textFieldName.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textFieldName.textboxKeyTyped(c, keycode)) {
            return true;
        }
        return super.keyTyped(c, keycode);
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        super.drawBackground(mouseX, mouseY, partialTickTime);
        textFieldName.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        drawTitle();
    }

    public String getNewName() {
        return textFieldName.getText();
    }
}
