package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.utils.EventState;
import riskyken.armourersWorkshop.utils.ModLogger;

public class AddonMekanismTools extends AbstractAddon {

    public AddonMekanismTools() {
        ModLogger.log("Loading Mekanism Tools Compatibility Addon");
    }
    
    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
        overrideItemRenderer("ObsidianSword", RenderType.SWORD);
        overrideItemRenderer("LapisLazuliSword", RenderType.SWORD);
        overrideItemRenderer("OsmiumSword", RenderType.SWORD);
        overrideItemRenderer("BronzeSword", RenderType.SWORD);
        overrideItemRenderer("GlowstoneSword", RenderType.SWORD);
        overrideItemRenderer("SteelSword", RenderType.SWORD);
    }

    @Override
    public String getModName() {
        return "MekanismTools";
    }

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
    }
}
