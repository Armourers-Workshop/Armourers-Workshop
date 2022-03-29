package moe.plushie.armourers_workshop.init.common;

import moe.plushie.armourers_workshop.core.item.*;
import moe.plushie.armourers_workshop.core.render.item.SkinItemStackRenderer;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.library.block.SkinLibraryBlock;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ModItems {

    private static final ArrayList<Item> REGISTERED_ITEMS_SORTED = new ArrayList<>();
    private static final HashMap<ResourceLocation, Item> REGISTERED_ITEMS = new HashMap<>();

    @SuppressWarnings("NullableProblems")
    private static final ItemGroup MAIN_GROUP = new ItemGroup("armourers_workshop_main") {

        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return SkinItemStackRenderer.getInstance().getPlayerMannequinItem();
        }

        @OnlyIn(Dist.CLIENT)
        public void fillItemList(NonNullList<ItemStack> lists) {
            for (Item item : REGISTERED_ITEMS_SORTED) {
                item.fillItemCategory(this, lists);
            }
        }
    };

    public static final Item SKIN = register("skin", SkinItem::new, p -> p.setISTER(() -> SkinItemStackRenderer::getInstance));
    public static final Item MANNEQUIN = register("mannequin", MannequinItem::new, p -> p.tab(MAIN_GROUP).setISTER(() -> SkinItemStackRenderer::getInstance));

    public static final Item HOLOGRAM_PROJECTOR = registerBlock("hologram-projector", ModBlocks.HOLOGRAM_PROJECTOR, p -> p.tab(MAIN_GROUP));
    public static final Item SKINNING_TABLE = registerBlock("skinning-table", ModBlocks.SKINNING_TABLE, p -> p.tab(MAIN_GROUP));
    public static final Item DYE_TABLE = registerBlock("dye-table", ModBlocks.DYE_TABLE, p -> p.tab(MAIN_GROUP));

    public static final Item SKIN_LIBRARY_CREATIVE = registerBlock("skin-library-creative", ModBlocks.SKIN_LIBRARY_CREATIVE, p -> p.tab(MAIN_GROUP));
    public static final Item SKIN_LIBRARY = registerBlock("skin-library", ModBlocks.SKIN_LIBRARY, p -> p.tab(MAIN_GROUP));
    public static final Item SKIN_LIBRARY_GLOBAL = registerBlock("skin-library-global", ModBlocks.SKIN_LIBRARY_GLOBAL, p -> p.tab(MAIN_GROUP));

    public static final Item COLOUR_MIXER = registerBlock("colour-mixer", ModBlocks.COLOUR_MIXER, p -> p.tab(MAIN_GROUP));

    public static final Item BOTTLE = register("dye-bottle", BottleItem::new, p -> p.tab(MAIN_GROUP));
    public static final Item SOAP = register("soap", FlavouredItem::new, p -> p.stacksTo(64).tab(MAIN_GROUP));

    public static final Item SKIN_TEMPLATE = register("skin-template", FlavouredItem::new, p -> p.stacksTo(16).tab(MAIN_GROUP));
//    public static final Item ARMOUR_CONTAINER = register("armour-container", FlavouredItem::new, p -> p.stacksTo(16).tab(MAIN_GROUP));

    public static final Item SKIN_UNLOCK_HEAD = register("skin-unlock-head", unlockWithBuilder(SkinSlotType.HEAD), p -> p.stacksTo(8).tab(MAIN_GROUP));
    public static final Item SKIN_UNLOCK_CHEST = register("skin-unlock-chest", unlockWithBuilder(SkinSlotType.CHEST), p -> p.stacksTo(8).tab(MAIN_GROUP));
    public static final Item SKIN_UNLOCK_FEET = register("skin-unlock-feet", unlockWithBuilder(SkinSlotType.FEET), p -> p.stacksTo(8).tab(MAIN_GROUP));
    public static final Item SKIN_UNLOCK_LEGS = register("skin-unlock-legs", unlockWithBuilder(SkinSlotType.LEGS), p -> p.stacksTo(8).tab(MAIN_GROUP));
    public static final Item SKIN_UNLOCK_WINGS = register("skin-unlock-wings", unlockWithBuilder(SkinSlotType.WINGS), p -> p.stacksTo(8).tab(MAIN_GROUP));
    public static final Item SKIN_UNLOCK_OUTFIT = register("skin-unlock-outfit", unlockWithBuilder(SkinSlotType.OUTFIT), p -> p.stacksTo(8).tab(MAIN_GROUP));

    public static final Item WAND_OF_STYLE = register("wand-of-style", WandOfStyleItem::new, p -> p.tab(MAIN_GROUP));
    public static final Item LINKING_TOOL = register("linking-tool", LinkingToolItem::new, p -> p.tab(MAIN_GROUP));


    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory) {
        return register(name, factory, null);
    }

    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Consumer<Item.Properties> customizer) {
        Item.Properties properties = new Item.Properties();
        properties.stacksTo(1);
        if (customizer != null) {
            customizer.accept(properties);
        }
        ResourceLocation registryName = AWCore.resource(name);
        T item = factory.apply(properties);
        item.setRegistryName(registryName);
        REGISTERED_ITEMS_SORTED.add(item);
        REGISTERED_ITEMS.put(registryName, item);
        return item;
    }

    private static BlockItem registerBlock(String name, Block block, Consumer<Item.Properties> customizer) {
        return register(name, properties -> new BlockItem(block, properties), customizer);
    }

    private static Function<Item.Properties, SKinUnlockItem> unlockWithBuilder(SkinSlotType slotType) {
        return properties -> new SKinUnlockItem(slotType, properties);
    }

    public static void forEach(Consumer<Item> action) {
        REGISTERED_ITEMS_SORTED.forEach(action);
    }
}
