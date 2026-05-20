package moe.plushie.armourers_workshop.compat.fabric.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.world.InteractionResult;

@Available("[16, 26)")
public class AbstractFabricEventResult {

    public static InteractionResult wrap(OpenInteractionResult result) {
        return AbstractInteractionResult.unwrap(result);
    }
}
