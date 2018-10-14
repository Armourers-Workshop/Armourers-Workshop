package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonOreSpawn extends ModAddon {
    
    public AddonOreSpawn() {
        super("OreSpawn", "OreSpawn");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_Bertha");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_UltimateSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_BattleAxe");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_Royal");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_Slice");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_Hammy");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_NightmareSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_QueenBattleAxe");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_Chainsaw");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_EmeraldSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_RoseSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_ExperienceSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_PoisonSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_RatSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_FairySword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_CrystalPinkSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_TigersEyeSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_CrystalStoneSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_CrystalWoodSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_RubySword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_AmethystSword");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_UltimatePickaxe");
        addItemOverride(ItemOverrideType.SWORD, "OreSpawn_BigHammer");
    }
}
