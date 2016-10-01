package riskyken.armourersWorkshop.client.gui.globallibrary;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.controls.GuiLabeledTextField;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;

public class GuiGlobalLibraryPanelSearchBox extends GuiPanel {
    
    private GuiLabeledTextField searchTextbox;
    
    public GuiGlobalLibraryPanelSearchBox(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void initGui() {
        buttonList.clear();
        searchTextbox = new GuiLabeledTextField(fontRenderer, x + 5, y + 5, width - 10 - 85, 12);
        searchTextbox.setEmptyLabel("Type to search...");
        buttonList.add(new GuiButtonExt(0, x + width - 85, y + 3, 80, 16, "Search"));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.drawScreen(mouseX, mouseY, partialTickTime);
        searchTextbox.drawTextBox();
    }
}
