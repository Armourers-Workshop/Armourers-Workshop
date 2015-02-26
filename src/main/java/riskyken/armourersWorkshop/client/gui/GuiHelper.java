package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHelper {
    
    public static void renderLocalizedGuiName(FontRenderer fontRenderer, int xSize, String name) {
        renderLocalizedGuiName(fontRenderer, xSize, name, null);
    }
    
    public static void renderLocalizedGuiName(FontRenderer fontRenderer, int xSize, String name, String append) {
        String unlocalizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + name + ".name";
        String localizedName = StatCollector.translateToLocal(unlocalizedName);
        String renderText = unlocalizedName;
        if (!unlocalizedName.equals(localizedName)){
            renderText = localizedName;
        }
        if (append != null) {
            renderText = renderText + " - " + append;
        }
        int xPos = xSize / 2 - fontRenderer.getStringWidth(renderText) / 2;
        fontRenderer.drawString(renderText, xPos, 6, 4210752);
    }
    
    public static String getLocalizedControlName(String guiName, String controlName) {
        String unlocalizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + "." + controlName;
        String localizedName = StatCollector.translateToLocal(unlocalizedName);
        if (!unlocalizedName.equals(localizedName)){
            return localizedName;
        }
        return unlocalizedName;
    }
}
