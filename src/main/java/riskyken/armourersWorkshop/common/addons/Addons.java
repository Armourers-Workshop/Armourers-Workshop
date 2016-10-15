package riskyken.armourersWorkshop.common.addons;

import java.util.ArrayList;
import java.util.IdentityHashMap;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import riskyken.armourersWorkshop.client.render.item.RenderItemBowSkin;
import riskyken.armourersWorkshop.client.render.item.RenderItemSwordSkin;
import riskyken.armourersWorkshop.utils.EventState;
import riskyken.armourersWorkshop.utils.ModLogger;

public final class Addons {
    
    private static IdentityHashMap<Item, IItemRenderer> customItemRenderers = Maps.newIdentityHashMap();
    
    private static ArrayList<AbstractAddon> loadedAddons = new ArrayList<AbstractAddon>(); 
    
    public static final String[] overrideSwordsDefault = {
        //Minecraft
        "minecraft:wooden_sword",
        "minecraft:stone_sword",
        "minecraft:iron_sword",
        "minecraft:golden_sword",
        "minecraft:diamond_sword",
        "",
        
        //Botania
        "Botania:manasteelSword",
        "Botania:terraSword",
        "Botania:elementiumSword",
        "Botania:excaliber",
        "",
        
        //BetterStorage
        "betterstorage:cardboardSword",
        "",
        
        //Balkon's Weapon Mod
        "weaponmod:battleaxe.wood",
        "weaponmod:battleaxe.stone",
        "weaponmod:battleaxe.iron",
        "weaponmod:battleaxe.diamond",
        "weaponmod:battleaxe.gold",
        
        "weaponmod:warhammer.wood",
        "weaponmod:warhammer.stone",
        "weaponmod:warhammer.iron",
        "weaponmod:warhammer.diamond",
        "weaponmod:warhammer.gold",
        
        "weaponmod:katana.wood",
        "weaponmod:katana.stone",
        "weaponmod:katana.iron",
        "weaponmod:katana.diamond",
        "weaponmod:katana.gold",
        "",
        
        //Tinkers' Construct
        "TConstruct:longsword",
        "TConstruct:broadsword",
        "TConstruct:cleaver",
        "TConstruct:battleaxe",
        "TConstruct:rapier",
        "TConstruct:cutlass",
        "",
        
        //Thaumcraft
        "Thaumcraft:ItemSwordElemental",
        "Thaumcraft:ItemSwordThaumium",
        "Thaumcraft:ItemSwordVoid",
        "",
        
        //Zelda Sword Skills
        "zeldaswordskills:zss.sword_darknut",
        "zeldaswordskills:zss.sword_kokiri",
        "zeldaswordskills:zss.sword_ordon",
        "zeldaswordskills:zss.sword_giant",
        "zeldaswordskills:zss.sword_biggoron",
        "zeldaswordskills:zss.sword_master",
        "zeldaswordskills:zss.sword_tempered",
        "zeldaswordskills:zss.sword_golden",
        "zeldaswordskills:zss.sword_master_true",
        "",
        
        //More Swords Mod
        "MSM3:dawnStar",
        "MSM3:vampiric",
        "MSM3:gladiolus",
        "MSM3:draconic",
        "MSM3:ender",
        "MSM3:crystal",
        "MSM3:glacial",
        "MSM3:aether",
        "MSM3:wither",
        "MSM3:admin",
        "",
        
        //Mekanism Tools
        "MekanismTools:ObsidianSword",
        "MekanismTools:LapisLazuliSword",
        "MekanismTools:OsmiumSword",
        "MekanismTools:BronzeSword",
        "MekanismTools:GlowstoneSword",
        "MekanismTools:SteelSword",
        "",
        
        //Battlegear 2
        "battlegear2:waraxe.wood",
        "battlegear2:waraxe.stone",
        "battlegear2:waraxe.iron",
        "battlegear2:waraxe.diamond",
        "battlegear2:waraxe.gold",
        
        "battlegear2:mace.wood",
        "battlegear2:mace.stone",
        "battlegear2:mace.iron",
        "battlegear2:mace.diamond",
        "battlegear2:mace.gold",
        
        "battlegear2:spear.wood",
        "battlegear2:spear.stone",
        "battlegear2:spear.iron",
        "battlegear2:spear.diamond",
        "battlegear2:spear.gold",
        "",
        
        //Glass Shards
        "glass_shards:glass_sword",
        "",
        
        //Twilight Forest
        "TwilightForest:item.ironwoodSword",
        "TwilightForest:item.fierySword",
        "TwilightForest:item.steeleafSword",
        "TwilightForest:item.knightlySword",
        //"TwilightForest:item.giantSword",
        "TwilightForest:item.iceSword",
        "TwilightForest:item.glassSword",
        "",
        
        //Metallurgy
        "Metallurgy:copper.sword",
        "Metallurgy:bronze.sword",
        "Metallurgy:hepatizon.sword",
        "Metallurgy:damascus.steel.sword",
        "Metallurgy:angmallen.sword",
        "Metallurgy:steel.sword",
        "Metallurgy:eximite.sword",
        "Metallurgy:desichalkos.sword",
        "Metallurgy:prometheum.sword",
        "Metallurgy:deep.iron.sword",
        "Metallurgy:black.steel.sword",
        "Metallurgy:oureclase.sword",
        "Metallurgy:astral.silver.sword",
        "Metallurgy:carmot.sword",
        "Metallurgy:mithril.sword",
        "Metallurgy:quicksilver.sword",
        "Metallurgy:haderoth.sword",
        "Metallurgy:orichalcum.sword",
        "Metallurgy:celenegil.sword",
        "Metallurgy:adamantine.sword",
        "Metallurgy:atlarus.sword",
        "Metallurgy:tartarite.sword",
        "Metallurgy:ignatius.sword",
        "Metallurgy:shadow.iron.sword",
        "Metallurgy:midasium.sword",
        "Metallurgy:vyroxeres.sword",
        "Metallurgy:ceruclase.sword",
        "Metallurgy:kalendrite.sword",
        "Metallurgy:vulcanite.sword",
        "Metallurgy:sanguinite.sword",
        "Metallurgy:shadow.steel.sword",
        "Metallurgy:inolashite.sword",
        "Metallurgy:amordrine.sword",
        "Metallurgy:silver.sword",
        "Metallurgy:platinum.sword",
        "Metallurgy:brass.sword",
        "Metallurgy:electrum.sword",
        "",
        
        //MapleCrafted
        "maplecrafted:item.axe1",
        "maplecrafted:item.axe2",
        "maplecrafted:item.axe4",
        "maplecrafted:item.axe5",
        "maplecrafted:item.axe6",
        "maplecrafted:item.axe7",
        "maplecrafted:item.axe8",
        "maplecrafted:item.axe9",
        "maplecrafted:item.axe10",
        "maplecrafted:item.axe11",
        "maplecrafted:item.axe12",
        "maplecrafted:item.axe13",
        "maplecrafted:item.axe14",
        "maplecrafted:item.axe15",
        "maplecrafted:item.axe16",
        "maplecrafted:item.axe17",
        "maplecrafted:item.axe18",
        "maplecrafted:item.axe19",
        "maplecrafted:item.axe20",
        "maplecrafted:item.axe21",
        "maplecrafted:item.axe22",
        "maplecrafted:item.axe23",
        "maplecrafted:item.axe24",
        "maplecrafted:item.axe25",
        "maplecrafted:item.axe26",
        "maplecrafted:item.axe27",
        "maplecrafted:item.axe28",
        "maplecrafted:item.axe29",
        "maplecrafted:item.axe30",
        "maplecrafted:item.axe31",
        "maplecrafted:item.axe32",
        
        "maplecrafted:item.makuLUK",
        "maplecrafted:item.exiledKillic",
        "maplecrafted:item.makusumaLUK",
        "maplecrafted:item.duskRavensBeak",
        "maplecrafted:item.serpentsCoil",
        "maplecrafted:item.razor",
        "maplecrafted:item.fruitKnife",
        "maplecrafted:item.diamondDagger",
        "maplecrafted:item.nebulaDaggerLUK",
        "maplecrafted:item.makusumaSTR",
        "maplecrafted:item.makuSTR",
        "maplecrafted:item.luminousHeavenDagger",
        "maplecrafted:item.nebulaDaggerSTR",
        "maplecrafted:item.koreanFan",
        "maplecrafted:item.angelicBetrayal",
        "maplecrafted:item.cursayer",
        "maplecrafted:item.sai",
        "maplecrafted:item.coconutKnife",
        "maplecrafted:item.ravensBeak",
        "maplecrafted:item.dragonKanzir",
        "maplecrafted:item.timelessPescas",
        "maplecrafted:item.bazlud",
        "maplecrafted:item.ironDagger",
        "maplecrafted:item.cass",
        "maplecrafted:item.balrogsAngelicBetrayal",
        "maplecrafted:item.forkedDagger",
        "maplecrafted:item.goldenRiver",
        "maplecrafted:item.berylMapleLightsplitter",
        "maplecrafted:item.mapleDarkMate",
        "maplecrafted:item.fieldDagger",
        
        "maplecrafted:item.desperado1",
        "maplecrafted:item.desperado2",
        "maplecrafted:item.desperado3",
        "maplecrafted:item.desperado4",
        "maplecrafted:item.desperado5",
        "maplecrafted:item.desperado6",
        "maplecrafted:item.desperado7",
        "maplecrafted:item.desperado8",
        "maplecrafted:item.desperado9",
        "maplecrafted:item.desperado10",
        "maplecrafted:item.desperado11",
        "maplecrafted:item.desperado12",
        "maplecrafted:item.desperado13",
        "maplecrafted:item.desperado14",
        "maplecrafted:item.desperado15",
        "maplecrafted:item.desperado16",
        "maplecrafted:item.desperado17",
        "maplecrafted:item.desperado18",
        "maplecrafted:item.desperado19",
        "maplecrafted:item.desperado20",
        
        "maplecrafted:item.katana1",
        "maplecrafted:item.katana2",
        "maplecrafted:item.katana3",
        "maplecrafted:item.katana4",
        "maplecrafted:item.katana5",
        "maplecrafted:item.katana6",
        "maplecrafted:item.katana7",
        "maplecrafted:item.katana8",
        "maplecrafted:item.katana9",
        "maplecrafted:item.katana10",
        "maplecrafted:item.katana11",
        "maplecrafted:item.katana12",
        "maplecrafted:item.katana13",
        "maplecrafted:item.katana14",
        "maplecrafted:item.katana15",
        "maplecrafted:item.katana16",
        "maplecrafted:item.katana17",
        "maplecrafted:item.katana18",
        "maplecrafted:item.katana19",
        
        "maplecrafted:item.mace1",
        "maplecrafted:item.mace2",
        "maplecrafted:item.mace3",
        "maplecrafted:item.mace4",
        "maplecrafted:item.mace5",
        "maplecrafted:item.mace6",
        "maplecrafted:item.mace7",
        "maplecrafted:item.mace8",
        "maplecrafted:item.mace10",
        "maplecrafted:item.mace11",
        "maplecrafted:item.mace12",
        "maplecrafted:item.mace13",
        "maplecrafted:item.mace14",
        "maplecrafted:item.mace15",
        "maplecrafted:item.mace16",
        "maplecrafted:item.mace17",
        "maplecrafted:item.mace18",
        "maplecrafted:item.mace19",
        "maplecrafted:item.mace20",
        "maplecrafted:item.mace21",
        "maplecrafted:item.mace22",
        "maplecrafted:item.mace23",
        "maplecrafted:item.mace24",
        "maplecrafted:item.mace25",
        "maplecrafted:item.mace26",
        "maplecrafted:item.mace27",
        "maplecrafted:item.mace28",
        "maplecrafted:item.mace29",
        "maplecrafted:item.mace30",
        "maplecrafted:item.mace31",
        "maplecrafted:item.mace32",
        "maplecrafted:item.mace33",
        "maplecrafted:item.mace34",
        "maplecrafted:item.mace35",
        "maplecrafted:item.mace36",
        
        "maplecrafted:item.polearm1",
        "maplecrafted:item.polearm2",
        "maplecrafted:item.polearm3",
        "maplecrafted:item.polearm4",
        "maplecrafted:item.polearm5",
        "maplecrafted:item.polearm6",
        "maplecrafted:item.polearm7",
        "maplecrafted:item.polearm8",
        "maplecrafted:item.polearm9",
        "maplecrafted:item.polearm10",
        "maplecrafted:item.polearm11",
        "maplecrafted:item.polearm12",
        "maplecrafted:item.polearm13",
        "maplecrafted:item.polearm14",
        "maplecrafted:item.polearm15",
        "maplecrafted:item.polearm16",
        "maplecrafted:item.polearm17",
        "maplecrafted:item.polearm18",
        "maplecrafted:item.polearm19",
        "maplecrafted:item.polearm20",
        "maplecrafted:item.polearm21",
        "maplecrafted:item.polearm22",
        "maplecrafted:item.polearm24",
        "maplecrafted:item.polearm25",
        "maplecrafted:item.polearm26",
        "maplecrafted:item.polearm27",
        "maplecrafted:item.polearm28",
        "maplecrafted:item.polearm29",
        "maplecrafted:item.polearm30",
        "maplecrafted:item.polearm31",
        "maplecrafted:item.polearm32",
        "maplecrafted:item.polearm33",
        "maplecrafted:item.polearm34",
        "maplecrafted:item.polearm35",
        "maplecrafted:item.polearm36",
        "maplecrafted:item.polearm37",
        "maplecrafted:item.polearm38",
        "maplecrafted:item.polearm39",
        "maplecrafted:item.polearm40",
        "maplecrafted:item.polearm41",
        "maplecrafted:item.polearm42",
        "maplecrafted:item.polearm43",
        "maplecrafted:item.polearm44",
        
        "maplecrafted:item.spear1",
        "maplecrafted:item.spear2",
        "maplecrafted:item.spear3",
        "maplecrafted:item.spear4",
        "maplecrafted:item.spear5",
        "maplecrafted:item.spear6",
        "maplecrafted:item.spear7",
        "maplecrafted:item.spear8",
        "maplecrafted:item.spear9",
        "maplecrafted:item.spear10",
        "maplecrafted:item.spear11",
        "maplecrafted:item.spear12",
        "maplecrafted:item.spear13",
        "maplecrafted:item.spear14",
        "maplecrafted:item.spear15",
        "maplecrafted:item.spear16",
        "maplecrafted:item.spear17",
        "maplecrafted:item.spear18",
        "maplecrafted:item.spear19",
        "maplecrafted:item.spear20",
        "maplecrafted:item.spear21",
        "maplecrafted:item.spear22",
        "maplecrafted:item.spear23",
        "maplecrafted:item.spear24",
        "maplecrafted:item.spear25",
        "maplecrafted:item.spear26",
        "maplecrafted:item.spear27",
        "maplecrafted:item.spear28",
        "maplecrafted:item.spear29",
        "maplecrafted:item.spear30",
        "maplecrafted:item.spear31",
        "maplecrafted:item.spear32",
        "maplecrafted:item.spear33",
        "maplecrafted:item.spear34",
        "maplecrafted:item.spear35",
        "maplecrafted:item.spear36",
        
        "maplecrafted:item.sword1",
        "maplecrafted:item.sword2",
        "maplecrafted:item.sword3",
        "maplecrafted:item.sword4",
        "maplecrafted:item.sword5",
        "maplecrafted:item.sword6",
        "maplecrafted:item.sword7",
        "maplecrafted:item.sword8",
        "maplecrafted:item.sword9",
        "maplecrafted:item.sword10",
        "maplecrafted:item.sword11",
        "maplecrafted:item.sword12",
        "maplecrafted:item.sword13",
        "maplecrafted:item.sword14",
        "maplecrafted:item.sword15",
        "maplecrafted:item.sword16",
        "maplecrafted:item.sword17",
        "maplecrafted:item.sword18",
        "maplecrafted:item.sword19",
        "maplecrafted:item.sword20",
        "maplecrafted:item.sword21",
        "maplecrafted:item.sword22",
        "maplecrafted:item.sword23",
        "maplecrafted:item.sword24",
        "maplecrafted:item.sword25",
        "maplecrafted:item.sword26",
        "maplecrafted:item.sword27",
        "maplecrafted:item.sword28",
        "maplecrafted:item.sword29",
        "maplecrafted:item.sword30",
        "maplecrafted:item.sword31",
        "maplecrafted:item.sword32",
        "maplecrafted:item.sword33",
        "maplecrafted:item.sword34",
        "maplecrafted:item.sword35",
        "maplecrafted:item.sword36",
        "maplecrafted:item.sword37",
        "maplecrafted:item.sword38",
        "maplecrafted:item.sword39",
        "maplecrafted:item.sword40",
        "maplecrafted:item.sword41",
        "maplecrafted:item.sword42",
        "maplecrafted:item.sword43",
        "maplecrafted:item.sword44",
        "maplecrafted:item.sword45",
        "maplecrafted:item.sword46",
        "maplecrafted:item.sword47",
        "maplecrafted:item.sword48",
        "maplecrafted:item.sword49",
        "maplecrafted:item.sword50",
        "maplecrafted:item.sword51",
        "maplecrafted:item.sword52",
        "maplecrafted:item.sword53",
        "maplecrafted:item.sword54",
        "maplecrafted:item.sword55",
        "maplecrafted:item.sword57",
        "",
        
        "OreSpawn:OreSpawn_Bertha",
        "OreSpawn:OreSpawn_UltimateSword",
        "OreSpawn:OreSpawn_BattleAxe",
        "OreSpawn:OreSpawn_Royal",
        "OreSpawn:OreSpawn_Slice",
        "OreSpawn:OreSpawn_Hammy",
        "OreSpawn:OreSpawn_NightmareSword",
        "OreSpawn:OreSpawn_QueenBattleAxe",
        "OreSpawn:OreSpawn_Chainsaw",
        "OreSpawn:OreSpawn_EmeraldSword",
        "OreSpawn:OreSpawn_RoseSword",
        "OreSpawn:OreSpawn_ExperienceSword",
        "OreSpawn:OreSpawn_PoisonSword",
        "OreSpawn:OreSpawn_RatSword",
        "OreSpawn:OreSpawn_FairySword",
        "OreSpawn:OreSpawn_CrystalPinkSword",
        "OreSpawn:OreSpawn_TigersEyeSword",
        "OreSpawn:OreSpawn_CrystalStoneSword",
        "OreSpawn:OreSpawn_CrystalWoodSword",
        "OreSpawn:OreSpawn_RubySword",
        "OreSpawn:OreSpawn_AmethystSword",
        "OreSpawn:OreSpawn_UltimatePickaxe",
        "OreSpawn:OreSpawn_BigHammer"
    };
    
