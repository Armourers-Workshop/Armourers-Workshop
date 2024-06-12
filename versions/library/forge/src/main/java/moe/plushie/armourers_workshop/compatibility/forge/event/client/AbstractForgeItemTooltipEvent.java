package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.client.gui.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractTooltipContext;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.ItemTooltipEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Available("[1.20, )")
public class AbstractForgeItemTooltipEvent {

    public static IEventHandler<ItemTooltipEvent.Gather> gatherFactory() {
        return AbstractForgeClientEventsImpl.ITEM_TOOLTIP_GATHER.map(event -> new ItemTooltipEvent.Gather() {
            @Override
            public ItemStack getItemStack() {
                return event.getItemStack();
            }

            @Override
            public List<Component> getTooltips() {
                return event.getToolTip();
            }

            @Override
            public ITooltipContext getContext() {
                return new AbstractTooltipContext<>(null, event.getFlags());
            }
        });
    }

    public static IEventHandler<ItemTooltipEvent.Render> renderFactory() {
        return AbstractForgeClientEventsImpl.ITEM_TOOLTIP_RENDER.map(event -> new ItemTooltipEvent.Render() {

            @Override
            public ItemStack getItemStack() {
                return event.getItemStack();
            }

            @Override
            public CGRect getFrame() {
                var font = event.getFont();
                var tooltips = event.getComponents();
                int mouseX = event.getX();
                int mouseY = event.getY();
                int screenWidth = event.getScreenWidth();
                int screenHeight = event.getScreenHeight();
                int i = 0;
                int j = tooltips.size() == 1 ? -2 : 0;
                for (var tooltip : tooltips) {
                    int k = tooltip.getWidth(font);
                    if (k > i) {
                        i = k;
                    }
                    j += tooltip.getHeight();
                }
                int j2 = mouseX + 12;
                int k2 = mouseY - 12;
                if (j2 + i > screenWidth) {
                    j2 -= 28 + i;
                }
                if (k2 + j + 6 > screenHeight) {
                    k2 = screenHeight - j - 6;
                }
                return new CGRect(j2, k2, i, j);
            }

            @Override
            public float getScreenWidth() {
                return event.getScreenWidth();
            }

            @Override
            public float getScreenHeight() {
                return event.getScreenHeight();
            }

            @Override
            public CGGraphicsContext getContext() {
                int mouseX = event.getX();
                int mouseY = event.getY();
                return AbstractGraphicsRenderer.of(event.getGraphics(), mouseX, mouseY, 0);
            }
        });
    }
}
