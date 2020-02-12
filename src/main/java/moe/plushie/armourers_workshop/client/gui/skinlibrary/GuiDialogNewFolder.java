package moe.plushie.armourers_workshop.client.gui.skinlibrary;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.GuiLabeledTextField;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDialogNewFolder extends AbstractGuiDialog {

    private GuiLabeledTextField textFolderName;
    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonCreate;
    private boolean invalidFolderName;

    public GuiDialogNewFolder(GuiScreen parent, String name, IDialogCallback callback, int width, int height) {
        super(parent, name, callback, width, height);
        this.slotHandler = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        textFolderName = new GuiLabeledTextField(fontRenderer, x + 10, y + 26, width - 20, 12);
        textFolderName.setMaxStringLength(30);
        textFolderName.setEmptyLabel(GuiHelper.getLocalizedControlName(name, "enterFolderName"));

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "close"));
        buttonCreate = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "create"));

        buttonList.add(buttonClose);
        buttonList.add(buttonCreate);

        textFolderName.setFocused(true);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonCreate) {
            if (!getFolderName().equals("")) {
                returnDialogResult(DialogResult.OK);
            }
        }
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        super.drawBackground(mouseX, mouseY, partialTickTime);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        textFolderName.drawTextBox();
        drawTitle();
        /*
         * String title = "Create New Folder"; int titleWidth =
         * fontRenderer.getStringWidth(title);
         * 
         * fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6,
         * 4210752);
         */
        if (invalidFolderName) {
            fontRenderer.drawSplitString(GuiHelper.getLocalizedControlName(name, "invalidFolderName"), x + 10, y + 45, width - 20, 0xFFFF6666);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        textFolderName.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            if (textFolderName.isFocused()) {
                textFolderName.setText("");
                checkFolderName();
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textFolderName.textboxKeyTyped(c, keycode)) {
            checkFolderName();
            return true;
        }
        if (keycode == 28) {
            if (!getFolderName().equals("") & !invalidFolderName) {
                returnDialogResult(DialogResult.OK);
            }
        }
        return super.keyTyped(c, keycode);
    }

    private void checkFolderName() {
        invalidFolderName = !SkinIOUtils.makeFileNameValid(getFolderName()).equals(getFolderName());
        buttonCreate.enabled = !invalidFolderName;
    }

    public String getFolderName() {
        return textFolderName.getText().trim();
    }
}
