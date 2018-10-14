package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonTinkersConstruct extends ModAddon {

    public AddonTinkersConstruct() {
        super("TConstruct", "Tinkers' Construct");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "longsword");
        addItemOverride(ItemOverrideType.SWORD, "broadsword");
        addItemOverride(ItemOverrideType.SWORD, "cleaver");
        addItemOverride(ItemOverrideType.SWORD, "battleaxe");
        addItemOverride(ItemOverrideType.SWORD, "rapier");
        addItemOverride(ItemOverrideType.SWORD, "cutlass");
    }
}
