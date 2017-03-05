package riskyken.armourersWorkshop.client.gui.controls;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;

@SideOnly(Side.CLIENT)
public interface IGuiListItem {
    
    public void drawListItem(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, boolean selected, int width);
    
    public boolean mousePressed(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, int button, int width);
    
    public void mouseReleased(FontRenderer fontRenderer, int x, int y, int mouseX, int mouseY, int button, int width);

    public String getDisplayName();
}
