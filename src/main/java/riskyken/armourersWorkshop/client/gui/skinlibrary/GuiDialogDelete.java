package riskyken.armourersWorkshop.client.gui.skinlibrary;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialog;

@SideOnly(Side.CLIENT)
public class GuiDialogDelete extends AbstractGuiDialog {

    private GuiButtonExt buttonClose;
    
    public GuiDialogDelete(GuiScreen parent, IDialogCallback callback, int width, int height) {
        super(parent, callback, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonClose = new GuiButtonExt(-1, x + 10, y + height - 30, 80, 20, "Close");
        buttonList.add(buttonClose);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
    }
    
    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        super.drawBackground(mouseX, mouseY, partialTickTime);
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        fontRenderer.drawString("delete test dialog", x + 5, y + 5, 4210752);
    }
}
