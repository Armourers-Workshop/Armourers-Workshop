package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.Gui;

@SideOnly(Side.CLIENT)
public class GuiTab extends Gui {
    
    private final int TAB_TEXTURE_WIDTH = 26;
    private final int TAB_TEXTURE_HEIGHT = 26;
    
    private final int ICON_TEXTURE_WIDTH = 16;
    private final int ICON_TEXTURE_HEIGHT = 16;
    
    private final String name;
    
    public boolean enabled;
    public boolean visible;
    
    private int iconTextureX = 0;
    private int iconTextureY = 0;
    
    public GuiTab(String name) {
        this.name = name;
        this.enabled = true;
    }
    
    public String getName() {
        return name;
    }
    
    public void render(int x, int y, int mouseX, int mouseY, boolean activeTab) {
        int textureOffsetX = 0;
        int textureOffsetY = TAB_TEXTURE_HEIGHT;
        if (isMouseOver(x, y, mouseX, mouseY)) {
            textureOffsetX += TAB_TEXTURE_WIDTH;
        }
        if (!enabled) {
            textureOffsetX = TAB_TEXTURE_WIDTH * 2;
        }
        if (activeTab) {
            textureOffsetY = 0;
        }
        drawTexturedModalRect(x, y, textureOffsetX, textureOffsetY, TAB_TEXTURE_WIDTH, TAB_TEXTURE_HEIGHT);
        renderIcon(x, y);
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
    
    public boolean isMouseOver(int x, int y, int mouseX, int mouseY) {
        if (mouseX >= x & mouseX < x + TAB_TEXTURE_WIDTH) {
            if (mouseY >= y & mouseY < y + TAB_TEXTURE_HEIGHT) {
                return true;
            }
        }
        return false;
    }
    
    private void renderIcon(int x, int y) {
        drawTexturedModalRect(
                x + (int)((float)TAB_TEXTURE_WIDTH / 2F - (float)ICON_TEXTURE_WIDTH / 2F),
                y + (int)((float)TAB_TEXTURE_HEIGHT / 2F - (float)ICON_TEXTURE_HEIGHT / 2F),
                iconTextureX, iconTextureY,
                ICON_TEXTURE_WIDTH, ICON_TEXTURE_HEIGHT);
    }
}
