package riskyken.armourersWorkshop.client.handler;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DebugTextHandler {
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        if (event.left != null && event.left.size() > 0) {
            event.left.add("");
            String prefix = EnumChatFormatting.GOLD + "[Armourers] " + EnumChatFormatting.WHITE;
            
            event.left.add(prefix + "Model cache: " + ArmourersWorkshop.proxy.getPlayerModelCacheSize());
        }
    }
}
