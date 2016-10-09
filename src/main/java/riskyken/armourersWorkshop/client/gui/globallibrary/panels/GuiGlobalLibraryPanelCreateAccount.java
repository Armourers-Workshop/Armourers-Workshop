package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;

@SideOnly(Side.CLIENT)
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
        super.initGui();
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!visible | !enabled | haveOpenDialog()) {
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
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.draw(mouseX, mouseY, partialTickTime);
    }
}
