package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.utils.EventState;
import riskyken.armourersWorkshop.utils.ModLogger;

public class AddonZeldaSwordSkills extends AbstractAddon {

    public AddonZeldaSwordSkills() {
        ModLogger.log("Loading Zelda Sword Skills Compatibility Addon");
    }
    
    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
        overrideItemRenderer("zss.sword_darknut", RenderType.SWORD);
        overrideItemRenderer("zss.sword_kokiri", RenderType.SWORD);
        overrideItemRenderer("zss.sword_ordon", RenderType.SWORD);
        overrideItemRenderer("zss.sword_giant", RenderType.SWORD);
        overrideItemRenderer("zss.sword_biggoron", RenderType.SWORD);
        overrideItemRenderer("zss.sword_master", RenderType.SWORD);
        overrideItemRenderer("zss.sword_tempered", RenderType.SWORD);
        overrideItemRenderer("zss.sword_golden", RenderType.SWORD);
        overrideItemRenderer("zss.sword_master_true", RenderType.SWORD);
    }

    @Override
    public String getModName() {
        return "zeldaswordskills";
    }

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
    }
}
