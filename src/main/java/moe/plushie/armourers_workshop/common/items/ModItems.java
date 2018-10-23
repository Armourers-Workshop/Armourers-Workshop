package moe.plushie.armourers_workshop.common.items;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.items.paintingtool.ItemBlendingTool;
import moe.plushie.armourers_workshop.common.items.paintingtool.ItemBurnTool;
import moe.plushie.armourers_workshop.common.items.paintingtool.ItemColourNoiseTool;
import moe.plushie.armourers_workshop.common.items.paintingtool.ItemColourPicker;
import moe.plushie.armourers_workshop.common.items.paintingtool.ItemDodgeTool;
import moe.plushie.armourers_workshop.common.items.paintingtool.ItemHueTool;
import moe.plushie.armourers_workshop.common.items.paintingtool.ItemPaintRoller;
import moe.plushie.armourers_workshop.common.items.paintingtool.ItemPaintbrush;
import moe.plushie.armourers_workshop.common.items.paintingtool.ItemPalette;
import moe.plushie.armourers_workshop.common.items.paintingtool.ItemShadeNoiseTool;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {
    
    public static ArrayList<Item> ITEM_LIST = new ArrayList<Item>();
    
    public static Item skinTemplate;
    public static Item skin;
    
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
    public static Item palette;
    
    public static Item mannequinTool;
    public static Item wandOfStyle;
    public static Item soap;
    public static Item dyeBottle;
    public static Item guideBook;
    public static Item armourersHammer;
    public static Item debugTool;
    public static Item skinUnlock;
    public static Item linkingTool;
    public static Item giftSack;
    public static Item outfit;
    
    public static Item armourContainerItem;
    public static Item[] armourContainer;
    
    
    public ModItems() {
        MinecraftForge.EVENT_BUS.register(this);
        skinTemplate = new ItemSkinTemplate();
        skin = new ItemSkin();
        // TODO Fix buildcraft compat. setEquipmentSkinType();
        
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
        palette = new ItemPalette();
        
        mannequinTool = new ItemMannequinTool();
        wandOfStyle = new ItemWandOfStyle();
        soap = new ItemSoap();
        dyeBottle = new ItemDyeBottle();
        guideBook = new ItemGuideBook();
        armourersHammer = new ItemArmourersHammer();
        debugTool = new ItemDebugTool();
        skinUnlock = new ItemSkinUnlock();
        linkingTool = new ItemLinkingTool();
        giftSack = new ItemGiftSack();
        outfit = new ItemOutfit();
        
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
}
