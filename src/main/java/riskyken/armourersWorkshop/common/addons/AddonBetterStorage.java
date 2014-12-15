package riskyken.armourersWorkshop.common.addons;

import riskyken.armourersWorkshop.utils.ModLogger;

public class AddonBetterStorage extends AbstractAddon {

    public AddonBetterStorage() {
        ModLogger.log("Loading Better Storage Compatibility Addon");
    }
    
    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
        overrideItemRenderer("cardboardSword", RenderType.SWORD);
    }

    @Override
    public String getModName() {
        return "betterstorage";
    }
}
