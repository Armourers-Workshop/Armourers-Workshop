package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiScreenWrapper extends GuiContainer {
    
    private final ModGuiContainer guiScreen;
    
    public GuiScreenWrapper(ModGuiContainer guiScreen) {
        super(guiScreen.getContainer());
        this.guiScreen = guiScreen;
    }
    
    @Override
    public void initGui() {
        buttonList.clear();
        guiScreen.initGui();
    }
    
    @Override
    public void updateScreen() {
        guiScreen.update();
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        guiScreen.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        guiScreen.mouseMovedOrUp(mouseX, mouseY, button);
    }
    
    @Override
    protected void keyTyped(char c, int keycode) {
        if (!guiScreen.keyTyped(c, keycode)) {
            super.keyTyped(c, keycode);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return guiScreen.doesGuiPauseGame();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        guiScreen.draw(mouseX, mouseY, partialTickTime);
    }
}
