package moe.plushie.armourers_workshop.compat.fabric.core.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.core.AbstractDirection;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionHand;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionResult;
import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricEventResult;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.platform.fabric.event.common.FabricEntityEvent;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.concurrent.atomic.AtomicReference;

@Available("[16, )")
public class AbstractFabricEntityEvent {

    public static IEventHandler<FabricEntityEvent.StartSleep> startSleepFactory() {
        return (priority, receiveCancelled, subscriber) -> EntitySleepEvents.ALLOW_BED.register((entity, sleepingPos, state, vanillaResult) -> {
            var result = new AtomicReference<OpenInteractionResult>(OpenInteractionResult.PASS);
            subscriber.accept(new FabricEntityEvent.StartSleep() {
                @Override
                public LivingEntity entity() {
                    return entity;
                }

                @Override
                public BlockPos sleepingPos() {
                    return sleepingPos;
                }

                @Override
                public BlockState blockState() {
                    return state;
                }

                @Override
                public void setResult(OpenInteractionResult value) {
                    result.set(value);
                }
            });
            return AbstractFabricEventResult.wrap(result.get());
        });
    }

    public static IEventHandler<FabricEntityEvent.StopSleep> stopSleepFactory() {
        return (priority, receiveCancelled, subscriber) -> EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> subscriber.accept(new FabricEntityEvent.StopSleep() {
            @Override
            public LivingEntity entity() {
                return entity;
            }

            @Override
            public BlockPos sleepingPos() {
                return sleepingPos;
            }
        }));
    }

    public static IEventHandler<FabricEntityEvent.UseBlock> useBlockFactory() {
        return (priority, receiveCancelled, subscriber) -> UseBlockCallback.EVENT.register((entity, level, hand, hitResult) -> {
            var result = new AtomicReference<OpenInteractionResult>(OpenInteractionResult.PASS);
            subscriber.accept(new FabricEntityEvent.UseBlock() {
                @Override
                public LivingEntity entity() {
                    return entity;
                }

                @Override
                public Level level() {
                    return level;
                }

                @Override
                public OpenInteractionHand hand() {
                    return AbstractInteractionHand.wrap(hand);
                }

                @Override
                public BlockHitResult hitResult() {
                    return hitResult;
                }

                @Override
                public void setResult(OpenInteractionResult value) {
                    result.set(value);
                }
            });
            return AbstractInteractionResult.unwrap(result.get());
        });
    }

    public static IEventHandler<FabricEntityEvent.AttackBlock> attackBlockFactory() {
        return (priority, receiveCancelled, subscriber) -> AttackBlockCallback.EVENT.register((entity, level, hand, pos, direction) -> {
            var result = new AtomicReference<OpenInteractionResult>(OpenInteractionResult.PASS);
            subscriber.accept(new FabricEntityEvent.AttackBlock() {
                @Override
                public LivingEntity entity() {
                    return entity;
                }

                @Override
                public Level level() {
                    return level;
                }

                @Override
                public OpenInteractionHand hand() {
                    return AbstractInteractionHand.wrap(hand);
                }

                @Override
                public BlockPos pos() {
                    return pos;
                }

                @Override
                public OpenDirection direction() {
                    return AbstractDirection.wrap(direction);
                }

                @Override
                public void setResult(OpenInteractionResult value) {
                    result.set(value);
                }
            });
            return AbstractInteractionResult.unwrap(result.get());
        });
    }
}
