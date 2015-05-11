package riskyken.armourersWorkshop.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientKeyPress;
import riskyken.armourersWorkshop.common.update.UpdateCheck;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;

public class ModClientFMLEventHandler {
    
    private static final String DOWNLOAD_URL = "http://minecraft.curseforge.com/mc-mods/229523-armourers-workshop/files";
    private boolean shownUpdateInfo = false;
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equals(LibModInfo.ID)) {
            ConfigHandler.loadConfigFile();
        }
    }
    
    public void onPlayerTickEndEvent() {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        
        if (!shownUpdateInfo && UpdateCheck.updateFound) {
            //http://minecraft.curseforge.com/mc-mods/229523-armourers-workshop/files
            shownUpdateInfo = true;
            ChatComponentText updateMessage = new ChatComponentText(LibModInfo.NAME + " update " + UpdateCheck.remoteModVersion + " is available. ");
            ChatComponentText updateURL = new ChatComponentText("[Download]");
            updateURL.getChatStyle().setUnderlined(true);
            updateURL.getChatStyle().setColor(EnumChatFormatting.BLUE);
            updateURL.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to goto the download page")));
            updateURL.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DOWNLOAD_URL));
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
}
