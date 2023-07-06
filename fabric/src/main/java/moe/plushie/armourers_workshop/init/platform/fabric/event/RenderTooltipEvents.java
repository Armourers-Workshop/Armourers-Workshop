package moe.plushie.armourers_workshop.init.platform.fabric.event;

import com.apple.library.coregraphics.CGGraphicsContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class RenderTooltipEvents {

    public static ItemStack TOOLTIP_ITEM_STACK = ItemStack.EMPTY;

    public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, callbacks -> (itemStack, x, y, width, height, screenWidth, screenHeight, context) -> {
        for (Before callback : callbacks) {
            callback.onRenderTooltip(itemStack, x, y, width, height, screenWidth, screenHeight, context);
        }
    });

    public interface Before {
        void onRenderTooltip(ItemStack itemStack, int x, int y, int width, int height, int screenWidth, int screenHeight, CGGraphicsContext context);
    }
}
