package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.common.IItemGroupProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IItemGroupBuilder;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemGroupBuilderImpl<T extends IItemGroup> implements IItemGroupBuilder<T> {

    private Supplier<Supplier<ItemStack>> icon = () -> () -> ItemStack.EMPTY;

    public ItemGroupBuilderImpl() {
    }

    @Override
    public IItemGroupBuilder<T> icon(Supplier<Supplier<ItemStack>> icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public IRegistryKey<T> build(String name) {
        ResourceLocation registryName = ModConstants.key(name);
        ItemGroup group = new ItemGroup(registryName);
        ModLog.debug("Registering '{}'", registryName);
        return new IRegistryKey<T>() {
            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }

            @Override
            public T get() {
                return ObjectUtils.unsafeCast(group);
            }
        };
    }

    public class ItemGroup implements IItemGroup {

        private final Supplier<CreativeModeTab> tab;
        private final ArrayList<Supplier<Item>> items = new ArrayList<>();

        public ItemGroup(ResourceLocation registryName) {
            this.tab = AbstractForgeRegistries.registerCreativeModeTab(registryName, icon, this::fill);
        }

        public void fill(List<ItemStack> results) {
            for (Supplier<Item> itemProvider : items) {
                Item item = itemProvider.get();
                results.add(item.getDefaultInstance());
                IItemGroupProvider provider = ObjectUtils.safeCast(item, IItemGroupProvider.class);
                if (provider != null) {
                    provider.fillItemGroup(results, this);
                }
            }
        }

        @Override
        public void add(Supplier<Item> item) {
            items.add(item);
        }

        @Override
        public CreativeModeTab get() {
            return tab.get();
        }
    }
}
