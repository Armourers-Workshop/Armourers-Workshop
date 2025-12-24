package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.FastMapper;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.world.InteractionResult;

@Available("[1.16, 1.26)")
public class AbstractInteractionResult {

    private static final FastMapper<OpenInteractionResult, InteractionResult> MAPPER = FastMapper.builder(OpenInteractionResult.FAIL, InteractionResult.FAIL, it -> {
        it.put(OpenInteractionResult.SUCCESS, InteractionResult.SUCCESS);
        it.put(OpenInteractionResult.CONSUME, InteractionResult.CONSUME);
        it.put(OpenInteractionResult.PASS, InteractionResult.PASS);
        it.put(OpenInteractionResult.FAIL, InteractionResult.FAIL);
        // only value side
        it.putValue(OpenInteractionResult.SUCCESS_SERVER, InteractionResult.SUCCESS);
        it.putValue(OpenInteractionResult.TRY_WITH_EMPTY_HAND, InteractionResult.SUCCESS);
        // only key side
        //it.putKey(InteractionResult.SUCCESS_NO_ITEM_USED, OpenInteractionResult.SUCCESS);
        //it.putKey(InteractionResult.CONSUME_PARTIAL, OpenInteractionResult.CONSUME);
    });

    public static OpenInteractionResult wrap(InteractionResult result) {
        return MAPPER.getKey(result);
    }

    public static InteractionResult unwrap(OpenInteractionResult result) {
        var result1 = MAPPER.getValue(result);
        if (result1 != null) {
            return result1;
        }
        if (result instanceof OpenInteractionResult.Success result2) {
            if (result2.swingSource() == OpenInteractionResult.SwingSource.NONE) {
                return InteractionResult.CONSUME;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
