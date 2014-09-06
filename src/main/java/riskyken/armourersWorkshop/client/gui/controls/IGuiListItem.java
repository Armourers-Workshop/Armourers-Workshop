package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraft.client.gui.FontRenderer;


public interface IGuiListItem {
    
    public void drawListItem(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, boolean selected);
    
    public boolean mousePressed(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, int button);
    
    public void mouseReleased(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, int button);

    public String getDisplayName();
}
