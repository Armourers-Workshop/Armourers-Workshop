package riskyken.armourersWorkshop.client.gui.globallibrary;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;

public class GuiGlobalLibraryPanelCreateAccount extends GuiPanel {

    private GuiTextField textboxUsername;
    private GuiTextField textboxEmail;
    private GuiTextField textboxPassword1;
    private GuiTextField textboxPassword2;
    private GuiButtonExt buttonRegister;
    
    public GuiGlobalLibraryPanelCreateAccount(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void initGui() {
        
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!visible | !enabled) {
            return false;
        }
        
        textboxUsername.textboxKeyTyped(c, keycode);
        textboxEmail.textboxKeyTyped(c, keycode);
        textboxPassword1.textboxKeyTyped(c, keycode);
        textboxPassword2.textboxKeyTyped(c, keycode);
        
        return false;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonRegister) {
            
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.drawScreen(mouseX, mouseY, partialTickTime);
    }
}
