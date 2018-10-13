package moe.plushie.armourers_workshop.common.network.messages.client;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = LibModInfo.ID, value = { Side.CLIENT })
public final class DelayedMessageHandler {

    private static final ArrayList<IDelayedMessage> DELAYED_MESSAGES = new ArrayList<IDelayedMessage>();
    
    private DelayedMessageHandler() {
    }
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase == Phase.END) {
            return;
        }
        synchronized (DELAYED_MESSAGES) {
            for (int i = 0; i < DELAYED_MESSAGES.size(); i++) {
                IDelayedMessage delayedMessage = DELAYED_MESSAGES.get(i);
                if (delayedMessage.isReady()) {
                    delayedMessage.onDelayedMessage();
                    DELAYED_MESSAGES.remove(i);
                    i--;
                }
            }
        }
    }
    
    public static void addDelayedMessage(IDelayedMessage delayedMessage) {
        synchronized (DELAYED_MESSAGES) {
            DELAYED_MESSAGES.add(delayedMessage);
        }
    }
    
    public static interface IDelayedMessage {
        
        public boolean isReady();
        
        public void onDelayedMessage();
        
    }
}
