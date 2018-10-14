package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonGlassShards extends ModAddon {

    public AddonGlassShards() {
        super("glass_shards", "Glass Shards");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "glass_sword");
    }
}
