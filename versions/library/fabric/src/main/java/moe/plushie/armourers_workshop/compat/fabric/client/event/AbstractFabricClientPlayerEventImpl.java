package moe.plushie.armourers_workshop.compat.fabric.client.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.event.client.ClientPlayerEvent;
import net.minecraft.world.entity.player.Player;

@Available("[16, )")
public class AbstractFabricClientPlayerEventImpl {

    public static ClientPlayerEvent.Clone clone(Player oldPlayer, Player newPlayer) {
        return new ClientPlayerEvent.Clone() {
            @Override
            public Player oldPlayer() {
                return oldPlayer;
            }

            @Override
            public Player newPlayer() {
                return newPlayer;
            }
        };
    }
}
