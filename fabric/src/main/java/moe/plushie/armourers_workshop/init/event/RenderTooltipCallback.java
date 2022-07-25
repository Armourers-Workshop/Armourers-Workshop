package moe.plushie.armourers_workshop.init.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface RenderTooltipCallback {
    Event<RenderTooltipCallback> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.class, callbacks -> (poseStack, list, i, j, w, x, y, w2, h) -> {
        for (RenderTooltipCallback callback : callbacks) {
            callback.onRenderTooltip(poseStack, list, i, j, w, x, y, w2, h);
        }
    });

    void onRenderTooltip(PoseStack poseStack, List<? extends FormattedCharSequence> list, int i, int j, int w, int x, int y, int w2, int h);
}
