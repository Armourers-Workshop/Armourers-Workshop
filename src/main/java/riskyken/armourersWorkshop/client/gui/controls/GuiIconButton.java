package riskyken.armourersWorkshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiIconButton extends GuiButtonExt {

    private final String hoverText;
    private final ResourceLocation iconTexture;
    private int iconPosX;
    private int iconPosY;
    private int iconWidth;
    private int iconHeight;
    private boolean isPressed;
    
    public GuiIconButton(int id, int xPos, int yPos, int width, int height, String hoverText, ResourceLocation iconTexture) {
        super(id, xPos, yPos, width, height, "");
        this.hoverText = hoverText;
        this.iconTexture = iconTexture;
    }
    
    public void setIconLocation(int x, int y, int width, int height) {
        this.iconPosX = x;
        this.iconPosY = y;
        this.iconWidth = width;
        this.iconHeight = height;
    }
    
    public void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }
    
    public boolean isPressed() {
        return isPressed;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);
        if (!this.visible) {
            return;
        }
        this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int hoverState = this.getHoverState(this.field_146123_n);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        int xPos = iconPosX;
        if (hoverState == 0) {
            xPos += (iconWidth + 1) * 4;
        }
        if (hoverState == 2) {
            xPos += iconWidth + 1;
        }
        if (isPressed) {
            GL11.glColor4f(1F, 1F, 0.6F, 1F);
            //xPos += (iconWidth + 1) * 2;
        }
        mc.renderEngine.bindTexture(iconTexture);
        drawTexturedModalRect(xPosition, yPosition, xPos, iconPosY, iconWidth, iconHeight);
    }
}
