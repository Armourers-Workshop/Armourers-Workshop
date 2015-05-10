package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.common.addons.Addons.RenderType;
import riskyken.armourersWorkshop.utils.EventState;
import riskyken.armourersWorkshop.utils.ModLogger;


public class AddonTConstruct extends AbstractAddon {

    public AddonTConstruct() {
        ModLogger.log("Loading Tinkers' Construct Compatibility Addon");
    }
    
    @Override
    public void init() {
        addRenderClass("tconstruct.items.tools.Longsword", RenderType.SWORD);
        addRenderClass("tconstruct.items.tools.Broadsword", RenderType.SWORD);
        addRenderClass("tconstruct.items.tools.Cleaver", RenderType.SWORD);
        addRenderClass("tconstruct.items.tools.Battleaxe", RenderType.SWORD);
        addRenderClass("tconstruct.items.tools.Rapier", RenderType.SWORD);
        addRenderClass("tconstruct.items.tools.Cutlass", RenderType.SWORD);
        addRenderClass("tconstruct.items.tools.Shortbow", RenderType.BOW);
    }

    @Override
    public String getModName() {
        return "TConstruct";
    }

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
    }
}
