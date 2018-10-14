package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonThaumcraft extends ModAddon {

    public AddonThaumcraft() {
        super("Thaumcraft", "Thaumcraft");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "ItemSwordElemental");
        addItemOverride(ItemOverrideType.SWORD, "ItemSwordThaumium");
        addItemOverride(ItemOverrideType.SWORD, "ItemSwordVoid");
    }
}
