package moe.plushie.armourers_workshop.client.gui.controls;

import java.awt.Color;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHSBSlider extends GuiSlider {

    private static final ResourceLocation sliderTexture = new ResourceLocation(LibGuiResources.CONTROL_SLIDER_HUE);
    
    private HSBSliderType type;
    private IHSBSliderCallback callback = null;
    private float hueValue;
    private float briValue;
    
    public GuiHSBSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type, float curValue, float hueValue, float brightnessValue) {
        super(id, xPos, yPos, width, height, "", "", 0, 256 * 6 - 5, 0, false, false);
        this.type = type;
        this.hueValue = hueValue;
        this.briValue = brightnessValue;
        setValue(curValue);
        this.callback = callback;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            mouseCheck();
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            mc.renderEngine.bindTexture(sliderTexture);
            
            if (type == HSBSliderType.SATURATION) {
                Color hueColour = Color.getHSBColor(hueValue, 1F, 1F);
                float red = (float) hueColour.getRed() / 255;
                float green = (float) hueColour.getGreen() / 255;
                float blue = (float) hueColour.getBlue() / 255;
                GL11.glColor4f(red, green, blue, 1.0F);
                drawTexturedModalRectScaled(x + 1, y + 1, 0, 176, 256, 20, this.width - 2, this.height - 2);
            }
            
            int srcY = 236;
            
            if (type == HSBSliderType.BRIGHTNESS) {
                srcY -= 20;
            }
            if (type == HSBSliderType.SATURATION) {
                srcY -= 40;
            }
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (type == HSBSliderType.SATURATION) {
                Color hueColour = Color.getHSBColor(0F, 0F, briValue);
                float red = (float) hueColour.getRed() / 255;
                float green = (float) hueColour.getGreen() / 255;
                float blue = (float) hueColour.getBlue() / 255;
                GL11.glColor4f(red, green, blue, 1.0F);
                drawTexturedModalRectScaled(x + 1, y + 1, 0, srcY, 231, 20, this.width - 2, this.height - 2);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                drawTexturedModalRectScaled(x + 1, y + 1, 0, srcY, 256, 20, this.width - 2, this.height - 2);
            }
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
    
    private void mouseCheck() {
        //Fix for TMI, it stops the mouse released event in container GUI's.
        if (Loader.isModLoaded("TooManyItems")) {
            if (Mouse.isCreated() && this.dragging && !Mouse.isButtonDown(0)) {
                this.dragging = false;
            }
        }
    }
    
    @Override
    protected void mouseDragged(Minecraft mc, int par2, int par3) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (par2 - (this.x + 4)) / (float)(this.width - 8);
                updateSlider();
                if (callback != null) {
                    callback.valueUpdated(this, this.sliderValue);
                }
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            ScaledResolution screenRes = new ScaledResolution(mc);
            double scaleWidth = mc.displayWidth / screenRes.getScaledWidth_double();
            double scaleHeight = mc.displayHeight / screenRes.getScaledHeight_double();
            
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor((int) ((this.x + 1) * scaleWidth),  (mc.displayHeight) - (int)((this.y + height - 1) * scaleHeight), (int) ((width - 2) * scaleWidth), (int) ((height - 2) * scaleHeight));
            
            this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (this.width - 3) - 2), this.y, 0, 0, 7, 4);
            this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (this.width - 3) - 2), this.y + this.height - 4, 7, 0, 7, 4);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }
    
    public HSBSliderType getType() {
        return type;
    }
    
    @Override
    public double getValue() {
        return sliderValue;
    }
    
    @Override
    public void setValue(double value) {
        this.sliderValue = value;
        updateSlider();
        if (callback != null) {
            callback.valueUpdated(this, this.sliderValue);
        }
    }
    
    public void setHue(float value) {
        this.hueValue = value;
    }
    
    public void setBrightness(float value) {
        this.briValue = value;
    }
    
    public void drawTexturedModalRectScaled (int x, int y, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight) {
        //Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, tarWidth, tarHeight, srcWidth, srcHeight);
        Gui.drawScaledCustomSizeModalRect(x, y, u, v, srcWidth, srcHeight, tarWidth, tarHeight, 256, 256);
    }
    
    public enum HSBSliderType {
        HUE, SATURATION, BRIGHTNESS;
    }
    
    public interface IHSBSliderCallback {
        public void valueUpdated(GuiHSBSlider source, double sliderValue);
    }
}
