package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface AbstractForgeItemTag {

    static <T extends Item> IItemTagKey<T> create(ResourceLocation registryName) {
        TagKey<Item> tag = TagKey.create(Registry.ITEM_REGISTRY, registryName);
        return new IItemTagKey<>() {

            @Override
            public Predicate<ItemStack> get() {
                return itemStack -> itemStack.is(tag);
            }

            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }
        };
    }
}
