package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonTwilightForest extends ModAddon {

    public AddonTwilightForest() {
        super("TwilightForest", "Twilight Forest");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "item.ironwoodSword");
        addItemOverride(ItemOverrideType.SWORD, "item.fierySword");
        addItemOverride(ItemOverrideType.SWORD, "item.steeleafSword");
        addItemOverride(ItemOverrideType.SWORD, "item.knightlySword");
        //addItemOverride(ItemOverrideType.SWORD, "item.giantSword");
        addItemOverride(ItemOverrideType.SWORD, "item.iceSword");
        addItemOverride(ItemOverrideType.SWORD, "item.glassSword");
    }
}
