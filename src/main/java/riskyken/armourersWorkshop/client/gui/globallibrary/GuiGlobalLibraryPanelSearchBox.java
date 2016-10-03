package riskyken.armourersWorkshop.client.gui.globallibrary;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
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
        searchTextbox = new GuiLabeledTextField(0, fontRenderer, x + 5, y + 5, width - 10 - 85, 12);
        searchTextbox.setEmptyLabel("Type to search...");
        buttonList.add(new GuiButtonExt(0, x + width - 85, y + 3, 80, 16, "Search"));
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible | !enabled) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
        searchTextbox.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            if (searchTextbox.isFocused()) {
                searchTextbox.setText("");
            }
        }
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!visible | !enabled) {
            return false;
        }
        boolean pressed = searchTextbox.textboxKeyTyped(c, keycode);
        if (keycode == 28) {
            ((GuiGlobalLibrary)parent).preformSearch(searchTextbox.getText());
        }
        return pressed;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        ((GuiGlobalLibrary)parent).preformSearch(searchTextbox.getText());
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.drawScreen(mouseX, mouseY, partialTickTime);
        searchTextbox.drawTextBox();
    }


}
