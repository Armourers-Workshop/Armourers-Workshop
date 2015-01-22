package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.utils.EventState;
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

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
    }
}
