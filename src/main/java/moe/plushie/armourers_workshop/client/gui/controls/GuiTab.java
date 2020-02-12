package moe.plushie.armourers_workshop.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTab extends Gui {

    private final GuiTabController parent;
    private final String name;

    public boolean enabled;
    public boolean visible;

    private int iconTextureX = 0;
    private int iconTextureY = 0;

    private int iconTextureWidth = 16;
    private int iconTextureHeight = 16;

    private int tabTextureWidth = 26;
    private int tabTextureHeight = 30;

    private int padLeft, padRight, padTop, padBottom = 0;

    private int animationFrames = 0;
    private int animationSpeed = 0;

    public GuiTab(GuiTabController parent, String name) {
        this.parent = parent;
        this.name = name;
        this.enabled = true;
        this.visible = true;
    }

    public String getName() {
        return name;
    }

    public void render(int index, int x, int y, int mouseX, int mouseY, boolean activeTab, ResourceLocation tabIcons, boolean left) {
        int textureOffsetX = 0;
        int textureOffsetY = tabTextureHeight;

        if (isMouseOver(x, y, mouseX, mouseY)) {
            textureOffsetX += tabTextureWidth;
        }
        if (!enabled) {
            textureOffsetX = tabTextureWidth * 2;
        }
        
        if (activeTab) {
            textureOffsetY = 0;
        }
        if (parent.isFullscreen()) {
            textureOffsetY += tabTextureHeight * 2;
        }
        if (!left & !parent.isFullscreen()) {
            textureOffsetX += tabTextureWidth * 2;
        }
        drawTexturedModalRect(x, y, textureOffsetX, textureOffsetY, tabTextureWidth, tabTextureHeight);

        if (parent.isEditMode() & left) {
            // Up
            if (index != 0) {
                drawTexturedModalRect(x - 2, y + 3, 0, 248, 8, 8);
            }
            // Delete
            drawTexturedModalRect(x - 2, y + 11, 16, 240, 8, 8);
            // Down
            if (index < parent.getTabCount() - 2) {
                drawTexturedModalRect(x - 2, y + 19, 0, 240, 8, 8);
            }
        }
        if (parent.isEditMode() & !left) {
            // Up
            if (index != 0) {
                drawTexturedModalRect(x + 19, y + 3, 0, 248, 8, 8);
            }
            // Delete
            drawTexturedModalRect(x + 19, y + 11, 16, 240, 8, 8);
            // Down
            if (index < parent.getTabCount() - 2) {
                drawTexturedModalRect(x + 19, y + 19, 0, 240, 8, 8);
            }
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(tabIcons);

        if (left) {
            renderIcon(x - 1, y, mouseX, mouseY);
        } else {
            renderIcon(x, y, mouseX, mouseY);
        }
    }

    public GuiTab setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public GuiTab setVisable(boolean visible) {
        this.visible = visible;
        return this;
    }

    public GuiTab setIconLocation(int x, int y) {
        this.iconTextureX = x;
        this.iconTextureY = y;
        return this;
    }

    public GuiTab setIconSize(int width, int height) {
        this.iconTextureWidth = width;
        this.iconTextureHeight = height;
        return this;
    }

    public GuiTab setTabTextureSize(int width, int height) {
        this.tabTextureWidth = width;
        this.tabTextureHeight = height;
        return this;
    }

    public GuiTab setPadding(int left, int right, int top, int bottom) {
        this.padLeft = left;
        this.padRight = right;
        this.padTop = top;
        this.padBottom = bottom;
        return this;
    }

    public GuiTab setAnimation(int frames, int speed) {
        this.animationFrames = frames;
        this.animationSpeed = speed;
        return this;
    }

    public boolean isMouseOver(int x, int y, int mouseX, int mouseY) {
        if (mouseX >= x + padLeft & mouseX < x + tabTextureWidth - padRight) {
            if (mouseY >= y + padTop & mouseY < y + tabTextureHeight - padBottom) {
                return true;
            }
        }
        return false;
    }

    public boolean mousePress(int x, int y, int mouseX, int mouseY) {
        if (mouseX >= x + padLeft & mouseX < x + tabTextureWidth - padRight) {
            if (mouseY >= y + padTop & mouseY < y + tabTextureHeight - padBottom) {
                if (enabled) {
                    if (!parent.isEditMode()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void renderIcon(int x, int y, int mouseX, int mouseY) {
        int animationOffset = 0;
        if (isMouseOver(x, y, mouseX, mouseY) & animationFrames > 0) {
            int frame = (int) ((System.currentTimeMillis() / animationSpeed) % animationFrames);
            animationOffset += iconTextureHeight * frame;
        }
        drawTexturedModalRect(x + (int) (tabTextureWidth / 2F - iconTextureWidth / 2F), y + (int) (tabTextureHeight / 2F - iconTextureHeight / 2F), iconTextureX, iconTextureY + animationOffset, iconTextureWidth, iconTextureHeight);
    }
}
