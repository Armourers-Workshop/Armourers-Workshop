package moe.plushie.armourers_workshop.compat.core.item;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionResult;
import moe.plushie.armourers_workshop.core.utils.FastMapper;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.world.ItemInteractionResult;

@Available("[21, 26)")
public class AbstractItemInteractionResult {

    private static final FastMapper<OpenInteractionResult, ItemInteractionResult> MAPPER = FastMapper.builder(OpenInteractionResult.FAIL, ItemInteractionResult.FAIL, it -> {
        it.put(OpenInteractionResult.SUCCESS, ItemInteractionResult.SUCCESS);
        it.put(OpenInteractionResult.CONSUME, ItemInteractionResult.CONSUME);
        //it.put(OpenInteractionResult.CONSUME_PARTIAL, ItemInteractionResult.CONSUME_PARTIAL);
        it.put(OpenInteractionResult.PASS, ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        //it.put(OpenInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION, ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
        it.put(OpenInteractionResult.FAIL, ItemInteractionResult.FAIL);
    });

    public static OpenInteractionResult wrap(ItemInteractionResult result) {
        return AbstractInteractionResult.wrap(result.result());
    }

    public static ItemInteractionResult unwrap(OpenInteractionResult result) {
        return MAPPER.getValue(result);
    }
}
