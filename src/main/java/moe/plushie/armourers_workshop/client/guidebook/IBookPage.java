package moe.plushie.armourers_workshop.client.guidebook;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBookPage {

    public void renderPage(FontRenderer fontRenderer, int mouseX, int mouseY, boolean turning, int pageNumber);
    
    public void renderRollover(FontRenderer fontRenderer, int mouseX, int mouseY);
}
