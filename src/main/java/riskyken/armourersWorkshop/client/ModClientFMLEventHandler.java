package riskyken.armourersWorkshop.client;

import riskyken.armourersWorkshop.common.items.ItemCustomArmour;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientArmourUpdate;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;

public class ModClientFMLEventHandler {
    
    private boolean[] armourSlots = new boolean[4];
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.CLIENT & event.type == Type.PLAYER & event.phase == Phase.END) {
            for (int i = 0; i < 4; i++) {
                if (armourSlots[i]) {
                    if (event.player.getCurrentArmor(i) == null) {
                        armourSlotUpdated(i, false);
                        armourSlots[i] = false;
                    }
                } else {
                    if (event.player.getCurrentArmor(i) != null) {
                        if (event.player.getCurrentArmor(i).getItem() instanceof ItemCustomArmour) {
                            armourSlotUpdated(i, true);
                            armourSlots[i] = true;
                        }
                    }
                }
            }
        }
    }
    
    private void armourSlotUpdated(int slotId, boolean added) {
        PacketHandler.networkWrapper.sendToServer(new MessageClientArmourUpdate((byte) slotId, added));
    }
}
