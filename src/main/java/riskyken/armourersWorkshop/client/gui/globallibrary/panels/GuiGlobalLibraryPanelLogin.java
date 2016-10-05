package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;

public class GuiGlobalLibraryPanelLogin extends GuiPanel {

    private GuiTextField textboxUsername;
    private GuiTextField textboxPassword;
    private GuiButtonExt buttonLogin;
    
    public GuiGlobalLibraryPanelLogin(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        textboxUsername = new GuiTextField(fontRenderer, this.x + 5, this.y + 5, 120, 16);
        textboxPassword = new GuiTextField(fontRenderer, this.x + 5, this.y + 30, 120, 16);
        buttonLogin = new GuiButtonExt(0, this.x + 5, this.y + 50, 80, 16, "Login");
        buttonList.add(buttonLogin);
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible | !enabled | haveOpenDialog()) {
            return;
        }
        textboxUsername.mouseClicked(mouseX, mouseY, button);
        textboxPassword.mouseClicked(mouseX, mouseY, button);
        super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!visible | !enabled | haveOpenDialog()) {
            return false;
        }
        textboxUsername.textboxKeyTyped(c, keycode);
        textboxPassword.textboxKeyTyped(c, keycode);
        return super.keyTyped(c, keycode);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonLogin) {
            
        }
    }
    
    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.draw(mouseX, mouseY, partialTickTime);
        textboxUsername.drawTextBox();
        textboxPassword.drawTextBox();
    }

}
