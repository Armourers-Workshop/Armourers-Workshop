package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractTooltipContext;
import moe.plushie.armourers_workshop.init.platform.event.client.ItemTooltipEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderTooltipEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Available("[1.21, )")
public class AbstractFabricItemTooltipEvent {

    public static IEventHandler<ItemTooltipEvent.Gather> gatherFactory() {
        return subscriber -> ItemTooltipCallback.EVENT.register((stack, context, flags, lines) -> subscriber.accept(new ItemTooltipEvent.Gather() {
            @Override
            public ItemStack getItemStack() {
                return stack;
            }

            @Override
            public List<Component> getTooltips() {
                return lines;
            }

            @Override
            public ITooltipContext getContext() {
                return new AbstractTooltipContext<>(context, flags);
            }
        }));
    }

    public static IEventHandler<ItemTooltipEvent.Render> renderFactory() {
        return subscriber -> RenderTooltipEvents.BEFORE.register((itemStack, x, y, width, height, screenWidth, screenHeight, context) -> subscriber.accept(new ItemTooltipEvent.Render() {
            @Override
            public ItemStack getItemStack() {
                return itemStack;
            }

            @Override
            public CGRect getFrame() {
                return new CGRect(x, y, width, height);
            }

            @Override
            public float getScreenWidth() {
                return screenWidth;
            }

            @Override
            public float getScreenHeight() {
                return screenHeight;
            }

            @Override
            public CGGraphicsContext getContext() {
                return context;
            }
        }));
    }
}
