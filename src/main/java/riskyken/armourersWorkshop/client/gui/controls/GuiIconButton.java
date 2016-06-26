package riskyken.armourersWorkshop.client.gui.controls;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.gui.GuiHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIconButton extends GuiButtonExt {

    private final GuiScreen parent;
    private final String hoverText;
    private final ResourceLocation iconTexture;
    private String disableText;
    private int iconPosX;
    private int iconPosY;
    private int iconWidth;
    private int iconHeight;
    private boolean isPressed;
    
    public GuiIconButton(GuiScreen parent, int id, int xPos, int yPos, int width, int height, String hoverText, ResourceLocation iconTexture) {
        super(id, xPos, yPos, width, height, "");
        this.parent = parent;
        this.hoverText = hoverText;
        this.iconTexture = iconTexture;
        this.disableText = "";
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
    
    public void setDisableText(String disableText) {
        this.disableText = disableText;
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
    
    public void drawRollover(Minecraft mc, int mouseX, int mouseY) {
        int hoverState = this.getHoverState(this.field_146123_n);
        if (hoverState == 0 & this.field_146123_n) {
            ArrayList<String> textList = new ArrayList<String>();
            textList.add(disableText);
            GuiHelper.drawHoveringText(textList, mouseX, mouseY, mc.fontRenderer, parent.width, parent.height, zLevel);
        }
        if (hoverState == 2) {
            ArrayList<String> textList = new ArrayList<String>();
            textList.add(hoverText);
            GuiHelper.drawHoveringText(textList, mouseX, mouseY, mc.fontRenderer, parent.width, parent.height, zLevel);
        }
    }
}
