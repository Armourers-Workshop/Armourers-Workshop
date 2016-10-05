package riskyken.armourersWorkshop.client.gui.globallibrary;

import com.google.gson.JsonObject;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;

public class GuiGlobalLibraryPanelSkinInfo extends GuiPanel {

    private GuiButtonExt buttonBack;
    private JsonObject skinJson = null;
    
    public GuiGlobalLibraryPanelSkinInfo(GuiScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void initGui() {
        
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        drawString(fontRenderer, "Skin Info", this.x + 5, this.y + 5, 0xFFEEEEEE);
        
        if (skinJson != null) {
            drawString(fontRenderer, "id: " + skinJson.get("id").getAsInt(), this.x + 5, this.y + 5 + 12 * 1, 0xFFEEEEEE);
            drawString(fontRenderer, "name: " + skinJson.get("name").getAsString(), this.x + 5, this.y + 5 + 12 * 2, 0xFFEEEEEE);
            drawString(fontRenderer, "file name: " + skinJson.get("file_name").getAsString(), this.x + 5, this.y + 5 + 12 * 3, 0xFFEEEEEE);
        }
        
        super.drawScreen(mouseX, mouseY, partialTickTime);
    }
    
    public void displaySkinInfo(JsonObject jsonObject) {
        skinJson = jsonObject;
        ((GuiGlobalLibrary)parent).switchScreen(Screen.SKIN_INFO);
    }
}
