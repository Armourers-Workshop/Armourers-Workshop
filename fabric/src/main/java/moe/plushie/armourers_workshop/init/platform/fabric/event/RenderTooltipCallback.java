package moe.plushie.armourers_workshop.init.platform.fabric.event;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public interface RenderTooltipCallback {
    Event<RenderTooltipCallback> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.class, callbacks -> (poseStack, itemStack, x, y, width, height, mouseX, mouseY, screenWidth, screenHeight) -> {
        for (RenderTooltipCallback callback : callbacks) {
            callback.onRenderTooltip(poseStack, itemStack, x, y, width, height, mouseX, mouseY, screenWidth, screenHeight);
        }
    });

    void onRenderTooltip(IPoseStack poseStack, ItemStack itemStack, int x, int y, int width, int height, int mouseX, int mouseY, int screenWidth, int screenHeight);
}
