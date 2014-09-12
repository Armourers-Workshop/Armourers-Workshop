package riskyken.armourersWorkshop.client;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.render.ItemModelRenderManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModForgeEventHandler {
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        if (event.left != null && event.left.size() > 0) {
            event.left.add("");
            event.left.add("Item model cache size:  " + ItemModelRenderManager.getCacheSize());
            event.left.add("Player model cache size:  " + ArmourersWorkshop.proxy.getPlayerModelCacheSize());
        }
    }
}
