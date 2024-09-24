package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.ClientPlayerEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientPlayerLifecycleEvents;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;

@Available("[1.16, )")
public class AbstractFabricClientPlayerEvent {

    public static IEventHandler<ClientPlayerEvent.LoggingIn> loggingInFactory() {
        return (priority, receiveCancelled, subscriber) -> ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> RenderSystem.recordRenderCall(() -> subscriber.accept(() -> client.player)));
    }

    public static IEventHandler<ClientPlayerEvent.LoggingOut> loggingOutFactory() {
        return (priority, receiveCancelled, subscriber) -> ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> RenderSystem.recordRenderCall(() -> subscriber.accept(() -> client.player)));
    }

    public static IEventHandler<ClientPlayerEvent.Clone> cloneFactory() {
        return (priority, receiveCancelled, subscriber) -> ClientPlayerLifecycleEvents.CLONE.register(((oldPlayer, newPlayer) -> subscriber.accept(new ClientPlayerEvent.Clone() {
            @Override
            public Player getOldPlayer() {
                return oldPlayer;
            }

            @Override
            public Player getNewPlayer() {
                return newPlayer;
            }
        })));
    }
}
