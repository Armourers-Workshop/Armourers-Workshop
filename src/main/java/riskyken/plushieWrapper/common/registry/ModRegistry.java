package riskyken.plushieWrapper.common.registry;

import java.util.HashMap;

import net.minecraft.item.Item;
import riskyken.plushieWrapper.common.item.PlushieItem;
import riskyken.plushieWrapper.common.item.ModItemWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModRegistry {

    private static final HashMap<PlushieItem, Item> itemMap = new HashMap<PlushieItem, Item>();
    
    public static void registerItem(PlushieItem item) {
        ModItemWrapper itemWrapper = new ModItemWrapper(item);
        itemMap.put(item, itemWrapper);
        GameRegistry.registerItem(itemWrapper, item.getName());
    }
    
    public static Item getMinecraftItem(PlushieItem item) {
        return itemMap.get(item);
    }
}
