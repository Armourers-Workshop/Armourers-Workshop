package riskyken.armourersWorkshop.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ModItems {

    public static Item customArmour;

    public static void init() {
        customArmour = new ItemCustomArmour(ArmorMaterial.DIAMOND, 1);
    }
}
