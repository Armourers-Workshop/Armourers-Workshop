package moe.plushie.armourers_workshop.init.platform.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class ClientPlayerLifecycleEvents {

    public static final Event<Clone> CLONE = EventFactory.createArrayBacked(Clone.class, callbacks -> (oldPlayer, newPlayer) -> {
        for (Clone callback : callbacks) {
            callback.accept(oldPlayer, newPlayer);
        }
    });


    @FunctionalInterface
    public interface Clone {

        void accept(Player oldPlayer, Player newPlayer);
    }
}
