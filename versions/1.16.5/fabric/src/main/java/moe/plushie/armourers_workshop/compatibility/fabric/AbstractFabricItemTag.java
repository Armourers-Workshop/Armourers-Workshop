package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface AbstractFabricItemTag {

    static <T extends Item> IItemTagKey<T> create(ResourceLocation registryName) {
        Tag<Item> tag = TagRegistry.item(registryName);
        return new IItemTagKey<T>() {

            @Override
            public Predicate<ItemStack> get() {
                return itemStack -> itemStack.getItem().is(tag);
            }

            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }
        };
    }
}
