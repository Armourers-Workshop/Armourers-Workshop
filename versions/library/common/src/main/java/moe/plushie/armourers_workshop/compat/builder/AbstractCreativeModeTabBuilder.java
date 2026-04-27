package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.compat.core.item.AbstractItemHandler;
import moe.plushie.armourers_workshop.core.item.DisplayItemProvider;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractCreativeModeTabBuilder<T extends CreativeModeTab> {

    protected Supplier<Supplier<ItemStack>> icon = () -> () -> ItemStack.EMPTY;

    public void icon(Supplier<Supplier<ItemStack>> icon) {
        this.icon = icon;
    }

    public T build(OpenResourceKey registryName) {
        var reference = new CreativeModeTab[1];
        var tab = create(registryName, displayItems -> {
            var tab1 = reference[0];
            for (var item : DisplayItemProvider.getItem(tab1)) {
                if (item instanceof AbstractItemHandler handler) {
                    handler.fill(displayItems, tab1);
                }
            }
        });
        reference[0] = tab;
        return Objects.unsafeCast(tab);
    }

    protected abstract CreativeModeTab create(OpenResourceKey registryName, Consumer<List<ItemStack>> provider);
}
