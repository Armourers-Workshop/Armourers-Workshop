package riskyken.armourersWorkshop.client;

import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientOpenCustomArmourGui;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

public class ModClientFMLEventHandler {
    
    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (Keybindings.openCustomArmourGui.isPressed()) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientOpenCustomArmourGui());
        }
    }
}
