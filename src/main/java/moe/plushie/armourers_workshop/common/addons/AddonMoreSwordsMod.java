package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonMoreSwordsMod extends ModAddon {

    public AddonMoreSwordsMod() {
        super("MSM3", "More Swords Mod");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "dawnStar");
        addItemOverride(ItemOverrideType.SWORD, "vampiric");
        addItemOverride(ItemOverrideType.SWORD, "gladiolus");
        addItemOverride(ItemOverrideType.SWORD, "draconic");
        addItemOverride(ItemOverrideType.SWORD, "ender");
        addItemOverride(ItemOverrideType.SWORD, "crystal");
        addItemOverride(ItemOverrideType.SWORD, "glacial");
        addItemOverride(ItemOverrideType.SWORD, "aether");
        addItemOverride(ItemOverrideType.SWORD, "wither");
        addItemOverride(ItemOverrideType.SWORD, "admin");
    }
}
