package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.other.builder.IItemGroupBuilder;
import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ModItemGroups {

    public static final CreativeModeTab MAIN_GROUP = create().icon(() -> SkinItemRenderer.getInstance()::getPlayerMannequinItem).build("main");
    public static final CreativeModeTab BUILDING_GROUP = create().icon(() -> () -> new ItemStack(ModItems.ARMOURER.get())).build("painting_tools");

    private static IItemGroupBuilder<CreativeModeTab> create() {
        return BuilderManager.getInstance().createItemGroupBuilder().appendItems(ModItemGroups::appendSortedItems);
    }

    private static void appendSortedItems(List<ItemStack> output, CreativeModeTab tab) {
        NonNullList<ItemStack> pending = NonNullList.create();
        for (Item item : getRegisteredItems()) {
            if (item.getItemCategory() == tab) {
                item.fillItemCategory(tab, pending);
            }
        }
        output.addAll(pending);
    }

    private static ArrayList<Item> getRegisteredItems() {
        ArrayList<Item> items = new ArrayList<>();
        Class<?> object = ModItems.class;
        for (Field field : object.getDeclaredFields()) {
            try {
                if (field.getModifiers() != (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL)) {
                    continue;
                }
                Object value = field.get(object);
                if (value instanceof IRegistryObject) {
                    value = ((IRegistryObject<?>) value).get();
                }
                if (value instanceof Item) {
                    items.add((Item) value);
                }
            } catch (Exception ignored) {
            }
        }
        return items;
    }

    public static void init() {
    }
}
