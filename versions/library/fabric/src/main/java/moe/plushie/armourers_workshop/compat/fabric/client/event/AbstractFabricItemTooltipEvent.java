package moe.plushie.armourers_workshop.compat.fabric.client.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.core.item.AbstractTooltipContext;
import moe.plushie.armourers_workshop.compat.fabric.client.AbstractFabricTooltipComponent;
import moe.plushie.armourers_workshop.init.event.client.ItemTooltipEvent;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Available("[21, )")
public class AbstractFabricItemTooltipEvent {

    public static IEventHandler<ItemTooltipEvent.Gather> gatherFactory() {
        return (priority, receiveCancelled, subscriber) -> ItemTooltipCallback.EVENT.register((stack, context, flags, lines) -> {
            var count = lines.size();
            subscriber.accept(new ItemTooltipEvent.Gather() {
                @Override
                public ItemStack itemStack() {
                    return stack;
                }

                @Override
                public List<Component> tooltips() {
                    return lines;
                }

                @Override
                public ITooltipContext context() {
                    return AbstractTooltipContext.wrap(context, null, flags);
                }
            });
            if (count != lines.size()) {
                AbstractFabricTooltipComponent.attach(lines, stack);
            }
        });
    }
}
