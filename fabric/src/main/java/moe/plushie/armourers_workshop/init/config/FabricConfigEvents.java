package moe.plushie.armourers_workshop.init.config;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class FabricConfigEvents {

    public static final Event<OnUpdate> LOADING = EventFactory.createArrayBacked(OnUpdate.class, callbacks -> (config) -> {
        for (OnUpdate callback : callbacks) {
            callback.config(config);
        }
    });

    public static final Event<OnUpdate> RELOADING = EventFactory.createArrayBacked(OnUpdate.class, callbacks -> (config) -> {
        for (OnUpdate callback : callbacks) {
            callback.config(config);
        }
    });

    public interface OnUpdate {
        void config(FabricConfig config);
    }
}
