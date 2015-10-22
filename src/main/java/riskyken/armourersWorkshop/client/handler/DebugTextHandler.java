package riskyken.armourersWorkshop.client.handler;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
public class DebugTextHandler {
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        if (!ConfigHandler.showF3DebugInfo) {
            return;
        }
        if (event.left != null && event.left.size() > 0) {
            EntityClientPlayerMP localPlayer = Minecraft.getMinecraft().thePlayer;
            List playerList = localPlayer.sendQueue.playerInfoList;
            event.left.add("");
            event.left.add(EnumChatFormatting.GOLD + "[" + LibModInfo.NAME + "]");
            String dataLine = "";
            dataLine += "sc:" + ArmourersWorkshop.proxy.getPlayerModelCacheSize() + " ";
            dataLine += "pc:" + ClientSkinCache.INSTANCE.getPartCount() + " ";
            dataLine += "mc:" + ClientSkinCache.INSTANCE.getModelCount() + " ";
            dataLine += "pd:" + EquipmentModelRenderer.INSTANCE.getSkinDataMapSize() + " ";
            event.left.add(dataLine);
            dataLine = "bq:" + ModelBakery.INSTANCE.getBakingQueueSize() + " ";
            dataLine += "rq:" + ClientSkinCache.INSTANCE.getRequestQueueSize() + " ";
            dataLine += "sr:" + ModClientFMLEventHandler.skinRenderLastTick;
            if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
                for (int i = 0; i < playerList.size(); i++) {
                    GuiPlayerInfo player = (GuiPlayerInfo) playerList.get(i);
                    if (player.name.equals(localPlayer.getCommandSenderName())) {
                        dataLine += " ping:" + player.responseTime + "ms";
                        break;
                    }
                } 
            }
            
            event.left.add(dataLine);
        }
    }
}
