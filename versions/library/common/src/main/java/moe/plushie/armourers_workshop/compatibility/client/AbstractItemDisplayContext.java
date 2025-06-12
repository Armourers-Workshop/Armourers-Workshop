package moe.plushie.armourers_workshop.compatibility.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.utils.EnumMapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemDisplayContext;

@Available("[1.20, )")
@Environment(EnvType.CLIENT)
public class AbstractItemDisplayContext {

    private static final EnumMapper<OpenItemDisplayContext, ItemDisplayContext> MAPPER = EnumMapper.create(OpenItemDisplayContext.NONE, ItemDisplayContext.NONE, builder -> {
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
