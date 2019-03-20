package moe.plushie.armourers_workshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
                boolean overLeft = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 9 && mouseY < this.y + this.height;
                int k = 1;
                if (overLeft) {
                    k = 2;
                }
                GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46 + k * 20, 9, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
                k = 1;
                boolean overRight = mouseX >= this.x + width - 9 && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                if (overRight) {
                    k = 2;
                }
                GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x + width - 9, this.y, 0, 46 + k * 20, 9, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
                //minecraft.fontRenderer.drawString("<", xPosition + 2, yPosition + 1, 0xDDDDDD, false);
                //minecraft.fontRenderer.drawString(">", xPosition + width - 8, yPosition + 1, 0xDDDDDD, false);
            }
            int k = 1;
            if (hovered) {
                k = 2;
            }
            if (fineTuneButtons) {
                GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x + 10 + (int)(this.sliderValue * (float)(this.width - 8 - 20)), this.y, 0, 46 + k * 20, 8, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            } else {
                GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 46 + k * 20, 8, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            }
        }
    }
    
    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        if (!this.visible) {
            return false;
        }
        if (fineTuneButtons) {
            if (isPosInRec(x, y, 9, height, mouseX, mouseY)) {
                //Left button
                setValue(getValue() - 1);
                updateSlider();
                return true;
            }
            if (isPosInRec(x + width - 9, y, 9, height, mouseX, mouseY)) {
                //Right button
                setValue(getValue() + 1);
                updateSlider();
                return true;
            }
            if (isPosInRec(x + 9, y, width - 18, height, mouseX, mouseY)) {
                //Centre button.
                updateSliderLocation(mouseX, mouseY);
                updateSlider();
                this.dragging = true;
                return true;
            }
        } else {
            if (isPosInRec(x, y, width, height, mouseX, mouseY)) {
                updateSliderLocation(mouseX, mouseY);
                updateSlider();
                this.dragging = true;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void mouseReleased(int par1, int par2) {
        super.mouseReleased(par1, par2);
        if (parent != null) {
            parent.onChangeSliderValue(this);
        }
    }
    
    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partial) {
        if (!visible) {
            return;
        }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int k = this.getHoverState(this.hovered);
        if (fineTuneButtons) {
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x + 10, this.y, 0, 46 + k * 18, this.width - 20, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
        } else {
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
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
        else if (this.hovered)
        {
            color = 16777120;
        }
        
        String buttonText = this.displayString;
        int strWidth = minecraft.fontRenderer.getStringWidth(buttonText);
        int ellipsisWidth = minecraft.fontRenderer.getStringWidth("...");
        
        if (strWidth > width - 6 && strWidth > ellipsisWidth)
            buttonText = minecraft.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";
        
        this.drawCenteredString(minecraft.fontRenderer, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
    }
    
    private boolean isPosInRec(int recX, int recY, int recW, int recH, int posX, int posY) {
        return posX >= recX && posY >= recY && posX < recX + recW && posY < recY + recH;
    }
    
    private void updateSliderLocation(int mouseX, int mouseY) {
        if (fineTuneButtons) {
            this.sliderValue = (mouseX - (this.x + 8 + 4)) / (float)(this.width - 8 - 18);
        } else {
            this.sliderValue = (mouseX - (this.x + 4)) / (float)(this.width - 8);
        }
    }
}
