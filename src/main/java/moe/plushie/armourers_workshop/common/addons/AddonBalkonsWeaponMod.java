package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonBalkonsWeaponMod extends ModAddon {
    
    public AddonBalkonsWeaponMod() {
        super("weaponmod", "Balkon's Weapon Mod");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "battleaxe.wood");
        addItemOverride(ItemOverrideType.SWORD, "battleaxe.stone");
        addItemOverride(ItemOverrideType.SWORD, "battleaxe.iron");
        addItemOverride(ItemOverrideType.SWORD, "battleaxe.diamond");
        addItemOverride(ItemOverrideType.SWORD, "battleaxe.gold");
        
        addItemOverride(ItemOverrideType.SWORD, "warhammer.wood");
        addItemOverride(ItemOverrideType.SWORD, "warhammer.stone");
        addItemOverride(ItemOverrideType.SWORD, "warhammer.iron");
        addItemOverride(ItemOverrideType.SWORD, "warhammer.diamond");
        addItemOverride(ItemOverrideType.SWORD, "warhammer.gold");
        
        addItemOverride(ItemOverrideType.SWORD, "katana.wood");
        addItemOverride(ItemOverrideType.SWORD, "katana.stone");
        addItemOverride(ItemOverrideType.SWORD, "katana.iron");
        addItemOverride(ItemOverrideType.SWORD, "katana.diamond");
        addItemOverride(ItemOverrideType.SWORD, "katana.gold");
    }
}
