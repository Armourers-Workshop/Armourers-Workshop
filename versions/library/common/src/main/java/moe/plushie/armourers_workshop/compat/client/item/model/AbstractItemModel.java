package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.item.AbstractItemDisplayContext;
import moe.plushie.armourers_workshop.compat.client.item.AbstractItemTransform;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransform;
import net.minecraft.client.resources.model.BakedModel;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractItemModel {

    private final BakedModel impl;

    private AbstractItemModel(BakedModel impl) {
        this.impl = impl;
    }

    public static AbstractItemModel wrap(BakedModel model) {
        return new AbstractItemModel(model);
    }

    public static BakedModel unwrap(AbstractItemModel model) {
        return model.impl;
    }

    public OpenItemTransform getTransform(OpenItemDisplayContext transformType) {
        return AbstractItemTransform.wrap(impl.getTransforms().getTransform(AbstractItemDisplayContext.unwrap(transformType)));
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AbstractItemModel that)) return false;
        return impl.equals(that.impl);
    }

    @Override
    public int hashCode() {
        return impl.hashCode();
    }
}
