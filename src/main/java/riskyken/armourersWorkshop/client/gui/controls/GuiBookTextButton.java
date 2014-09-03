package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraft.client.Minecraft;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.client.config.GuiButtonExt;

public class GuiBookTextButton extends GuiButtonExt {

    public GuiBookTextButton(int id, int xPos, int yPos, int width, String displayString) {
        super(id, xPos, yPos, width, 8, displayString);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.field_146123_n);
            int color = UtilColour.getMinecraftColor(8);
            
            if (hoverState == 2) {
                color = UtilColour.getMinecraftColor(7);
            }
            
            String buttonText = this.displayString;
            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
            
            mc.fontRenderer.drawString(buttonText, this.xPosition, this.yPosition, color);
        }
    }
    
}
