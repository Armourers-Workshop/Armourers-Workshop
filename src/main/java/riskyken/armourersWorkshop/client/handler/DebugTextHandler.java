package riskyken.armourersWorkshop.client.handler;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.model.ClientModelCache;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DebugTextHandler {
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        if (event.left != null && event.left.size() > 0) {
            event.left.add("");
            event.left.add(EnumChatFormatting.GOLD + "[" + LibModInfo.NAME + "]");
            String dataLine = "";
            dataLine += "ModelCache:" + ArmourersWorkshop.proxy.getPlayerModelCacheSize() + " - ";
            dataLine += "PlayerData:" + EquipmentModelRenderer.INSTANCE.getSkinDataMapSize();
            event.left.add(dataLine);
            dataLine = "BakingQueue:" + ModelBakery.INSTANCE.getBakingQueueSize() + " - ";
            dataLine += "RequestQueue:" + ClientModelCache.INSTANCE.getRequestQueueSize();
            event.left.add(dataLine);
        }
    }
}
