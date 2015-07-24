package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.common.addons.Addons.RenderType;
import riskyken.armourersWorkshop.utils.EventState;


public class AddonTConstruct extends AbstractAddon {
    
    @Override
    public void preInit() {
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
    public void postInit() {
    }
    
    @Override
    public String getModId() {
        return "TConstruct";
    }

    @Override
    public String getModName() {
        return "Tinkers' Construct";
    }

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
    }
}
