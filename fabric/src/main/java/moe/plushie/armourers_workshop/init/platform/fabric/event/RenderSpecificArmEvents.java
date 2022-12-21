package moe.plushie.armourers_workshop.init.platform.fabric.event;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class RenderSpecificArmEvents {

    public static final Event<SpecificArm> MAIN_HAND = EventFactory.createArrayBacked(SpecificArm.class, callbacks -> (poseStack, buffers, i, player, hand) -> {
        for (SpecificArm callback : callbacks) {
            boolean result = callback.render(poseStack, buffers, i, player, hand);
            if (!result) {
                return false;
            }
        }
        return true;
    });

    public static final Event<SpecificArm> OFF_HAND = EventFactory.createArrayBacked(SpecificArm.class, callbacks -> (poseStack, buffers, i, player, hand) -> {
        for (SpecificArm callback : callbacks) {
            boolean result = callback.render(poseStack, buffers, i, player, hand);
            if (!result) {
                return false;
            }
        }
        return true;
    });

    @FunctionalInterface
    public interface SpecificArm {
        boolean render(IPoseStack poseStack, MultiBufferSource buffers, int i, Player player, InteractionHand hand);
    }
}
