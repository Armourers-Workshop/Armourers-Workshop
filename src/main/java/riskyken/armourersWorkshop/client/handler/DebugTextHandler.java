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
import riskyken.armourersWorkshop.client.render.DisplayList;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinPaintCache;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.proxies.ClientProxy;

@SideOnly(Side.CLIENT)
public class DebugTextHandler {
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        if (!ConfigHandlerClient.showF3DebugInfo) {
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
                event.left.add("Client Skin Count: " + ArmourersWorkshop.proxy.getPlayerModelCacheSize());
                if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
                    event.left.add("Common Skin Count: " + CommonSkinCache.INSTANCE.size());
                }
                event.left.add("Part Count: " + ClientSkinCache.INSTANCE.getPartCount());
                event.left.add("Player Data: " + SkinModelRenderer.INSTANCE.getSkinDataMapSize());
                int bakeQueue = ModelBakery.INSTANCE.getBakingQueueSize();
                event.left.add("Baking Queue: " + bakeQueue);
                event.left.add("Request Queue: " + (ClientSkinCache.INSTANCE.getRequestQueueSize() - bakeQueue));
                event.left.add("Texture Count: " + ClientSkinPaintCache.INSTANCE.size());
                event.left.add("Skin Render Type: " + ClientProxy.getSkinRenderType().toString().toLowerCase());
                event.left.add("Texture Render: " + ClientProxy.useSafeTextureRender());
                event.left.add("Display Lists: " + DisplayList.getListCount());
                event.left.add("Average Bake Time: " + ModelBakery.INSTANCE.getAverageBakeTime() + "ms");
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
