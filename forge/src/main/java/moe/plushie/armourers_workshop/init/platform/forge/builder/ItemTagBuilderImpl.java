package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import moe.plushie.armourers_workshop.api.common.builder.IItemTagBuilder;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ItemTagBuilderImpl<T extends Item> implements IItemTagBuilder<T> {

    public ItemTagBuilderImpl() {
    }

    @Override
    public IItemTagKey<T> build(String name) {
        ResourceLocation registryName = ModConstants.key(name);
        Supplier<IItemTagKey<Item>> tag = AbstractForgeRegistries.ITEM_TAGS.register(name);
        return new IItemTagKey<T>() {

            @Override
            public Predicate<ItemStack> get() {
                return tag.get().get();
            }

            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }
        };
    }
}
