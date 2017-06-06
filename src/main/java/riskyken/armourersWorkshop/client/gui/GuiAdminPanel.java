package riskyken.armourersWorkshop.client.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiAdminPanel;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiAdminPanel.AdminPanelCommand;

public class GuiAdminPanel extends GuiScreen {
    
    protected final int guiWidth;
    protected final int guiHeight;
    
    protected int guiLeft;
    protected int guiTop;
    
    private GuiButtonExt recoverSkins;
    private GuiButtonExt reloadLibrary;
    
    public GuiAdminPanel() {
        this.guiWidth = 180;
        this.guiHeight = 128;
    }
    
    @Override
    public void initGui() {
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        
        buttonList.clear();
        
        recoverSkins = new GuiButtonExt(0, guiLeft + 5, guiTop + 5, 100, 15, "Recover Skins");
        reloadLibrary = new GuiButtonExt(0, guiLeft + 5, guiTop + 20, 100, 15, "Reload Library");
                
        buttonList.add(recoverSkins);
        buttonList.add(reloadLibrary);
    }
    
    @Override
    protected void keyTyped(char key, int keycode) {
        if (keycode == 1 || keycode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }
        super.keyTyped(key, keycode);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == recoverSkins) {
            MessageClientGuiAdminPanel message = new MessageClientGuiAdminPanel(AdminPanelCommand.RECOVER_SKINS);
            PacketHandler.networkWrapper.sendToServer(message);
        }
        if (button == reloadLibrary) {
            MessageClientGuiAdminPanel message = new MessageClientGuiAdminPanel(AdminPanelCommand.RELOAD_LIBRARY);
            PacketHandler.networkWrapper.sendToServer(message);
        }
    }
    
    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        drawGradientRect(this.guiLeft, this.guiTop, this.guiLeft + this.guiWidth, this.guiTop + guiHeight, 0xC0101010, 0xD0101010);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
