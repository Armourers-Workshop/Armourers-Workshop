package moe.plushie.armourers_workshop.compat.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;

@Available("[1.16, 1.22)")
public class AbstractFabricClientPickBlock {

    public static final Event<ClientPickBlockGatherCallback> EVENT = ClientPickBlockGatherCallback.EVENT;
}
