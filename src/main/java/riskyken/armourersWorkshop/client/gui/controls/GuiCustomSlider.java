package riskyken.armourersWorkshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class GuiCustomSlider extends GuiSlider {

    private boolean fineTuneButtons;
    private double lastValue;
    
    public GuiCustomSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, ISlider par) {
        super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, par);
        this.lastValue = sliderValue;
    }
    
    public GuiCustomSlider setFineTuneButtons(boolean fineTuneButtons) {
        this.fineTuneButtons = fineTuneButtons;
        return this;
    }
    
    @Override
    protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                updateSliderLocation(mouseX, mouseY);
                if (lastValue != sliderValue) {
                    updateSlider();
                    lastValue = sliderValue;
                }
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (fineTuneButtons) {
                boolean overLeft = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + 9 && mouseY < this.yPosition + this.height;
                int k = 1;
                if (overLeft) {
                    k = 2;
                }
                GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46 + k * 20, 9, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
                k = 1;
                boolean overRight = mouseX >= this.xPosition + width - 9 && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                if (overRight) {
                    k = 2;
                }
                GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition + width - 9, this.yPosition, 0, 46 + k * 20, 9, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
                //minecraft.fontRenderer.drawString("<", xPosition + 2, yPosition + 1, 0xDDDDDD, false);
                //minecraft.fontRenderer.drawString(">", xPosition + width - 8, yPosition + 1, 0xDDDDDD, false);
            }
            int k = 1;
            if (field_146123_n) {
                k = 2;
            }
            if (fineTuneButtons) {
                GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition + 10 + (int)(this.sliderValue * (float)(this.width - 8 - 20)), this.yPosition, 0, 46 + k * 20, 8, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            } else {
                GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 46 + k * 20, 8, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            }
        }
    }
    
    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        if (fineTuneButtons) {
            if (isPosInRec(xPosition, yPosition, 9, height, mouseX, mouseY)) {
                //Left button
                setValue(getValue() - 1);
                updateSlider();
                return true;
            }
            if (isPosInRec(xPosition + width - 9, yPosition, 9, height, mouseX, mouseY)) {
                //Right button
                setValue(getValue() + 1);
                updateSlider();
                return true;
            }
            if (isPosInRec(xPosition + 9, yPosition, width - 18, height, mouseX, mouseY)) {
                //Centre button.
                updateSliderLocation(mouseX, mouseY);
                updateSlider();
                this.dragging = true;
                return true;
            }
        } else {
            if (isPosInRec(xPosition, yPosition, width, height, mouseX, mouseY)) {
                updateSliderLocation(mouseX, mouseY);
                updateSlider();
                this.dragging = true;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (!visible) {
            return;
        }
        this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int k = this.getHoverState(this.field_146123_n);
        if (fineTuneButtons) {
            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition + 10, this.yPosition, 0, 46 + k * 18, this.width - 20, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
        } else {
            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
        }
        this.mouseDragged(minecraft, mouseX, mouseY);
        int color = 14737632;
        
        if (packedFGColour != 0)
        {
            color = packedFGColour;
        }
        else if (!this.enabled)
        {
            color = 10526880;
        }
        else if (this.field_146123_n)
        {
            color = 16777120;
        }
        
        String buttonText = this.displayString;
        int strWidth = minecraft.fontRenderer.getStringWidth(buttonText);
        int ellipsisWidth = minecraft.fontRenderer.getStringWidth("...");
        
        if (strWidth > width - 6 && strWidth > ellipsisWidth)
            buttonText = minecraft.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";
        
        this.drawCenteredString(minecraft.fontRenderer, buttonText, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, color);
    }
    
    private boolean isPosInRec(int recX, int recY, int recW, int recH, int posX, int posY) {
        return posX >= recX && posY >= recY && posX < recX + recW && posY < recY + recH;
    }
    
    private void updateSliderLocation(int mouseX, int mouseY) {
        if (fineTuneButtons) {
            this.sliderValue = (mouseX - (this.xPosition + 8 + 4)) / (float)(this.width - 8 - 18);
        } else {
            this.sliderValue = (mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
        }
    }
}
