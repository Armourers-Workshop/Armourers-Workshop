package riskyken.armourersWorkshop.client.gui.skinlibrary;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialog;

@SideOnly(Side.CLIENT)
public class GuiDialogDelete extends AbstractGuiDialog {

    private final boolean folder;
    private final String name;
    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonDelete;
    
    public GuiDialogDelete(GuiScreen parent, IDialogCallback callback, int width, int height, boolean folder, String name) {
        super(parent, callback, width, height);
        this.folder = folder;
        this.name = name;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Close");
        buttonDelete = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "Delete");
        
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
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        super.drawBackground(mouseX, mouseY, partialTickTime);
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        String title = "Delete";
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
        if (folder) {
            fontRenderer.drawSplitString(String.format("Delete folder %s and all the files it contains.", name), x + 10, y + 45, width - 20, 0xFFFF6666);
        } else {
            fontRenderer.drawSplitString(String.format("Delete file %s.", name), x + 10, y + 45, width - 20, 0xFFFF6666);
        }
    }
    
    public String getName() {
        return name;
    }
    public boolean isFolder() {
        return folder;
    }
}
