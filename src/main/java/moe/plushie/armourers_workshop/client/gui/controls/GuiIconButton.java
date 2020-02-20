package moe.plushie.armourers_workshop.client.gui.controls;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIconButton extends GuiButtonExt {

    private final GuiScreen parent;
    private String hoverText;
    private final ResourceLocation iconTexture;
    private String disableText;
    private int iconPosX;
    private int iconPosY;
    private int iconWidth;
    private int iconHeight;
    private boolean isPressed;
    private boolean horizontal = true;
    private boolean drawButtonBackground = true;
    private boolean playSound = true;

    public GuiIconButton(GuiScreen parent, int id, int xPos, int yPos, int width, int height, String hoverText, ResourceLocation iconTexture) {
        super(id, xPos, yPos, width, height, "");
        this.parent = parent;
        this.iconTexture = iconTexture;
        this.disableText = "";
        this.hoverText = hoverText;
    }
    
    public GuiIconButton(GuiScreen parent, int id, int xPos, int yPos, int width, int height, ResourceLocation iconTexture) {
        super(id, xPos, yPos, width, height, "");
        this.parent = parent;
        this.iconTexture = iconTexture;
        this.disableText = "";
    }

    public GuiIconButton setIconLocation(int x, int y, int width, int height) {
        this.iconPosX = x;
        this.iconPosY = y;
        this.iconWidth = width;
        this.iconHeight = height;
        return this;
    }

    public GuiIconButton setHoverText(String hoverText) {
        this.hoverText = hoverText;
        return this;
    }

    public GuiIconButton setHorizontal(boolean value) {
        this.horizontal = value;
        return this;
    }

    public GuiIconButton setPlayPressSound(boolean value) {
        this.playSound = value;
        return this;
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

    public GuiIconButton setDrawButtonBackground(boolean drawButtonBackground) {
        this.drawButtonBackground = drawButtonBackground;
        return this;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (drawButtonBackground) {
            super.drawButton(mc, mouseX, mouseY, partial);
        }
        if (!this.visible) {
            return;
        }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int hoverState = this.getHoverState(this.hovered);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        int offsetPos = 0;
        // disabled
        if (hoverState == 0) {
            offsetPos += (iconWidth) * 2;
        }
        // hovering
        if (hoverState == 2) {
            offsetPos += iconWidth;
        }
        if (isPressed) {
            offsetPos += (iconWidth) * 2;
        }
        mc.renderEngine.bindTexture(iconTexture);
        if (horizontal) {
            drawTexturedModalRect(x + width / 2 - iconWidth / 2, y + height / 2 - iconHeight / 2, iconPosX + offsetPos, iconPosY, iconWidth, iconHeight);
        } else {
            drawTexturedModalRect(x + width / 2 - iconWidth / 2, y + height / 2 - iconHeight / 2, iconPosX, iconPosY + offsetPos, iconWidth, iconHeight);
        }
    }

    public void drawRollover(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) {
            return;
        }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int hoverState = this.getHoverState(this.hovered);
        if (hoverState == 0 & this.hovered) {
            if (!StringUtils.isNullOrEmpty(disableText)) {
                ArrayList<String> textList = new ArrayList<String>();
                textList.add(disableText);
                GuiHelper.drawHoveringText(textList, mouseX, mouseY, mc.fontRenderer, parent.width, parent.height, zLevel);
            }
        }
        if (hoverState == 2) {
            if (!StringUtils.isNullOrEmpty(hoverText)) {
                ArrayList<String> textList = new ArrayList<String>();
                textList.add(hoverText);
                GuiHelper.drawHoveringText(textList, mouseX, mouseY, mc.fontRenderer, parent.width, parent.height, zLevel);
            }
        }
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
        if (playSound) {
            super.playPressSound(soundHandlerIn);
        }
    }
}
