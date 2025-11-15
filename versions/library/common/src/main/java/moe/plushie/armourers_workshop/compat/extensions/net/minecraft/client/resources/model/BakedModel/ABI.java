package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.client.resources.model.BakedModel;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.AbstractItemDisplayContext;
import moe.plushie.armourers_workshop.compat.client.AbstractItemTransform;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransform;
import net.minecraft.client.resources.model.BakedModel;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.22)")
@Extension
public class ABI {

    public static OpenItemTransform getTransform(@This BakedModel bakedModel, OpenItemDisplayContext transformType) {
        return AbstractItemTransform.wrap(bakedModel.getTransforms().getTransform(AbstractItemDisplayContext.unwrap(transformType)));
    }
}
