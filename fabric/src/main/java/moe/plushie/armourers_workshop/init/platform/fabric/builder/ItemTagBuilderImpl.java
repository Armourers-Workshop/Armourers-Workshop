package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.ITagKey;
import moe.plushie.armourers_workshop.api.common.builder.IItemTagBuilder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class ItemTagBuilderImpl<T extends Item> implements IItemTagBuilder<T> {

    public ItemTagBuilderImpl() {
    }

    @Override
    public ITagKey<T> build(String name) {
        ResourceLocation registryName = ArmourersWorkshop.getResource(name);
        Tag<Item> tag = TagRegistry.item(registryName);
        return new ITagKey<T>() {
            @Override
            public boolean contains(T val) {
                return val.is(tag);
            }

            @Override
            public Tag<T> get() {
                return ObjectUtils.unsafeCast(tag);
            }

            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }
        };
    }
}
