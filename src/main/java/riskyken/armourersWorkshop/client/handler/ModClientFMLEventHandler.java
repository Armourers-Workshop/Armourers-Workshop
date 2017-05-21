package riskyken.armourersWorkshop.client.handler;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientKeyPress;
import riskyken.armourersWorkshop.common.update.UpdateCheck;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public class ModClientFMLEventHandler {
    
    private static final String DOWNLOAD_URL = "https://minecraft.curseforge.com/projects/armourers-workshop/files";
    private boolean shownUpdateInfo = false;
    private boolean showmDevWarning;
    public static float renderTickTime;
    public static int skinRendersThisTick = 0;
    public static int skinRenderLastTick = 0;
    
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
            updateURL.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DOWNLOAD_URL));
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
