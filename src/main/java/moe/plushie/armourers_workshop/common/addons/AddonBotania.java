package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonBotania extends ModAddon {

    public AddonBotania() {
        super("Botania", "Botania");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "manasteelSword");
        addItemOverride(ItemOverrideType.SWORD, "elementiumSword");
        addItemOverride(ItemOverrideType.SWORD, "terraSword");
        addItemOverride(ItemOverrideType.SWORD, "starSword");
        
        addItemOverride(ItemOverrideType.PICKAXE, "manasteelPick");
        addItemOverride(ItemOverrideType.PICKAXE, "elementiumPick");
        addItemOverride(ItemOverrideType.PICKAXE, "terraPick");
        
        addItemOverride(ItemOverrideType.AXE, "manasteelAxe");
        addItemOverride(ItemOverrideType.AXE, "elementiumAxe");
        addItemOverride(ItemOverrideType.AXE, "terraAxe");
        
        addItemOverride(ItemOverrideType.SHOVEL, "manasteelShovel");
        addItemOverride(ItemOverrideType.SHOVEL, "elementiumShovel");
    }
}
