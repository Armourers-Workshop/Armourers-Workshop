package moe.plushie.armourers_workshop.init.platform.fabric.event;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class RenderSpecificArmEvents {

    public static final Event<SpecificArm> ARM = EventFactory.createArrayBacked(SpecificArm.class, callbacks -> (player, lightmap, arms, context) -> {
        for (var callback : callbacks) {
            boolean result = callback.render(player, lightmap, arms, context);
            if (!result) {
                return false;
            }
        }
        return true;
    });

    @FunctionalInterface
    public interface SpecificArm {
        boolean render(Player player, int lightmap, OpenInteractionHand arms, IGraphicsContext context);
    }
}
