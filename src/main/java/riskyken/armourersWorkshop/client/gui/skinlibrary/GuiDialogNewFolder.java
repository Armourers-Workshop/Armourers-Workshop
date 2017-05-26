package riskyken.armourersWorkshop.client.gui.skinlibrary;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialog;
import riskyken.armourersWorkshop.client.gui.controls.GuiLabeledTextField;

@SideOnly(Side.CLIENT)
public class GuiDialogNewFolder extends AbstractGuiDialog {
    
    private GuiLabeledTextField textFolderName;
    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonCreate;
    
    public GuiDialogNewFolder(GuiScreen parent, IDialogCallback callback, int width, int height) {
        super(parent, callback, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        textFolderName = new GuiLabeledTextField(fontRenderer, x + 10, y + 26, width - 20, 12);
        textFolderName.setMaxStringLength(30);
        textFolderName.setEmptyLabel("Enter folder name");
        
        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, "Close");
        buttonCreate = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, "Create");
        
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
        String title = "Create New Folder";
        int titleWidth = fontRenderer.getStringWidth(title);
        
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        textFolderName.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            if (textFolderName.isFocused()) {
                textFolderName.setText("");
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textFolderName.textboxKeyTyped(c, keycode)) {
            return true;
        }
        if (keycode == 28) {
            if (!getFolderName().equals("")) {
                returnDialogResult(DialogResult.OK);
            }
        }
        return super.keyTyped(c, keycode);
    }
    
    public String getFolderName() {
        return textFolderName.getText().trim();
    }
}
