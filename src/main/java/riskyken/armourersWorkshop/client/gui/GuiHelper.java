package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHelper {
    public static void renderLocalizedGuiName(FontRenderer fontRenderer, int xSize, String name) {
        String unlocalizedName = "container.inventory." + name + ".name";
        String localizedName = StatCollector.translateToLocal(unlocalizedName);
        String renderText = unlocalizedName;
        if (!unlocalizedName.equals(localizedName)){
            renderText = localizedName;
        }
        int xPos = xSize / 2 - fontRenderer.getStringWidth(renderText) / 2;
        fontRenderer.drawString(renderText, xPos, 6, 4210752);
    }
}
