package moe.plushie.armourers_workshop.client.handler;

import com.google.common.cache.CacheStats;

import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.model.bake.ModelBakery;
import moe.plushie.armourers_workshop.client.render.DisplayList;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinPaintCache;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DebugTextHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        if (!ConfigHandlerClient.showF3DebugInfo) {
            return;
        }
        if (event.getType() != ElementType.TEXT) {
            return;
        }
        if (event.getLeft() != null && event.getLeft().size() > 0) {
            EntityPlayerSP localPlayer = Minecraft.getMinecraft().player;

            if (!event.getLeft().get(0).contains("1.12.2")) {
                return;
            }

            event.getLeft().add("");
            event.getLeft().add(TextFormatting.GOLD + "[" + LibModInfo.NAME + "]");
            event.getLeft().add("Skins Rendered: " + ModClientFMLEventHandler.skinRenderLastTick);
            event.getLeft().add("GPU Model Count: " + ClientSkinCache.INSTANCE.getModelCount());
            if (GuiScreen.isCtrlKeyDown() != LibModInfo.DEVELOPMENT_VERSION) {
                int size = ClientSkinCache.INSTANCE.getCacheSize();
                CacheStats skinStats = ClientSkinCache.INSTANCE.getStats();
                event.getLeft().add(String.format("Client Skin Cache: %d - HR: %.2f%%", size, skinStats.hitRate()));
                event.getLeft().add("Skin Parts Count: " + ClientSkinCache.INSTANCE.getPartCount());
                if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
                    event.getLeft().add("Common Skin Cache: S[" + CommonSkinCache.INSTANCE.size() + "] F[" + CommonSkinCache.INSTANCE.fileLinkSize() + "] G[" + CommonSkinCache.INSTANCE.globalLinkSize() + "]");
                }

                int bakeQueue = ModelBakery.INSTANCE.getBakingQueueSize();
                event.getLeft().add("Baking Queue: " + bakeQueue);
                event.getLeft().add("Request Queue: " + (ClientSkinCache.INSTANCE.getRequestQueueSize() - bakeQueue));
                event.getLeft().add("Texture Count: " + ClientSkinPaintCache.INSTANCE.size());
                event.getLeft().add("Display Lists: " + DisplayList.getListCount());
                event.getLeft().add("Average Bake Time: " + ModelBakery.INSTANCE.getAverageBakeTime() + "ms");
                event.getLeft().add("Paint Type: " + ClientProxy.getTexturePaintType().toString());
                /*
                 * List playerList = localPlayer.sendQueue.playerInfoList; if
                 * (!Minecraft.getMinecraft().isIntegratedServerRunning()) { for (int i = 0; i <
                 * playerList.size(); i++) { GuiPlayerInfo player = (GuiPlayerInfo)
                 * playerList.get(i); if
                 * (player.name.equals(localPlayer.getCommandSenderName())) {
                 * event.left.add("ping:" + player.responseTime + "ms"); break; } } }
                 */
            } else {
                event.getLeft().add("Hold " + TextFormatting.GREEN + "Ctrl" + TextFormatting.WHITE + " for more.");
            }
        }
    }

}
