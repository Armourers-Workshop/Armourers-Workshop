package extensions.net.minecraft.client.renderer.block.model.ItemTransforms;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import net.minecraft.client.renderer.block.model.ItemTransforms;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[1.16, 1.20)")
public class TypeConverter {

    private static final AbstractItemTransformType[] TYPES1 = AbstractItemTransformType.values();
    private static final ItemTransforms.TransformType[] TYPES2 = ItemTransforms.TransformType.values();

    public static ItemTransforms.TransformType ofType(@ThisClass Class<?> clazz, AbstractItemTransformType transformType) {
        return TYPES2[transformType.ordinal()];
    }

    public static AbstractItemTransformType ofType(@ThisClass Class<?> clazz, ItemTransforms.TransformType transformType) {
        return TYPES1[transformType.ordinal()];
    }
}
