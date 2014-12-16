package riskyken.armourersWorkshop.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ChatComponentText;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientKeyPress;
import riskyken.armourersWorkshop.common.update.UpdateCheck;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;

public class ModClientFMLEventHandler {
    
    private boolean shownUpdateInfo = false;
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equals(LibModInfo.ID)) {
            ConfigHandler.loadConfigFile();
        }
    }
    
    public void onPlayerTickEndEvent() {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        //RiskyKensUtilities.proxy.onPlayerTick(player);
        
        if (shownUpdateInfo) { return; }
        if (UpdateCheck.updateFound) {
            shownUpdateInfo = true;
            player.addChatMessage(new ChatComponentText(LibModInfo.NAME + " update " + UpdateCheck.remoteModVersion + " is available."));
        }
    }
    
    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (Keybindings.openCustomArmourGui.isPressed()) {
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
