package riskyken.minecraftWrapper.common.registry;

import java.util.HashMap;

import net.minecraft.item.Item;
import riskyken.minecraftWrapper.common.item.ModItem;
import riskyken.minecraftWrapper.common.item.ModItemWrapper;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModRegistry {

    private static final HashMap<ModItem, Item> itemMap = new HashMap<ModItem, Item>();
    
    public static void registerItem(ModItem item) {
        ModItemWrapper itemWrapper = new ModItemWrapper(item);
        itemMap.put(item, itemWrapper);
        GameRegistry.registerItem(itemWrapper, item.getName());
    }
    
    public static Item getMinecraftItem(ModItem item) {
        return itemMap.get(item);
    }
}
