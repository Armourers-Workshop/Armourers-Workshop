package moe.plushie.armourers_workshop.compat.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.client.event.AbstractRenderItemTooltipEvent;
import moe.plushie.armourers_workshop.compat.core.item.AbstractTooltipContext;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.event.client.ItemTooltipEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Available("[20, )")
public class AbstractForgeItemTooltipEvent extends AbstractForgeItemTooltipEventImpl {

    public static IEventHandler<ItemTooltipEvent.Gather> gatherFactory() {
        return AbstractForgeClientEventsImpl.ITEM_TOOLTIP_GATHER.map(event -> new ItemTooltipEvent.Gather() {
            @Override
            public ItemStack itemStack() {
                return event.getItemStack();
            }

            @Override
            public List<Component> tooltips() {
                return event.getToolTip();
            }

            @Override
            public ITooltipContext context() {
                return AbstractTooltipContext.wrap(null, null, event.getFlags());
            }
        });
    }

    public static IEventHandler<ItemTooltipEvent.Render> renderFactory() {
        return AbstractForgeClientEventsImpl.ITEM_TOOLTIP_RENDER.map(event -> AbstractRenderItemTooltipEvent.create(event.getItemStack(), compute(event), event.getScreenWidth(), event.getScreenHeight(), event.getX(), event.getY(), 0, event.getGraphics()));
    }
}
