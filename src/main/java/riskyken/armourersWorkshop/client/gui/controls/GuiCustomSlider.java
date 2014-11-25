package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiSlider;

public class GuiCustomSlider extends GuiSlider {

    public GuiCustomSlider(int id, int xPos, int yPos, int width, int height,
            String prefix, String suf, double minVal, double maxVal,
            double currentVal, boolean showDec, boolean drawStr, ISlider par) {
        super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal,
                showDec, drawStr, par);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        // TODO Auto-generated method stub
        super.drawButton(mc, mouseX, mouseY);
    }
    
    @Override
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (par2 - (this.xPosition + 4)) / (float)(this.width - 8);
                updateSlider();
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 5);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition + 5, 0, 81, 4, 5);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 5);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition + 5, 196, 81, 4, 5);
        }
    }
}
