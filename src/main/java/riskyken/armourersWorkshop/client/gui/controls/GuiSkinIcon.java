package riskyken.armourersWorkshop.client.gui.controls;

import com.google.gson.JsonObject;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class GuiSkinIcon extends GuiButtonExt {

    private final JsonObject skinJson;
    
    public GuiSkinIcon(int id, int xPos, int yPos, int width, int height, JsonObject skinJson) {
        super(id, xPos, yPos, width, height, "");
        this.skinJson = skinJson;
    }
    
    public JsonObject getSkinJson() {
        return skinJson;
    }
    
    public GuiSkinIcon setPosition(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        return this;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) {
            return;
        }
        this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int k = this.getHoverState(this.field_146123_n);
        int hoverColour = 0xC0101010;
        if (k == 1) {
            hoverColour = 0xC0444410;
        }
        drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, hoverColour, 0xD0101010);
    }
}
