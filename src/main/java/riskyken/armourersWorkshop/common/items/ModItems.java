package riskyken.armourersWorkshop.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;

public class ModItems {
    
    public static ItemCustomArmour[] customHeadArmour;
    public static ItemCustomArmour[] customChestArmour;
    public static ItemCustomArmour[] customLegsArmour;
    public static ItemCustomArmour[] customSkirtArmour;
    public static ItemCustomArmour[] customFeetArmour;
    
    public static Item armourTemplate;
    public static Item paintbrush;
    public static Item paintRoller;
    public static Item colourPicker;
    public static Item burnTool;
    public static Item dodgeTool;
    public static Item colourNoiseTool;
    public static Item shadeNoiseTool;
    public static Item guideBook;
    
    public static void init() {
        customHeadArmour = new ItemCustomArmour[5];
        customChestArmour = new ItemCustomArmour[5];
        customLegsArmour = new ItemCustomArmour[5];
        customSkirtArmour = new ItemCustomArmour[5];
        customFeetArmour = new ItemCustomArmour[5];
        
        for (int i = 0; i < 5; i++) {
            customHeadArmour[i] = new ItemCustomArmour(ArmorMaterial.values()[i], ArmourerType.HEAD);
            customChestArmour[i] = new ItemCustomArmour(ArmorMaterial.values()[i], ArmourerType.CHEST);
            customLegsArmour[i] = new ItemCustomArmour(ArmorMaterial.values()[i], ArmourerType.LEGS);
            customSkirtArmour[i] = new ItemCustomArmour(ArmorMaterial.values()[i], ArmourerType.SKIRT);
            customFeetArmour[i] = new ItemCustomArmour(ArmorMaterial.values()[i], ArmourerType.FEET);
        }
        
        armourTemplate = new ItemArmourTemplate();
        paintbrush = new ItemPaintbrush();
        paintRoller = new ItemPaintRoller();
        colourPicker = new ItemColourPicker();
        burnTool = new ItemBurnTool();
        dodgeTool = new ItemDodgeTool();
        colourNoiseTool = new ItemColourNoiseTool();
        shadeNoiseTool = new ItemShadeNoiseTool();
        guideBook = new ItemGuideBook();
    }
}
