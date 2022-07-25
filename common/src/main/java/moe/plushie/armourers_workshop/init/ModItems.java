package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.builder.client.render.SkinCubeItemRenderer;
import moe.plushie.armourers_workshop.builder.item.*;
import moe.plushie.armourers_workshop.core.client.render.MannequinItemRenderer;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.item.*;
import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import moe.plushie.armourers_workshop.api.registry.IItemBuilder;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ModItems {

    private static final ItemBuilder MAIN = new ItemBuilder(ModItemGroups.MAIN_GROUP);
    private static final ItemBuilder BUILDING = new ItemBuilder(ModItemGroups.BUILDING_GROUP);
    private static final ItemBuilder NONE = new ItemBuilder(null);

    public static final IRegistryObject<Item> SKIN = NONE.normal(SkinItem::new).bind(() -> SkinItemRenderer::getInstance).build("skin");
    public static final IRegistryObject<Item> MANNEQUIN = MAIN.normal(MannequinItem::new).rarity(Rarity.RARE).bind(() -> MannequinItemRenderer::getInstance).build("mannequin");

    public static final IRegistryObject<Item> SKIN_LIBRARY = MAIN.block(ModBlocks.SKIN_LIBRARY).build("skin-library");
    public static final IRegistryObject<Item> SKIN_LIBRARY_CREATIVE = MAIN.block(ModBlocks.SKIN_LIBRARY_CREATIVE).rarity(Rarity.EPIC).build("skin-library-creative");
    public static final IRegistryObject<Item> SKIN_LIBRARY_GLOBAL = MAIN.block(ModBlocks.SKIN_LIBRARY_GLOBAL).build("skin-library-global");

    public static final IRegistryObject<Item> SKINNING_TABLE = MAIN.block(ModBlocks.SKINNING_TABLE).build("skinning-table");
    public static final IRegistryObject<Item> DYE_TABLE = MAIN.block(ModBlocks.DYE_TABLE).build("dye-table");
    public static final IRegistryObject<Item> OUTFIT_MAKER = MAIN.block(ModBlocks.OUTFIT_MAKER).build("outfit-maker");
    public static final IRegistryObject<Item> HOLOGRAM_PROJECTOR = MAIN.block(ModBlocks.HOLOGRAM_PROJECTOR).build("hologram-projector");

    public static final IRegistryObject<Item> BOTTLE = MAIN.normal(BottleItem::new).build("dye-bottle");
    public static final IRegistryObject<Item> MANNEQUIN_TOOL = MAIN.normal(MannequinToolItem::new).build("mannequin-tool");
    public static final IRegistryObject<Item> ARMOURERS_HAMMER = MAIN.normal(ArmourersHammerItem::new).build("armourers-hammer");
    public static final IRegistryObject<Item> WAND_OF_STYLE = MAIN.normal(WandOfStyleItem::new).build("wand-of-style");

    public static final IRegistryObject<Item> SKIN_UNLOCK_HEAD = MAIN.unlock(SkinSlotType.HEAD).build("skin-unlock-head");
    public static final IRegistryObject<Item> SKIN_UNLOCK_CHEST = MAIN.unlock(SkinSlotType.CHEST).build("skin-unlock-chest");
    public static final IRegistryObject<Item> SKIN_UNLOCK_FEET = MAIN.unlock(SkinSlotType.FEET).build("skin-unlock-feet");
    public static final IRegistryObject<Item> SKIN_UNLOCK_LEGS = MAIN.unlock(SkinSlotType.LEGS).build("skin-unlock-legs");
    public static final IRegistryObject<Item> SKIN_UNLOCK_WINGS = MAIN.unlock(SkinSlotType.WINGS).build("skin-unlock-wings");
    public static final IRegistryObject<Item> SKIN_UNLOCK_OUTFIT = MAIN.unlock(SkinSlotType.OUTFIT).build("skin-unlock-outfit");

    public static final IRegistryObject<Item> LINKING_TOOL = MAIN.normal(LinkingToolItem::new).build("linking-tool");
    public static final IRegistryObject<Item> SKIN_TEMPLATE = MAIN.normal(FlavouredItem::new).stacksTo(64).build("skin-template");
    public static final IRegistryObject<Item> SOAP = MAIN.normal(SoapItem::new).stacksTo(64).build("soap");
    public static final IRegistryObject<Item> GIFT_SACK = MAIN.normal(GiftSackItem::new).stacksTo(64).build("gift-sack");
    //public static final RegistryObject<Item> ARMOUR_CONTAINER = MAIN.normal(FlavouredItem::new).stacksTo(16).build("armour-container");

    public static final IRegistryObject<Item> ARMOURER = BUILDING.block(ModBlocks.ARMOURER).rarity(Rarity.EPIC).build("armourer");
    public static final IRegistryObject<Item> COLOR_MIXER = BUILDING.block(ModBlocks.COLOR_MIXER).build("colour-mixer");

    public static final IRegistryObject<Item> SKIN_CUBE = BUILDING.cube(ModBlocks.SKIN_CUBE).build("skin-cube");
    public static final IRegistryObject<Item> SKIN_CUBE_GLOWING = BUILDING.cube(ModBlocks.SKIN_CUBE_GLOWING).build("skin-cube-glowing");
    public static final IRegistryObject<Item> SKIN_CUBE_GLASS = BUILDING.cube(ModBlocks.SKIN_CUBE_GLASS).build("skin-cube-glass");
    public static final IRegistryObject<Item> SKIN_CUBE_GLASS_GLOWING = BUILDING.cube(ModBlocks.SKIN_CUBE_GLASS_GLOWING).build("skin-cube-glass-glowing");

    public static final IRegistryObject<Item> PAINT_BRUSH = BUILDING.normal(PaintbrushItem::new).build("paintbrush");
    public static final IRegistryObject<Item> PAINT_ROLLER = BUILDING.normal(PaintRollerItem::new).build("paint-roller");
    public static final IRegistryObject<Item> BURN_TOOL = BUILDING.normal(BurnToolItem::new).build("burn-tool");
    public static final IRegistryObject<Item> DODGE_TOOL = BUILDING.normal(DodgeToolItem::new).build("dodge-tool");
    public static final IRegistryObject<Item> SHADE_NOISE_TOOL = BUILDING.normal(ShadeNoiseToolItem::new).build("shade-noise-tool");
    public static final IRegistryObject<Item> COLOR_NOISE_TOOL = BUILDING.normal(ColourNoiseToolItem::new).build("colour-noise-tool");
    public static final IRegistryObject<Item> BLENDING_TOOL = BUILDING.normal(BlendingToolItem::new).build("blending-tool");
    public static final IRegistryObject<Item> HUE_TOOL = BUILDING.normal(HueToolItem::new).build("hue-tool");
    public static final IRegistryObject<Item> COLOR_PICKER = BUILDING.normal(ColorPickerItem::new).build("colour-picker");
    public static final IRegistryObject<Item> BLOCK_MARKER = BUILDING.normal(BlockMarkerItem::new).build("block-marker");

    public static void init() {
    }

    private static class ItemBuilder {

        CreativeModeTab tab;

        ItemBuilder(CreativeModeTab tab) {
            this.tab = tab;
        }

        <T extends Item> IItemBuilder<Item> normal(Function<Item.Properties, T> factory) {
            return ObjectUtils.unsafeCast(BuilderManager.getInstance().createItemBuilder(factory).stacksTo(1).tab(tab));
        }

        <T extends Item> IItemBuilder<Item> rare(Function<Item.Properties, T> factory) {
            return normal(factory).rarity(Rarity.RARE);
        }

        IItemBuilder<Item> block(IRegistryObject<Block> block) {
            return normal(properties -> new BlockItem(block.get(), properties)).rarity(Rarity.RARE);
        }

        IItemBuilder<Item> cube(IRegistryObject<Block> block) {
            return normal(properties -> new SkinCubeItem(block.get(), properties)).stacksTo(64).bind(() -> SkinCubeItemRenderer::getInstance);
        }

        IItemBuilder<Item> unlock(SkinSlotType slotType) {
            return normal(properties -> new SkinUnlockItem(slotType, properties)).stacksTo(16).rarity(Rarity.UNCOMMON);
        }
    }
}
