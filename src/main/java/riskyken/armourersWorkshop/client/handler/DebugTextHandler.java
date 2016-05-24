package riskyken.armourersWorkshop.client.handler;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.client.skin.ClientSkinPaintCache;
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
            event.left.add("Skins Rendered: " + ModClientFMLEventHandler.skinRenderLastTick);
            event.left.add("Model Count: " + ClientSkinCache.INSTANCE.getModelCount());
            if (GuiScreen.isCtrlKeyDown()) {
                event.left.add("Skin Count: " + ArmourersWorkshop.proxy.getPlayerModelCacheSize());
                event.left.add("Part Count: " + ClientSkinCache.INSTANCE.getPartCount());
                event.left.add("Player Data: " + SkinModelRenderer.INSTANCE.getSkinDataMapSize());
                int bakeQueue = ModelBakery.INSTANCE.getBakingQueueSize();
                event.left.add("Baking Queue: " + bakeQueue);
                event.left.add("Request Queue: " + (ClientSkinCache.INSTANCE.getRequestQueueSize() - bakeQueue));
                event.left.add("Texture Count: " + ClientSkinPaintCache.INSTANCE.size());
                if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
                    for (int i = 0; i < playerList.size(); i++) {
                        GuiPlayerInfo player = (GuiPlayerInfo) playerList.get(i);
                        if (player.name.equals(localPlayer.getCommandSenderName())) {
                            event.left.add("ping:" + player.responseTime + "ms");
                            break;
                        }
                    } 
                }
            } else {
                event.left.add("Hold " + EnumChatFormatting.GREEN + "Ctrl" + EnumChatFormatting.WHITE + " for more.");  
            }
        }
    }
}
