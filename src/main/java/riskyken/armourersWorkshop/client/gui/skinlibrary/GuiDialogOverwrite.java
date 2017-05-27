package riskyken.armourersWorkshop.client.gui.skinlibrary;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialog;

@SideOnly(Side.CLIENT)
public class GuiDialogOverwrite extends AbstractGuiDialog {

    private final String fileName;
    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonOK;
    
    public GuiDialogOverwrite(GuiScreen parent, IDialogCallback callback, int width, int height, String fileName) {
        super(parent, callback, width, height);
        this.fileName = fileName;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Close");
        buttonOK = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "OK");
        
        buttonList.add(buttonClose);
        buttonList.add(buttonOK);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonOK) {
            returnDialogResult(DialogResult.OK);
        }
    }
    
    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        super.drawBackground(mouseX, mouseY, partialTickTime);
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        String title = "Overwrite File";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        fontRenderer.drawSplitString(String.format("Overwrite file %s.", fileName), x + 10, y + 45, width - 20, 0xFFFF6666);
    }
    
    public String getFileName() {
        return fileName;
    }
}
