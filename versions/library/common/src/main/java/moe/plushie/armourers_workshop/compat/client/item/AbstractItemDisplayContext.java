package moe.plushie.armourers_workshop.compat.client.item;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.FastMapper;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.world.item.ItemDisplayContext;

@Available("[1.20, )")
public class AbstractItemDisplayContext {

    private static final FastMapper<OpenItemDisplayContext, ItemDisplayContext> MAPPER = FastMapper.builder(OpenItemDisplayContext.NONE, ItemDisplayContext.NONE, builder -> {
        builder.put(OpenItemDisplayContext.NONE, ItemDisplayContext.NONE);
        builder.put(OpenItemDisplayContext.THIRD_PERSON_LEFT_HAND, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        builder.put(OpenItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        builder.put(OpenItemDisplayContext.FIRST_PERSON_LEFT_HAND, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
        builder.put(OpenItemDisplayContext.FIRST_PERSON_RIGHT_HAND, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
        builder.put(OpenItemDisplayContext.HEAD, ItemDisplayContext.HEAD);
        builder.put(OpenItemDisplayContext.GROUND, ItemDisplayContext.GROUND);
        builder.put(OpenItemDisplayContext.GUI, ItemDisplayContext.GUI);
        builder.put(OpenItemDisplayContext.FIXED, ItemDisplayContext.FIXED);
    });

    public static OpenItemDisplayContext wrap(ItemDisplayContext transformType) {
        return MAPPER.getKey(transformType);
    }

    public static ItemDisplayContext unwrap(OpenItemDisplayContext transformType) {
        return MAPPER.getValue(transformType);
    }
}
