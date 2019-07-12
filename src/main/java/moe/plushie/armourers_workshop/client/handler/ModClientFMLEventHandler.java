package moe.plushie.armourers_workshop.client.handler;

import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.settings.Keybindings;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientKeyPress;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientKeyPress.Button;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;

public class ModClientFMLEventHandler {
    
    private boolean showmDevWarning;
    private boolean shownMoBendsWarning;
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
        if (!showmDevWarning && LibModInfo.DEVELOPMENT_VERSION) {
            TextComponentString devWarning = new TextComponentString(TranslateUtils.translate("chat.armourers_workshop:devWarning"));
            devWarning.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(devWarning);
            showmDevWarning = true;
        }
        if (!shownMoBendsWarning & ModAddonManager.addonMobends.isModLoaded()) {
            TextComponentString moBendsWarning = new TextComponentString(TranslateUtils.translate("chat.armourers_workshop:mobends.warn"));
            moBendsWarning.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(moBendsWarning);
            shownMoBendsWarning = true;
        }
    }
    
    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (Keybindings.KEY_UNDO.isPressed()) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientKeyPress(Button.UNDO));
        }
        if (Keybindings.OPEN_WARDROBE.isPressed() & ConfigHandler.canOpenWardrobe(Minecraft.getMinecraft().player)) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientKeyPress(Button.OPEN_WARDROBE));
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
