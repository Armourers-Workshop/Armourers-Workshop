package moe.plushie.armourers_workshop.core.handler;

import moe.plushie.armourers_workshop.builder.world.WorldUpdater;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class WorldHandler {

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            WorldUpdater.getInstance().tick(event.world);
        }
    }
}
