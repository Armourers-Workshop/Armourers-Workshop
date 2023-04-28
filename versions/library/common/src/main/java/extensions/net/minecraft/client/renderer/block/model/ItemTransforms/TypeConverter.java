package extensions.net.minecraft.client.renderer.block.model.ItemTransforms;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemTransformType;
import net.minecraft.client.renderer.block.model.ItemTransforms;

@Extension
@Available("[1.16, 1.19.4)")
public class TypeConverter {

    private static final IItemTransformType[] TYPES1 = IItemTransformType.values();
    private static final ItemTransforms.TransformType[] TYPES2 = ItemTransforms.TransformType.values();

    public static ItemTransforms.TransformType ofType(@ThisClass Class<?> clazz, IItemTransformType transformType) {
        return TYPES2[transformType.ordinal()];
    }

    public static IItemTransformType ofType(@ThisClass Class<?> clazz, ItemTransforms.TransformType transformType) {
        return TYPES1[transformType.ordinal()];
    }
}
