package moe.plushie.armourers_workshop.init.platform.fabric.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public final class ClientStartupEvents {

    public static final Event<ClientWillStart> CLIENT_WILL_START = EventFactory.createArrayBacked(ClientWillStart.class, callbacks -> (instance) -> {
        for (ClientWillStart callback : callbacks) {
            callback.onClientWillStart(instance);
        }
    });

    public static final Event<ClientLifecycleEvents.ClientStarted> CLIENT_STARTED = ClientLifecycleEvents.CLIENT_STARTED;

    public static final Event<ClientLifecycleEvents.ClientStopping> CLIENT_STOPPING = ClientLifecycleEvents.CLIENT_STOPPING;

    @FunctionalInterface
    public interface ClientWillStart {
        void onClientWillStart(Minecraft client);
    }
}
