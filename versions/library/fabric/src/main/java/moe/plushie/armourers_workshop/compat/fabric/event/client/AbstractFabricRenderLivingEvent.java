package moe.plushie.armourers_workshop.compat.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.client.RenderLivingEntityEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderLivingEntityEvents;

@Available("[1.16, )")
public class AbstractFabricRenderLivingEvent {

    public static IEventHandler<RenderLivingEntityEvent.Pre<?, ?>> preFactory() {
        return (priority, receiveCancelled, subscriber) -> RenderLivingEntityEvents.PRE.register(subscriber::accept);
    }

    public static IEventHandler<RenderLivingEntityEvent.Setup<?, ?>> setupFactory() {
        return (priority, receiveCancelled, subscriber) -> RenderLivingEntityEvents.SETUP.register(subscriber::accept);
    }

    public static IEventHandler<RenderLivingEntityEvent.Post<?, ?>> postFactory() {
        return (priority, receiveCancelled, subscriber) -> RenderLivingEntityEvents.POST.register(subscriber::accept);
    }
}
