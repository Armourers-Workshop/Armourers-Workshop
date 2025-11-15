package moe.plushie.armourers_workshop.init.platform.fabric.event;

import moe.plushie.armourers_workshop.init.event.client.RenderLivingEntityEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class RenderLivingEntityEvents {

    public static final Event<Callback<RenderLivingEntityEvent.Setup<?, ?>>> SETUP = EventFactory.createArrayBacked(Callback.class, callbacks -> (event) -> {
        for (var callback : callbacks) {
            callback.accept(event);
        }
    });

    public static final Event<Callback<RenderLivingEntityEvent.Pre<?, ?>>> PRE = EventFactory.createArrayBacked(Callback.class, callbacks -> (event) -> {
        for (var callback : callbacks) {
            callback.accept(event);
        }
    });

    public static final Event<Callback<RenderLivingEntityEvent.Post<?, ?>>> POST = EventFactory.createArrayBacked(Callback.class, callbacks -> (event) -> {
        for (var callback : callbacks) {
            callback.accept(event);
        }
    });

    public interface Callback<E extends RenderLivingEntityEvent<?, ?>> {

        void accept(E event);
    }
}
