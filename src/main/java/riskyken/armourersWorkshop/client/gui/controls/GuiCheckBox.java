package riskyken.armourersWorkshop.client.gui.controls;

import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class GuiCheckBox extends cpw.mods.fml.client.config.GuiCheckBox {

    private int boxWidth;
    private int textColour;
    
    public GuiCheckBox(int id, int x, int y, String text, boolean checked) {
        super(id, x, y, text, checked);
        this.boxWidth = 9;
        this.height = 9;
        this.textColour = 14737632;
    }
    
    public GuiCheckBox setTextColour(int textColour) {
        this.textColour = textColour;
        return this;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.boxWidth && mouseY < this.yPosition + this.height;
            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46, this.boxWidth, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            this.mouseDragged(mc, mouseX, mouseY);
            int color = this.textColour;
            
            if (packedFGColour != 0) {
                color = packedFGColour;
            } else if (!this.enabled) {
                color = 10526880;
            }
            
            if (this.isChecked()) {
                this.drawCenteredString(mc.fontRenderer, "x", this.xPosition + this.boxWidth / 2 + 1, this.yPosition, 0xFFCCCCCC);
            }  
            mc.fontRenderer.drawString(displayString, xPosition + this.boxWidth + 2, yPosition + 1, color, false);
        }
    }
}
