package moe.plushie.armourers_workshop.client.handler;

import moe.plushie.armourers_workshop.client.settings.Keybindings;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientKeyPress;
import moe.plushie.armourers_workshop.common.update.UpdateCheck;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
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

public class ModClientFMLEventHandler {
    
    private boolean shownUpdateInfo = false;
    private boolean showmDevWarning;
    public static float renderTickTime;
    public static int skinRendersThisTick = 0;
    public static int skinRenderLastTick = 0;
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.getModID().equals(LibModInfo.ID)) {
            ConfigHandler.loadConfigFile();
            ConfigHandlerClient.loadConfigFile();
        }
    }
    
    public void onPlayerTickEndEvent() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (!shownUpdateInfo && UpdateCheck.updateFound) {
            shownUpdateInfo = true;
            TextComponentString updateMessage = new TextComponentString(TranslateUtils.translate("chat.armourers_workshop:updateAvailable", UpdateCheck.remoteModVersion) + " ");
            TextComponentString updateURL = new TextComponentString(TranslateUtils.translate("chat.armourers_workshop:updateDownload"));
            updateURL.getStyle().setUnderlined(true);
            updateURL.getStyle().setColor(TextFormatting.BLUE);
            updateURL.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TranslateUtils.translate("chat.armourers_workshop:updateDownloadRollover"))));
            updateURL.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, LibModInfo.DOWNLOAD_URL));
            updateMessage.appendSibling(updateURL);
            player.sendMessage(updateMessage);
        }
        if (!showmDevWarning && LibModInfo.DEVELOPMENT_VERSION) {
            TextComponentString devWarning = new TextComponentString(TranslateUtils.translate("chat.armourers_workshop:devWarning"));
            devWarning.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(devWarning);
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
