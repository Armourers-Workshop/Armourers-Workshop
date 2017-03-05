package riskyken.armourersWorkshop.client.guidebook;

import net.minecraft.client.gui.FontRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBookPage {

    public void renderPage(FontRenderer fontRenderer, int mouseX, int mouseY, boolean turning, int pageNumber);
    
    public void renderRollover(FontRenderer fontRenderer, int mouseX, int mouseY);
}
