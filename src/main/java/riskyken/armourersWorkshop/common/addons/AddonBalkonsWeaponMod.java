package riskyken.armourersWorkshop.common.addons;

import riskyken.armourersWorkshop.utils.ModLogger;

public class AddonBalkonsWeaponMod extends AbstractAddon {

    public AddonBalkonsWeaponMod() {
        ModLogger.log("Loading Balkon's Weapon Mod Compatibility Addon");
    }
    
    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
        overrideItemRenderer("battleaxe.wood");
        overrideItemRenderer("battleaxe.stone");
        overrideItemRenderer("battleaxe.iron");
        overrideItemRenderer("battleaxe.diamond");
        overrideItemRenderer("battleaxe.gold");
        
        overrideItemRenderer("warhammer.wood");
        overrideItemRenderer("warhammer.stone");
        overrideItemRenderer("warhammer.iron");
        overrideItemRenderer("warhammer.diamond");
        overrideItemRenderer("warhammer.gold");
        
        overrideItemRenderer("katana.wood");
        overrideItemRenderer("katana.stone");
        overrideItemRenderer("katana.iron");
        overrideItemRenderer("katana.diamond");
        overrideItemRenderer("katana.gold");
    }

    @Override
    public String getModName() {
        return "weaponmod";
    }
}
