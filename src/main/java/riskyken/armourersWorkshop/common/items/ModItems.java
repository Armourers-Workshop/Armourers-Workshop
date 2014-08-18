package riskyken.armourersWorkshop.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ModItems {
    
    public static Item customHeadArmour;
    public static Item customChestArmour;

    public static void init() {
        customHeadArmour = new ItemCustomArmour(ArmorMaterial.DIAMOND, 0);
        customChestArmour = new ItemCustomArmour(ArmorMaterial.DIAMOND, 1);
    }
}
