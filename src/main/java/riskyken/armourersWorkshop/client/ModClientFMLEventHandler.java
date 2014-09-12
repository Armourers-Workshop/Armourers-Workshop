package riskyken.armourersWorkshop.client;

import riskyken.armourersWorkshop.client.render.ItemModelRenderManager;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientOpenCustomArmourGui;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;

public class ModClientFMLEventHandler {
    
    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (Keybindings.openCustomArmourGui.isPressed()) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientOpenCustomArmourGui());
        }
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT) {
            if (event.type == Type.PLAYER) {
                if (event.phase == Phase.END) {
                    //if (event.player.worldObj.getTotalWorldTime() % 40L != 0L) {
                        ItemModelRenderManager.tick();
                    //}
                }
            }
        }
    }
}
