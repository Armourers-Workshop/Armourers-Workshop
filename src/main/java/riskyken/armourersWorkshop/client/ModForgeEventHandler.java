package riskyken.armourersWorkshop.client;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModForgeEventHandler {
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        if (event.left != null && event.left.size() > 0) {
            event.left.add("");
            String prefix = EnumChatFormatting.GOLD + "[" + LibModInfo.NAME + "] " + EnumChatFormatting.WHITE;
            
            event.left.add(prefix + "Model cache size: " + ArmourersWorkshop.proxy.getPlayerModelCacheSize());
        }
    }
}
