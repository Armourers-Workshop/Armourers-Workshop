package riskyken.armourersWorkshop.client.gui.globallibrary.dialogs;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiDialog;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryDialogDownloadSkin extends GuiDialog {

    private GuiButtonExt buttonClose;
    
    public GuiGlobalLibraryDialogDownloadSkin(GuiPanel parent, int width, int height) {
        super(parent, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonClose = new GuiButtonExt(0, this.x + this.width - 85, this.y + this.height - 25, 80, 20, "Close");
        buttonList.add(buttonClose);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            closeDialog();
        }
    }
    
    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.draw(mouseX, mouseY, partialTickTime);
        fontRenderer.drawString("Download Dialog Test", this.x + 5, this.y + 5, 0xFFEEEEEE);
    }

}
