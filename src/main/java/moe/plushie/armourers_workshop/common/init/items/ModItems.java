package moe.plushie.armourers_workshop.common.init.items;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.init.items.paintingtool.ItemBlendingTool;
import moe.plushie.armourers_workshop.common.init.items.paintingtool.ItemBurnTool;
import moe.plushie.armourers_workshop.common.init.items.paintingtool.ItemColourNoiseTool;
import moe.plushie.armourers_workshop.common.init.items.paintingtool.ItemColourPicker;
import moe.plushie.armourers_workshop.common.init.items.paintingtool.ItemDodgeTool;
import moe.plushie.armourers_workshop.common.init.items.paintingtool.ItemHueTool;
import moe.plushie.armourers_workshop.common.init.items.paintingtool.ItemPaintRoller;
import moe.plushie.armourers_workshop.common.init.items.paintingtool.ItemPaintbrush;
import moe.plushie.armourers_workshop.common.init.items.paintingtool.ItemShadeNoiseTool;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

    public static final ArrayList<Item> ITEM_LIST = new ArrayList<Item>();

    public static final Item SKIN_TEMPLATE = new ItemSkinTemplate();
    public static final Item SKIN = new ItemSkin();

    public static final Item PAINT_BRUSH = new ItemPaintbrush();
    public static final Item PAINT_ROLLER = new ItemPaintRoller();
    public static final Item COLOUR_PICKER = new ItemColourPicker();
    public static final Item BURN_TOOL = new ItemBurnTool();
    public static final Item DODGE_TOOL = new ItemDodgeTool();
    public static final Item COLOUR_NOISE_TOOL = new ItemColourNoiseTool();
    public static final Item SHADE_NOISE_TOOL = new ItemShadeNoiseTool();
    public static final Item HUE_TOOL = new ItemHueTool();
    public static final Item BLENDING_TOOL = new ItemBlockMarker();
    public static final Item BLOCK_MARKER = new ItemBlendingTool();
    // public static final Item PAINTBALL_GUN = new ItemPaintballGun();
    // public static final Item PALETTE = new ItemPalette();

    public static final Item MANNEQUIN_TOOL = new ItemMannequinTool();
    public static final Item WAND_OF_STYLE = new ItemWandOfStyle();
    public static final Item SOAP = new ItemSoap();
    public static final Item DYE_BOTTLE = new ItemDyeBottle();
    public static final Item GUIDE_BOOK = new ItemGuideBook();
    public static final Item ARMOURERS_HAMMER = new ItemArmourersHammer();
    public static final Item DEBUG_TOOL = new ItemDebugTool();
    public static final Item SKIN_UNLOCK = new ItemSkinUnlock();
    public static final Item LINKING_TOOL = new ItemLinkingTool();
    public static final Item GIFT_SACK = new ItemGiftSack();
    public static final Item MANNEQUIN = new ItemMannequin();

    public static final Item ARMOUR_CONTAINER_ITEM = new ItemArmourContainerItem();
    public static final Item[] ARMOUR_CONTAINER = new Item[4];

    public ModItems() {
        MinecraftForge.EVENT_BUS.register(this);
        ARMOUR_CONTAINER[0] = new ItemArmourContainer(LibItemNames.ARMOUR_CONTAINER_HEAD, EntityEquipmentSlot.HEAD);
        ARMOUR_CONTAINER[1] = new ItemArmourContainer(LibItemNames.ARMOUR_CONTAINER_CHEST, EntityEquipmentSlot.CHEST);
        ARMOUR_CONTAINER[2] = new ItemArmourContainer(LibItemNames.ARMOUR_CONTAINER_LEGS, EntityEquipmentSlot.LEGS);
        ARMOUR_CONTAINER[3] = new ItemArmourContainer(LibItemNames.ARMOUR_CONTAINER_FEET, EntityEquipmentSlot.FEET);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> reg = event.getRegistry();
        for (int i = 0; i < ITEM_LIST.size(); i++) {
            reg.register(ITEM_LIST.get(i));
        }
    }
}
