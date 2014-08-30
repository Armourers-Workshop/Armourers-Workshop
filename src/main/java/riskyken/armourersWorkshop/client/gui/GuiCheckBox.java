package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.lib.LibModInfo;

public class GuiCheckBox extends GuiButton {
	
	private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/check-box.png");
	
	private boolean checked;
	
	public GuiCheckBox(int id, int x, int y, String text, boolean checked) {
		super(id, x, y, 18, 18, text);
		this.checked = checked;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int x, int y) {
		minecraft.getTextureManager().bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawCheckBox(minecraft, x, y);
		drawLabel(minecraft, this.xPosition + 22, this.yPosition + 6);
	}
	
	private void drawCheckBox(Minecraft minecraft, int x, int y) {
		int sourceX = 0;
		int sourceY = 0;
		
		if (checked) { sourceX += 18; }
		if(isHovering(x, y, this.xPosition, this.yPosition, width, height)) { sourceY += 18; }
		
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
