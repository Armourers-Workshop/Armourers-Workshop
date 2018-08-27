package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.Gui;

@SideOnly(Side.CLIENT)
public class GuiTab extends Gui {
    
    private final String name;
    
    public boolean enabled;
    public boolean visible;
    
    private int iconTextureX = 0;
    private int iconTextureY = 0;
    
    private int iconTextureWidth = 16;
    private int iconTextureHeight = 16;
    
    private int tabTextureWidth = 26;
    private int tabTextureHeight = 26;
    
    private int padLeft, padRight, padTop, padBottom = 0;
    
    private int animationFrames = 0;
    private int animationSpeed = 0;
    
    public GuiTab(String name) {
        this.name = name;
        this.enabled = true;
        this.visible = true;
    }
    
    public String getName() {
        return name;
    }
    
    public void render(int x, int y, int mouseX, int mouseY, boolean activeTab) {
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
        drawTexturedModalRect(x, y, textureOffsetX, textureOffsetY, tabTextureWidth, tabTextureHeight);
        renderIcon(x, y, mouseX, mouseY);
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
    
    private void renderIcon(int x, int y, int mouseX, int mouseY) {
        int animationOffset = 0;
        if (isMouseOver(x, y, mouseX, mouseY) & animationFrames > 0) {
            int frame = (int) ((System.currentTimeMillis() / (long)animationSpeed) % animationFrames);
            animationOffset += iconTextureHeight * frame;
        }
        drawTexturedModalRect(
                x + (int)((float)tabTextureWidth / 2F - (float)iconTextureWidth / 2F),
                y + (int)((float)tabTextureHeight / 2F - (float)iconTextureHeight / 2F),
                iconTextureX, iconTextureY + animationOffset,
                iconTextureWidth, iconTextureHeight);
    }
}
