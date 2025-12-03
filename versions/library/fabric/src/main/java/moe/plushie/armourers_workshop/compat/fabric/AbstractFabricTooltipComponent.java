package moe.plushie.armourers_workshop.compat.fabric;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.List;

@Available("[1.18, )")
@OnlyIn(Dist.CLIENT)
public class AbstractFabricTooltipComponent {

    private static final IdentityHashMap<Object, ItemStack> FAST_CACHE = new IdentityHashMap<>();

    public static ItemStack deattach(List<ClientTooltipComponent> tooltips) {
        for (var tooltip : tooltips) {
            if (tooltip instanceof ClientTextTooltip tooltip1) {
                var itemStack = FAST_CACHE.get(tooltip1.text);
                if (itemStack != null) {
                    return itemStack;
                }
            }
        }
        return null;
    }

    public static void attach(List<Component> tooltips, ItemStack itemStack) {
        if (tooltips.isEmpty()) {
            return;
        }
        var text = tooltips.get(0).getVisualOrderText();
        FAST_CACHE.put(text, itemStack);
    }

    static {
        // reset all cache when the frame start.
        EventBus.register(RenderFrameEvent.Pre.class, event -> FAST_CACHE.clear());
    }
}
