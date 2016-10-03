package riskyken.armourersWorkshop.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.client.skin.ClientSkinPaintCache;
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
        if (event.getLeft() != null && event.getLeft().size() > 0) {
            EntityPlayerSP localPlayer = Minecraft.getMinecraft().thePlayer;
            //List playerList = localPlayer.sendQueue.playerInfoList;
            event.getLeft().add("");
            event.getLeft().add(TextFormatting.GOLD + "[" + LibModInfo.NAME + "]");
            event.getLeft().add("Skins Rendered: " + ModClientFMLEventHandler.skinRenderLastTick);
            event.getLeft().add("Model Count: " + ClientSkinCache.INSTANCE.getModelCount());
            if (GuiScreen.isCtrlKeyDown()) {
                event.getLeft().add("Skin Count: " + ArmourersWorkshop.proxy.getPlayerModelCacheSize());
                if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
                    event.getLeft().add("Common Skin Count: " + CommonSkinCache.INSTANCE.size());
                }
                event.getLeft().add("Part Count: " + ClientSkinCache.INSTANCE.getPartCount());
                event.getLeft().add("Player Data: " + SkinModelRenderer.INSTANCE.getSkinDataMapSize());
                int bakeQueue = ModelBakery.INSTANCE.getBakingQueueSize();
                event.getLeft().add("Baking Queue: " + bakeQueue);
                event.getLeft().add("Request Queue: " + (ClientSkinCache.INSTANCE.getRequestQueueSize() - bakeQueue));
                event.getLeft().add("Texture Count: " + ClientSkinPaintCache.INSTANCE.size());
                event.getLeft().add("Skin Render Type: " + ClientProxy.getSkinRenderType().toString().toLowerCase());
                event.getLeft().add("TextureRender: " + ClientProxy.useSafeTextureRender());
                
                if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
                    /*
                    for (int i = 0; i < playerList.size(); i++) {
                        GuiPlayerInfo player = (GuiPlayerInfo) playerList.get(i);
                        if (player.name.equals(localPlayer.getName())) {
                            event.getLeft().add("ping:" + player.responseTime + "ms");
                            break;
                        }
                    }
                    */
                }
            } else {
                event.getLeft().add("Hold " + TextFormatting.GREEN + "Ctrl" + TextFormatting.WHITE + " for more.");  
            }
        }
    }
}
