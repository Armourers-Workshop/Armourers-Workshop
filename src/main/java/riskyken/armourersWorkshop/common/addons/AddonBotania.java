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
        overrideItemRenderer("manasteelSword", RenderType.SWORD);
        overrideItemRenderer("terraSword", RenderType.SWORD);
        overrideItemRenderer("elementiumSword", RenderType.SWORD);
    }
    
    @Override
    public String getModName() {
        return "Botania";
    }
}
