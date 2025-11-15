package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class DisplayItemProvider {

    private static final ArrayList<Entry> DISPLAY_ITEMS = new ArrayList<>();

    public static void addItem(IRegistryHolder<CreativeModeTab> tab, Item item) {
        DISPLAY_ITEMS.add(new Entry(tab, item));
    }

    public static List<Item> getItem(CreativeModeTab tab) {
        var items = new ArrayList<Item>();
        for (var entry : DISPLAY_ITEMS) {
            if (tab == entry.tab.get()) {
                items.add(entry.item);
            }
        }
        return items;
    }

    private static class Entry {

        private final IRegistryHolder<CreativeModeTab> tab;
        private final Item item;

        private Entry(IRegistryHolder<CreativeModeTab> tab, Item item) {
            this.tab = tab;
            this.item = item;
        }
    }
}
