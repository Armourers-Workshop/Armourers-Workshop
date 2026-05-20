package moe.plushie.armourers_workshop.compat.fabric.core.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.event.common.PlayerEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicReference;

@Available("[16, )")
public class AbstractFabricPlayerEvent {

    public static IEventHandler<PlayerEvent.LoggingIn> loggingInFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> subscriber.accept(() -> handler.player)));
    }

    public static IEventHandler<PlayerEvent.LoggingOut> loggingOutFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> subscriber.accept(() -> handler.player)));
    }


    public static IEventHandler<PlayerEvent.Clone> cloneFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerPlayerEvents.COPY_FROM.register(((oldPlayer, newPlayer, alive) -> subscriber.accept(new PlayerEvent.Clone() {
            @Override
            public Player original() {
                return oldPlayer;
            }

            @Override
            public Player player() {
                return newPlayer;
            }
        })));
    }

    public static IEventHandler<PlayerEvent.Death> deathFactory() {
        return (priority, receiveCancelled, subscriber) -> ServerPlayerEvents.ALLOW_DEATH.register(((player, damageSource, damageAmount) -> {
            subscriber.accept(() -> player);
            return true;
        }));
    }

    public static IEventHandler<PlayerEvent.Attack> attackFactory() {
        return (priority, receiveCancelled, subscriber) -> AttackEntityCallback.EVENT.register(((player, world, hand, entity, hitResult) -> {
            var results = new AtomicReference<OpenInteractionResult>(OpenInteractionResult.PASS);
            subscriber.accept(new PlayerEvent.Attack() {
                @Override
                public Entity target() {
                    return entity;
                }

                @Override
                public Player player() {
                    return player;
                }

                @Override
                public void setCancelled(boolean isCancelled) {
                    if (isCancelled) {
                        results.set(OpenInteractionResult.SUCCESS);
                    } else {
                        results.set(OpenInteractionResult.PASS);
                    }
                }
            });
            return AbstractInteractionResult.unwrap(results.get());
        }));
    }
}
