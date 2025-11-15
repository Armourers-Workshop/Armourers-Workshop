package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ITagKey;
import moe.plushie.armourers_workshop.compat.core.block.AbstractBlockTag;
import moe.plushie.armourers_workshop.compat.core.item.AbstractItemTag;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.tags.TagKey;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.18, )")
@Extension
public class TagKeyExt {

    public static TypedProvider<ITagKey<?>> createItemTagRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.factory(registryName -> {
            var tag = TagKey.create(BuiltInRegistriesExt.ITEM_TAG, registryName.toLocation());
            return (AbstractItemTag) itemStack -> itemStack.is(tag);
        });
    }

    public static TypedProvider<ITagKey<?>> createBlockTagRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.factory(registryName -> {
            var tag = TagKey.create(BuiltInRegistriesExt.BLOCK_TAG, registryName.toLocation());
            return (AbstractBlockTag) blockState -> blockState.is(tag);
        });
    }
}
