package moe.plushie.armourers_workshop.client.gui.controls;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScrollbar  extends GuiButton {

	private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.CONTROL_SCROLLBAR);
	
    /** The value of this slider control. */
	private int sliderValue = 0;
    
    /** The max value of this slider control. */
    private int sliderMaxValue;
    
    private int amount = 2;

    /** Is this slider control being dragged. */
    private boolean dragging;
    
    private boolean horizontal;
    
    private int xOffset = 0;
    
    private int yOffset = 0;
    
    private boolean styleFlat = false;
	
	public GuiScrollbar(int id, int x, int y, int width, int height, String text, boolean horizontal) {
		super(id, x, y, width, height, text);
		this.horizontal = horizontal;
		if (horizontal) {
			xOffset = 1;
			sliderMaxValue = width - 30;
		} else {
			yOffset = 1;
			sliderMaxValue = height - 30;
		}
	}
	
	public void setStyleFlat(boolean styleFlat) {
        this.styleFlat = styleFlat;
    }
	
	public boolean isStyleFlat() {
        return styleFlat;
    }
	
	@Override
	public int getHoverState(boolean par1) {
		return 0;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int x, int y, float partial) {
        if (this.visible)
        {
            updateMouse();
            FontRenderer fontRendererObj = minecraft.fontRenderer;
            minecraft.getTextureManager().bindTexture(texture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            //this.field_82253_i = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;

            drawArrows(minecraft, x, y);
            
            //this.drawTexturedModalRect(this.xPosition + 10, this.yPosition, 0, 10, 60, 10);
            //this.drawTexturedModalRect(this.xPosition + this.width - 10, this.yPosition, 10, 0, 10, 10);
            
            //this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
            this.mouseDragged(minecraft, x, y);

            //this.drawCenteredString(fontRendererObj, Float.toString(sliderValue), this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 14737632);
        }
	}
	
	private void drawArrows(Minecraft minecraft, int x, int y) {
		int sourceX = 0;
		int sourceY = 0;
		
		sourceX = yOffset * 20;
		
		
		//arrows
		this.drawHover(this.x, this.y, sourceX, sourceY, 10, 10, x, y);
		this.drawHover(this.x, this.y + this.height - 10, sourceX + 10, sourceY, 10, 10, x, y);
		
		
		//gutter sides
		this.drawTexturedModalRect(this.x, this.y + 10, sourceX, 20, 10, 10);
		this.drawTexturedModalRect(this.x, this.y + this.height - 20, sourceX + 10, 20, 10, 10);
		
		//gutter fill
		this.drawTexturedModalRect(this.x, this.y + 20, 246 * yOffset, 246 * xOffset, width, height - 40);
		
		//grip
		float gripPos = (height - 30) / 100F * getPercentageValue();
		this.drawHover(this.x, (int) (this.y + gripPos + 10), 40, sourceY, 10, 10, x, y);
	}
	
	private void drawHover(int x, int y, int sourceX, int sourceY, int width, int height, int mouseX, int mouseY) {
		int hover = 0;
		if(isHovering(mouseX, mouseY, x, y, width, height)) { hover = 10; }
		this.drawTexturedModalRect(x, y, sourceX, sourceY + hover, width, height);
	}
	
	private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	@Override
	public void mouseReleased(int par1, int par2) {
		this.dragging = false;
	}
	
    private void updateMouse() {
        if (Mouse.isCreated()) {
            int dWheel = Mouse.getDWheel();
            if (dWheel < 0) {
                setValue(sliderValue + amount);
            } else if (dWheel > 0) {
                setValue(sliderValue - amount);
            }
        }
    }
	
	public void setValue(int newValue) {
		sliderValue = newValue;
		if (sliderValue <= 0) { sliderValue = 0; }
		if (sliderValue > sliderMaxValue) { sliderValue = sliderMaxValue; }
	}
	
	public void setSliderMaxValue(int sliderMaxValue) {
        this.sliderMaxValue = sliderMaxValue;
        sliderValue = Math.min(sliderValue, sliderMaxValue);
    }
	
	public void setAmount(int amount) {
        this.amount = amount;
    }
	
	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int x, int y) {
		if (super.mousePressed(par1Minecraft, x, y)) {
			
			if (!isHovering(x, y, this.x, this.y, 10, 10)) {
				if (!isHovering(x, y, this.x + xOffset * (this.width - 10), this.y + yOffset * (this.height - 10), 10, 10)) {
					this.dragging = true;
				}
				else {
					setValue(sliderValue + amount);
				}
			} else {
				setValue(sliderValue - amount);
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected void mouseDragged(Minecraft par1Minecraft, int x, int y) {
		if (this.dragging) {
			if (horizontal) {
			    float per = (x - this.x - 12) / (width - 30F) * sliderMaxValue;
				setValue((int) per);
			} else {
			    float per = (y - this.y - 12) / (height - 30F) * sliderMaxValue;
				setValue((int) per);
			}
		}
	}

	public int getValue() {
		return sliderValue;
	}
	
	public int getPercentageValue() {
	    if (sliderValue == 0) {
	        return 0;
	    }
	    return (int) (((float)sliderValue / (float)sliderMaxValue) * 100);
	}

}
