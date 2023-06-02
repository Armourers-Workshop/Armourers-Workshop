package extensions.net.minecraft.client.renderer.block.model.ItemTransforms;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import net.minecraft.world.item.ItemDisplayContext;

@Extension
@Available("[1.20, )")
public class TypeConverter {

    private static final AbstractItemTransformType[] TYPES1 = AbstractItemTransformType.values();
    private static final ItemDisplayContext[] TYPES2 = ItemDisplayContext.values();

    public static ItemDisplayContext ofType(@ThisClass Class<?> clazz, AbstractItemTransformType transformType) {
        return TYPES2[transformType.ordinal()];
    }

    public static AbstractItemTransformType ofType(@ThisClass Class<?> clazz, ItemDisplayContext transformType) {
        return TYPES1[transformType.ordinal()];
    }
}
