package moe.plushie.armourers_workshop.common.world;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public class SyncWorldUpdater {

    private static final ArrayList<AsyncWorldUpdate> WORLD_UPDATES = new ArrayList<AsyncWorldUpdate>();

    private SyncWorldUpdater() {
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.CLIENT) {
            return;
        }
        World world = event.world;
        synchronized (WORLD_UPDATES) {
            for (int i = 0; i < WORLD_UPDATES.size(); i++) {
                AsyncWorldUpdate worldUpdate = WORLD_UPDATES.get(i);
                if (worldUpdate.ready()) {
                    if (worldUpdate.getDimensionId() == world.provider.getDimension()) {
                        worldUpdate.doUpdate(world);
                        WORLD_UPDATES.remove(i);
                        i--;
                    }
                }
            }
        }
    }

    public static void addWorldUpdate(AsyncWorldUpdate worldUpdate) {
        synchronized (WORLD_UPDATES) {
            WORLD_UPDATES.add(worldUpdate);
        }
    }
}
