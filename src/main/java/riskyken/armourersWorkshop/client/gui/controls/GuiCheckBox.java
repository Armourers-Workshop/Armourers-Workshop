package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCheckBox extends GuiButton {
	
	private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/controls/check-box.png");
	
	private boolean checked;
	private boolean small;
	
	public GuiCheckBox(int id, int x, int y, int width, int height, String text, boolean checked, boolean small) {
		super(id, x, y, width, height, text);
		this.checked = checked;
		this.small = small;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int x, int y) {
	    if (this.visible) {
	        minecraft.getTextureManager().bindTexture(texture);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        drawCheckBox(minecraft, x, y);
	        drawLabel(minecraft, this.xPosition + 18, this.yPosition + 4); 
	    }
	}
	
	private void drawCheckBox(Minecraft minecraft, int x, int y) {
		int sourceX = 0;
		int sourceY = 0;
		
		if (small) {
			sourceY += 28;
		}
		
		if (checked) {
			sourceX += this.width;
		}
		if(isHovering(x, y, this.xPosition, this.yPosition, width, height)) {
			sourceY += this.height;
		}
		
		this.drawTexturedModalRect(this.xPosition, this.yPosition, sourceX, sourceY, width, height);
	}
	
	private void drawLabel(Minecraft minecraft, int x, int y) {
		FontRenderer fontRendererObj = minecraft.fontRenderer;
		fontRendererObj.drawString(this.displayString, x, y, 4210752);
	}
	
	private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