    public static final String[] overrideBowsDefault = {
        "minecraft:bow"
    };
    
    public static String[] overrideSwordsActive = {};
    public static String[] overrideBowsActive = {};
    
    private static void loadAddon(Class<? extends AbstractAddon> addonClass, String modId) {
        if (!Loader.isModLoaded(modId)) {
            return;
        }
        try {
            AbstractAddon addon = addonClass.getConstructor().newInstance();
            ModLogger.log(String.format("Loading %s Compatibility Addon", addon.getModName()));
            loadedAddons.add(addon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void preInit() {
        loadAddon(AddonBattlegear2.class, "battlegear2");
        loadAddon(AddonBuildCraft.class, "BuildCraft|Core");
        loadAddon(AddonAquaTweaks.class, "AquaTweaks");
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).preInit();
        }
    }
    
    public static void init() {
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).init();
        }
    }
    
    public static void postInit() {
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).postInit();
        }
    }
    
    public static void onWeaponRender(ItemRenderType type, EventState state) {
        for (int i = 0; i < loadedAddons.size(); i++) {
            loadedAddons.get(i).onWeaponRender(type, state);
        }
    }
    
    public static void initRenderers() {    
        overrideSwordRenders();
        overrideBowRenders();
    }
    
    private static void overrideSwordRenders() {
        for (int i = 0; i < overrideSwordsActive.length; i++) {
            String arrayItem = overrideSwordsActive[i];
            if (arrayItem.contains(":")) {
                String modId = arrayItem.substring(0, arrayItem.indexOf(":"));
                String itemId = arrayItem.substring(arrayItem.indexOf(":") + 1);
                if (Loader.isModLoaded(modId) | modId.equalsIgnoreCase("minecraft")) {
                    overrideItemRenderer(modId, itemId, RenderType.SWORD);
                }
            } else {
                if (!arrayItem.isEmpty()) {
                    ModLogger.log(Level.ERROR, String.format("Invalid sword override in config file: %s", arrayItem));
                }
            }
        }
    }
    
    private static void overrideBowRenders() {
        for (int i = 0; i < overrideBowsActive.length; i++) {
            String arrayItem = overrideBowsActive[i];
            if (arrayItem.contains(":")) {
                String modId = arrayItem.substring(0, arrayItem.indexOf(":"));
                String itemId = arrayItem.substring(arrayItem.indexOf(":") + 1);
                if (Loader.isModLoaded(modId) | modId.equalsIgnoreCase("minecraft")) {
                    overrideItemRenderer(modId, itemId, RenderType.BOW);
                }
            } else {
                if (!arrayItem.isEmpty()) {
                    ModLogger.log(Level.ERROR, String.format("Invalid bow override in config file: %s", arrayItem));
                }
            }
        }
    }
    
    private static void overrideItemRenderer(String modId, String itemName, RenderType renderType) {
        Item item = GameRegistry.findItem(modId, itemName);
        if (item != null) {
            ItemStack stack = new ItemStack(item);
            IItemRenderer renderer = getItemRenderer(stack);
            ModLogger.log("Overriding render on - " + modId + ":" + itemName);
            if (renderer != null) {
                ModLogger.log("Storing custom item renderer for: " + itemName);
                customItemRenderers.put(item, renderer);
            }
            switch (renderType) {
            case SWORD:
                MinecraftForgeClient.registerItemRenderer(item, new RenderItemSwordSkin());
                break;
            case BOW:
                MinecraftForgeClient.registerItemRenderer(item, new RenderItemBowSkin());
                break;
            }
            
        } else {
            ModLogger.log(Level.WARN, "Unable to override item renderer for: " + modId + " - " + itemName);
        }
    }
    
    private static IItemRenderer getItemRenderer(ItemStack stack) {
        try {
            IdentityHashMap<Item, IItemRenderer> customItemRenderers = null;
            customItemRenderers = ReflectionHelper.getPrivateValue(MinecraftForgeClient.class, null, "customItemRenderers");
            IItemRenderer renderer = customItemRenderers.get(stack.getItem());
            if (renderer != null) {
                return renderer;
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public static IItemRenderer getItemRenderer(ItemStack item, ItemRenderType type) {
        IItemRenderer renderer = customItemRenderers.get(item.getItem());
        if (renderer != null && renderer.handleRenderType(item, type)) {
            return renderer;
        }
        return null;
    }
    
    public static enum RenderType {
        SWORD,
        BOW;
    }
}
