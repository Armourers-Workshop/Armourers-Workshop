package moe.plushie.armourers_workshop.init.platform.fabric.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class RenderTooltipEvents {

    public static ItemStack TOOLTIP_ITEM_STACK = ItemStack.EMPTY;

    public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, callbacks -> (poseStack, itemStack, x, y, width, height, mouseX, mouseY, screenWidth, screenHeight) -> {
        for (Before callback : callbacks) {
            callback.onRenderTooltip(poseStack, itemStack, x, y, width, height, mouseX, mouseY, screenWidth, screenHeight);
        }
    });

    public interface Before {
        void onRenderTooltip(PoseStack poseStack, ItemStack itemStack, int x, int y, int width, int height, int mouseX, int mouseY, int screenWidth, int screenHeight);
    }
}
