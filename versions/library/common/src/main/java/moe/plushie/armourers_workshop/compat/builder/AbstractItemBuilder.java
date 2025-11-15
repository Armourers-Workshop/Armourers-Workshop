package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.core.item.DisplayItemProvider;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.function.Function;

@Available("[1.16, )")
public class AbstractItemBuilder<T extends Item> {

    protected final Function<Item.Properties, T> factory;
    protected final ArrayList<Function<Item.Properties, Item.Properties>> updaters = new ArrayList<>();

    protected IRegistryHolder<CreativeModeTab> group;

    public AbstractItemBuilder(Function<Item.Properties, T> factory) {
        this.factory = factory;
    }

    public void apply(IRegistryHolder<CreativeModeTab> group) {
        this.group = group;
    }

    public void apply(Function<Item.Properties, Item.Properties> updater) {
        this.updaters.add(updater);
    }

    public T build(OpenResourceLocation registryName) {
        var value = create(registryName);
        if (group != null) {
            DisplayItemProvider.addItem(group, value);
        }
        return value;
    }

    protected T create(OpenResourceLocation registryName) {
        var properties = new Item.Properties();
        for (var updater : updaters) {
            properties = updater.apply(properties);
        }
        return factory.apply(properties.setId(registryName));
    }
}
