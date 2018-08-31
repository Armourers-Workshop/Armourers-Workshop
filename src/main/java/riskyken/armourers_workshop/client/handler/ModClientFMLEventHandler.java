package riskyken.armourers_workshop.client.handler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;
import riskyken.armourers_workshop.client.settings.Keybindings;
import riskyken.armourers_workshop.common.config.ConfigHandler;
import riskyken.armourers_workshop.common.network.PacketHandler;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientKeyPress;

public class ModClientFMLEventHandler {
    
    private boolean shownUpdateInfo = false;
    private boolean showmDevWarning;
    public static float renderTickTime;
    public static int skinRendersThisTick = 0;
    public static int skinRenderLastTick = 0;
    /*
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equals(LibModInfo.ID)) {
            ConfigHandler.loadConfigFile();
            ConfigHandlerClient.loadConfigFile();
        }
    }
    
    public void onPlayerTickEndEvent() {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (!shownUpdateInfo && UpdateCheck.updateFound) {
            shownUpdateInfo = true;
            ChatComponentText updateMessage = new ChatComponentText(TranslateUtils.translate("chat.armourersworkshop:updateAvailable", UpdateCheck.remoteModVersion) + " ");
            ChatComponentText updateURL = new ChatComponentText(TranslateUtils.translate("chat.armourersworkshop:updateDownload"));
            updateURL.getChatStyle().setUnderlined(true);
            updateURL.getChatStyle().setColor(EnumChatFormatting.BLUE);
            updateURL.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(TranslateUtils.translate("chat.armourersworkshop:updateDownloadRollover"))));
            updateURL.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, LibModInfo.DOWNLOAD_URL));
            updateMessage.appendSibling(updateURL);
            player.addChatMessage(updateMessage);
        }
        if (!showmDevWarning && LibModInfo.DEVELOPMENT_VERSION) {
            ChatComponentText devWarning = new ChatComponentText(TranslateUtils.translate("chat.armourersworkshop:devWarning"));
            devWarning.getChatStyle().setColor(EnumChatFormatting.RED);
            player.addChatMessage(devWarning);
            showmDevWarning = true;
        }
    }
    */
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
                    //onPlayerTickEndEvent();
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
