package moe.plushie.armourers_workshop.compat.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.common.EntityEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityLifecycleEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;

@Available("[1.16, )")
public class AbstractFabricEntityEvent {

    public static IEventHandler<EntityEvent.ReloadSize> reloadSizeFactory() {
        return (priority, receiveCancelled, subscriber) -> EntityLifecycleEvents.SIZE.register((entity, pose, oldSize, newSize) -> {
            var outputSize = new EntityDimensions[1];
            outputSize[0] = newSize;
            subscriber.accept(new EntityEvent.ReloadSize() {
                @Override
                public Entity entity() {
                    return entity;
                }

                @Override
                public void setSize(EntityDimensions size) {
                    outputSize[0] = size;
                }

                @Override
                public EntityDimensions size() {
                    return outputSize[0];
                }
            });
            return outputSize[0];
        });
    }
}
