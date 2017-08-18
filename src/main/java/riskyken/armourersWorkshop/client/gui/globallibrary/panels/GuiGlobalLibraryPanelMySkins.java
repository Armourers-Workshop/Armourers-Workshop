package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;

public class GuiGlobalLibraryPanelMySkins extends GuiPanel {

    private static int iconScale = 110;
    
    public GuiGlobalLibraryPanelMySkins(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
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
