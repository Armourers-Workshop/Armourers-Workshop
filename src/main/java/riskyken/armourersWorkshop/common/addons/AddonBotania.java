package riskyken.armourersWorkshop.common.addons;

import riskyken.armourersWorkshop.utils.ModLogger;

public class AddonBotania extends AbstractAddon {

    public AddonBotania() {
        ModLogger.log("Loading Botania Compatibility Addon");
    }
    
    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
        overrideItemRenderer("manasteelSword");
        overrideItemRenderer("terraSword");
        overrideItemRenderer("elementiumSword");
    }
    
    @Override
    public String getModName() {
        return "Botania";
    }
}
