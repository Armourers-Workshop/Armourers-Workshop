package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.renderer.block.model.ItemTransforms;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.utils.EnumMap;
import net.minecraft.world.item.ItemDisplayContext;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[1.20, )")
public class TypeConverter {

    private static final EnumMap<AbstractItemTransformType, ItemDisplayContext> MAPPER = EnumMap.byName(AbstractItemTransformType.NONE, ItemDisplayContext.NONE);

    public static ItemDisplayContext ofType(@ThisClass Class<?> clazz, AbstractItemTransformType transformType) {
        return MAPPER.getValue(transformType);
    }

    public static AbstractItemTransformType ofType(@ThisClass Class<?> clazz, ItemDisplayContext transformType) {
        return MAPPER.getKey(transformType);
    }
}
