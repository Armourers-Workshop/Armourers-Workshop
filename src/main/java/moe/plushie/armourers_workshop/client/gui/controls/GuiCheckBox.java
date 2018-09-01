package moe.plushie.armourers_workshop.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCheckBox extends net.minecraftforge.fml.client.config.GuiCheckBox {

    private int boxWidth;
    private int textColour;
    
    public GuiCheckBox(int id, int x, int y, String text, boolean checked) {
        super(id, x, y, text, checked);
        this.boxWidth = 9;
        this.height = 9;
        this.textColour = 4210752;
    }
    
    public GuiCheckBox setTextColour(int textColour) {
        this.textColour = textColour;
        return this;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.boxWidth && mouseY < this.y + this.height;
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46, this.boxWidth, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            this.mouseDragged(mc, mouseX, mouseY);
            int color = this.textColour;
            
            if (packedFGColour != 0) {
                color = packedFGColour;
            } else if (!this.enabled) {
                color = 10526880;
            }
            
            if (this.isChecked()) {
                this.drawCenteredString(mc.fontRenderer, "x", this.x + this.boxWidth / 2 + 1, this.y, 0xFFCCCCCC);
            }  
            mc.fontRenderer.drawString(displayString, x + this.boxWidth + 2, y + 1, color, false);
        }
    }
}
