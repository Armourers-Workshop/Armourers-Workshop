package moe.plushie.armourers_workshop.client.gui;

import java.io.IOException;

import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiAdminPanel;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiAdminPanel.AdminPanelCommand;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiAdminPanel extends GuiScreen {
    
    private static final String GUI_NAME = "admin-panel";
    
    protected final int guiWidth;
    protected final int guiHeight;
    
    protected int guiLeft;
    protected int guiTop;
    
    public GuiAdminPanel() {
        this.guiWidth = 320;
        this.guiHeight = 240;
        
    }
    
    @Override
    public void initGui() {
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        
        buttonList.clear();
        
        for (EnumButtons button : EnumButtons.values()) {
            buttonList.add(new GuiButtonExt(button.ordinal(), guiLeft + button.x, guiTop + button.y, 100, 15, GuiHelper.getLocalizedControlName(GUI_NAME, button.name().toLowerCase())));
        }
    }
    
    @Override
    protected void keyTyped(char key, int keycode) throws IOException {
        if (keycode == 1 || keycode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.player.closeScreen();
        }
        super.keyTyped(key, keycode);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == EnumButtons.RECOVER_SKINS.ordinal()) {
            MessageClientGuiAdminPanel message = new MessageClientGuiAdminPanel(AdminPanelCommand.RECOVER_SKINS);
            PacketHandler.networkWrapper.sendToServer(message);
        }
        if (button.id == EnumButtons.RELOAD_LIBRARY.ordinal()) {
            MessageClientGuiAdminPanel message = new MessageClientGuiAdminPanel(AdminPanelCommand.RELOAD_LIBRARY);
            PacketHandler.networkWrapper.sendToServer(message);
        }
        if (button.id == EnumButtons.UPDATE_SKINS.ordinal()) {
            MessageClientGuiAdminPanel message = new MessageClientGuiAdminPanel(AdminPanelCommand.UPDATE_SKINS);
            PacketHandler.networkWrapper.sendToServer(message);
        }
        if (button.id == EnumButtons.CLEAR_CACHE.ordinal()) {
            MessageClientGuiAdminPanel message = new MessageClientGuiAdminPanel(AdminPanelCommand.RELOAD_CACHE);
            PacketHandler.networkWrapper.sendToServer(message);
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawGradientRect(this.guiLeft, this.guiTop, this.guiLeft + this.guiWidth, this.guiTop + guiHeight, 0xC0101010, 0xD0101010);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    private enum EnumButtons {
        RECOVER_SKINS(5, 5),
        RELOAD_LIBRARY(5, 25),
        UPDATE_SKINS(5, 45),
        CLEAR_CACHE(5, 65);
        
        private final int x;
        private final int y;
        
        private EnumButtons(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
