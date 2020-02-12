package moe.plushie.armourers_workshop.client.gui.skinlibrary;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDialogDelete extends AbstractGuiDialog {

    private final boolean folder;
    private final String fileName;
    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonDelete;

    public GuiDialogDelete(GuiScreen parent, String name, IDialogCallback callback, int width, int height, boolean folder, String fileName) {
        super(parent, name, callback, width, height);
        this.folder = folder;
        this.fileName = fileName;
        this.slotHandler = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "close"));
        buttonDelete = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "delete"));

        buttonList.add(buttonClose);
        buttonList.add(buttonDelete);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonDelete) {
            returnDialogResult(DialogResult.OK);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        drawTitle();
        if (folder) {
            fontRenderer.drawSplitString(String.format(GuiHelper.getLocalizedControlName(name, "deleteFolder"), fileName), x + 10, y + 45, width - 20, 0xFFFF6666);
        } else {
            fontRenderer.drawSplitString(String.format(GuiHelper.getLocalizedControlName(name, "deleteFile"), fileName), x + 10, y + 45, width - 20, 0xFFFF6666);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isFolder() {
        return folder;
    }
}
