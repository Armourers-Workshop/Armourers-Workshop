package moe.plushie.armourers_workshop.compatibility.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.common.PlayerEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityLifecycleEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Available("[1.16, )")
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
            public Player getOriginal() {
                return oldPlayer;
            }

            @Override
            public Player getPlayer() {
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
            InteractionResult[] results = {InteractionResult.PASS};
            subscriber.accept(new PlayerEvent.Attack() {
                @Override
                public Entity getTarget() {
                    return entity;
                }

                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void setCancelled(boolean isCancelled) {
                    if (isCancelled) {
                        results[0] = InteractionResult.SUCCESS;
                    } else {
                        results[0] = InteractionResult.PASS;
                    }

                }
            });
            return results[0];
        }));
    }

    public static IEventHandler<PlayerEvent.StartTracking> startTrackingFactory() {
        return (priority, receiveCancelled, subscriber) -> EntityLifecycleEvents.DID_START_TRACKING.register((target, player) -> subscriber.accept(new PlayerEvent.StartTracking() {
            @Override
            public Entity getTarget() {
                return target;
            }

            @Override
            public Player getPlayer() {
                return player;
            }
        }));
    }
}
