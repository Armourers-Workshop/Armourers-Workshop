package riskyken.armourersWorkshop.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientKeyPress;
import riskyken.armourersWorkshop.common.update.UpdateCheck;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public class ModClientFMLEventHandler {
    
    private static final String DOWNLOAD_URL = "http://minecraft.curseforge.com/mc-mods/229523-armourers-workshop/files";
    private boolean shownUpdateInfo = false;
    public static float renderTickTime;
    public static int skinRendersThisTick = 0;
    public static int skinRenderLastTick = 0;
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.getModID().equals(LibModInfo.ID)) {
            ConfigHandlerClient.loadConfigFile();
        }
    }
    
    public void onPlayerTickEndEvent() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        
        if (!shownUpdateInfo && UpdateCheck.updateFound) {
            shownUpdateInfo = true;
            TextComponentString updateMessage = new TextComponentString(TranslateUtils.translate("chat.armourersworkshop:updateAvailable", UpdateCheck.remoteModVersion) + " ");
            TextComponentString updateURL = new TextComponentString(TranslateUtils.translate("chat.armourersworkshop:updateDownload"));
            updateURL.getStyle().setUnderlined(true);
            updateURL.getStyle().setColor(TextFormatting.BLUE);
            updateURL.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TranslateUtils.translate("chat.armourersworkshop:updateDownloadRollover"))));
            updateURL.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DOWNLOAD_URL));
            updateMessage.appendSibling(updateURL);
            player.addChatMessage(updateMessage);
        }
    }
    
    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (Keybindings.openCustomArmourGui.isPressed() & ConfigHandler.allowEquipmentWardrobe) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientKeyPress((byte) 0));
        }
        if (Keybindings.undo.isPressed()) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientKeyPress((byte) 1));
        }
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT) {
            if (event.type == Type.PLAYER) {
                if (event.phase == Phase.END) {
                    onPlayerTickEndEvent();
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onRenderTickEvent(RenderTickEvent event) {
        if (event.phase == Phase.START) {
            renderTickTime = event.renderTickTime;
            skinRenderLastTick = skinRendersThisTick;
            skinRendersThisTick = 0;
        }
    }
}
