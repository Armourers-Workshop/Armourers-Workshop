package riskyken.armourersWorkshop.common.addons;

import riskyken.armourersWorkshop.utils.ModLogger;

public class AddonThaumcraft extends AbstractAddon {

    public AddonThaumcraft() {
        ModLogger.log("Loading Thaumcraft Compatibility Addon");
    }
    
    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
        overrideItemRenderer("ItemSwordElemental", RenderType.SWORD);
        overrideItemRenderer("ItemSwordThaumium", RenderType.SWORD);
        overrideItemRenderer("ItemSwordVoid", RenderType.SWORD);
    }

    @Override
    public String getModName() {
        return "Thaumcraft";
    }
}
