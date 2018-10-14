package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonBetterStorage extends ModAddon {

    public AddonBetterStorage() {
        super("betterstorage", "BetterStorage");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "cardboardSword");
    }
}
