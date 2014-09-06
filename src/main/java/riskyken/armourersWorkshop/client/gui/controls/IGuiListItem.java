package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraft.client.gui.FontRenderer;


public interface IGuiListItem {
    
    public void drawListItem(FontRenderer fontRenderer, int x, int y, int relativeX, int relativeY);
    
    public boolean mousePressed(int x, int y, int button);
    
    public void mouseReleased(int x, int y, int button);
}
