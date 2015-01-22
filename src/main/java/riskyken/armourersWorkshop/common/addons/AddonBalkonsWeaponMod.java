package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.utils.EventState;
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
        overrideItemRenderer("battleaxe.wood", RenderType.SWORD);
        overrideItemRenderer("battleaxe.stone", RenderType.SWORD);
        overrideItemRenderer("battleaxe.iron", RenderType.SWORD);
        overrideItemRenderer("battleaxe.diamond", RenderType.SWORD);
        overrideItemRenderer("battleaxe.gold", RenderType.SWORD);
        
        overrideItemRenderer("warhammer.wood", RenderType.SWORD);
        overrideItemRenderer("warhammer.stone", RenderType.SWORD);
        overrideItemRenderer("warhammer.iron", RenderType.SWORD);
        overrideItemRenderer("warhammer.diamond", RenderType.SWORD);
        overrideItemRenderer("warhammer.gold", RenderType.SWORD);
        
        overrideItemRenderer("katana.wood", RenderType.SWORD);
        overrideItemRenderer("katana.stone", RenderType.SWORD);
        overrideItemRenderer("katana.iron", RenderType.SWORD);
        overrideItemRenderer("katana.diamond", RenderType.SWORD);
        overrideItemRenderer("katana.gold", RenderType.SWORD);
    }

    @Override
    public String getModName() {
        return "weaponmod";
    }

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
    }
}
