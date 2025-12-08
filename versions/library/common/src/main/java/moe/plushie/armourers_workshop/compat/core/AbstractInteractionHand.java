package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.FastMapper;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import net.minecraft.world.InteractionHand;

@Available("[1.16, )")
public class AbstractInteractionHand {

    private static final FastMapper<OpenInteractionHand, InteractionHand> MAPPER = FastMapper.builder(OpenInteractionHand.MAIN_HAND, InteractionHand.MAIN_HAND, it -> {
        it.put(OpenInteractionHand.MAIN_HAND, InteractionHand.MAIN_HAND);
        it.put(OpenInteractionHand.OFF_HAND, InteractionHand.OFF_HAND);
    });

    public static OpenInteractionHand wrap(InteractionHand hand) {
        if (hand != null) {
            return MAPPER.getKey(hand);
        }
        return null;
    }

    public static InteractionHand unwrap(OpenInteractionHand hand) {
        if (hand != null) {
            return MAPPER.getValue(hand);
        }
        return null;
    }
}
