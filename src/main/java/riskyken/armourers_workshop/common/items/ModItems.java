package riskyken.armourers_workshop.common.items;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import riskyken.armourers_workshop.common.addons.ModAddonManager;
import riskyken.armourers_workshop.common.items.paintingtool.ItemBlendingTool;
import riskyken.armourers_workshop.common.items.paintingtool.ItemBurnTool;
import riskyken.armourers_workshop.common.items.paintingtool.ItemColourNoiseTool;
import riskyken.armourers_workshop.common.items.paintingtool.ItemColourPicker;
import riskyken.armourers_workshop.common.items.paintingtool.ItemDodgeTool;
import riskyken.armourers_workshop.common.items.paintingtool.ItemHueTool;
import riskyken.armourers_workshop.common.items.paintingtool.ItemPaintRoller;
import riskyken.armourers_workshop.common.items.paintingtool.ItemPaintbrush;
import riskyken.armourers_workshop.common.items.paintingtool.ItemShadeNoiseTool;
import riskyken.armourers_workshop.common.lib.LibItemNames;
import riskyken.armourers_workshop.utils.ModLogger;

public class ModItems {
    
    public static ArrayList<Item> ITEM_LIST = new ArrayList<Item>();
    
    public static Item equipmentSkinTemplate;
    public static Item equipmentSkin;
    
    public static Item paintbrush;
    public static Item paintRoller;
    public static Item colourPicker;
    public static Item burnTool;
    public static Item dodgeTool;
    public static Item colourNoiseTool;
    public static Item shadeNoiseTool;
    public static Item hueTool;
    public static Item blendingTool;
    public static Item blockMarker;
    //public static Item paintballGun;
    
    public static Item mannequinTool;
    public static Item wandOfStyle;
    public static Item soap;
    public static Item dyeBottle;
    public static Item guideBook;
    public static Item armourersHammer;
    public static Item debugTool;
    public static Item skinUnlock;
    public static Item linkingTool;
    
    public static Item armourContainerItem;
    public static Item[] armourContainer;
    
    
    public ModItems() {
        MinecraftForge.EVENT_BUS.register(this);
        equipmentSkinTemplate = new ItemSkinTemplate();
        setEquipmentSkinType();
        
        //Tools
        paintbrush = new ItemPaintbrush();
        paintRoller = new ItemPaintRoller();
        colourPicker = new ItemColourPicker();
        burnTool = new ItemBurnTool();
        dodgeTool = new ItemDodgeTool();
        colourNoiseTool = new ItemColourNoiseTool();
        shadeNoiseTool = new ItemShadeNoiseTool();
        hueTool = new ItemHueTool();
        blockMarker = new ItemBlockMarker();
        blendingTool = new ItemBlendingTool();
        //paintballGun = new ItemPaintballGun();
        
        mannequinTool = new ItemMannequinTool();
        wandOfStyle = new ItemWandOfStyle();
        soap = new ItemSoap();
        dyeBottle = new ItemDyeBottle();
        guideBook = new ItemGuideBook();
        armourersHammer = new ItemArmourersHammer();
        debugTool = new ItemDebugTool();
        skinUnlock = new ItemSkinUnlock();
        linkingTool = new ItemLinkingTool();
        
        armourContainerItem = new ItemArmourContainerItem();
        armourContainer = new Item[4];
        armourContainer[0] = new ItemArmourContainer(LibItemNames.ARMOUR_CONTAINER_HEAD, EntityEquipmentSlot.HEAD);
        armourContainer[1] = new ItemArmourContainer(LibItemNames.ARMOUR_CONTAINER_CHEST, EntityEquipmentSlot.CHEST);
        armourContainer[2] = new ItemArmourContainer(LibItemNames.ARMOUR_CONTAINER_LEGS, EntityEquipmentSlot.LEGS);
        armourContainer[3] = new ItemArmourContainer(LibItemNames.ARMOUR_CONTAINER_FEET, EntityEquipmentSlot.FEET);
    }
    
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> reg = event.getRegistry();
        for (int i = 0; i < ITEM_LIST.size(); i++) {
            reg.register(ITEM_LIST.get(i));
        }
    }
    
    private void setEquipmentSkinType() {
        boolean skinTypeSet = true;
        
        if (ModAddonManager.addonBuildCraft.isSkinCompatibleVersion()) {
            try {
                Class<?> c = Class.forName("riskyken.armourers_workshop.common.items.ItemSkinRobotOverlay");
                Object classObject = c.newInstance();
                
                if (classObject instanceof ItemSkin) {
                    equipmentSkin = (ItemSkin)classObject;
                } else {
                    skinTypeSet = false;
                }
                
            } catch (Exception e) {
                ModLogger.log(Level.WARN, "Failed to load BuildCraft skinned item.");
                e.printStackTrace();
                skinTypeSet = false;
            }
        } else {
            skinTypeSet = false;
        }
        
        if (!skinTypeSet) {
            equipmentSkin = new ItemSkin();
        }
    }
}
