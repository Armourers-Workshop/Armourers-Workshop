package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.utils.EnumMapper;
import net.minecraft.world.InteractionHand;

@Available("[1.16, )")
public class AbstractInteractionHand {

    private static final EnumMapper<OpenInteractionHand, InteractionHand> MAPPER = EnumMapper.create(OpenInteractionHand.MAIN_HAND, InteractionHand.MAIN_HAND, it -> {
        it.put(OpenInteractionHand.MAIN_HAND, InteractionHand.MAIN_HAND);
        it.put(OpenInteractionHand.OFF_HAND, InteractionHand.OFF_HAND);
    });

    public static OpenInteractionHand wrap(InteractionHand hand) {
        return MAPPER.getKey(hand);
    }

    public static InteractionHand unwrap(OpenInteractionHand hand) {
        return MAPPER.getValue(hand);
    }
}
