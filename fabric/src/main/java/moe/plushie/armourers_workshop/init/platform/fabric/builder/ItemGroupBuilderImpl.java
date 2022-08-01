package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.other.builder.IItemGroupBuilder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ItemGroupBuilderImpl<T extends CreativeModeTab> implements IItemGroupBuilder<T> {

    private Supplier<Supplier<ItemStack>> icon = () -> () -> ItemStack.EMPTY;
    private BiConsumer<List<ItemStack>, CreativeModeTab> appendItems;

    public ItemGroupBuilderImpl() {
    }

    @Override
    public IItemGroupBuilder<T> icon(Supplier<Supplier<ItemStack>> icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public IItemGroupBuilder<T> appendItems(BiConsumer<List<ItemStack>, CreativeModeTab> appendItems) {
        this.appendItems = appendItems;
        return this;
    }

    @Override
    public T build(String name) {
        return ObjectUtils.unsafeCast(creativeModeTab(name));
    }

    private CreativeModeTab creativeModeTab(String name) {
        return FabricItemGroupBuilder.create(ArmourersWorkshop.getResource(name))
                .icon(() -> icon.get().get())
                .appendItems(appendItems)
                .build();
    }
}
