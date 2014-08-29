package riskyken.armourersWorkshop.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ModItems {
    
    public static Item customHeadArmour;
    public static Item customChestArmour;
    public static Item customLegsArmour;
    public static Item customFeetArmour;
    public static Item paintbrush;
    public static Item colourPicker;

    public static void init() {
        customHeadArmour = new ItemCustomArmour(ArmorMaterial.DIAMOND, 0);
        customChestArmour = new ItemCustomArmour(ArmorMaterial.DIAMOND, 1);
        customLegsArmour = new ItemCustomArmour(ArmorMaterial.DIAMOND, 2);
        customFeetArmour = new ItemCustomArmour(ArmorMaterial.DIAMOND, 3);
        paintbrush = new ItemPaintbrush();
        colourPicker = new ItemColourPicker();
    }
}
