package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.utils.EventState;
import riskyken.armourersWorkshop.utils.ModLogger;

public class AddonMoreSwordsMod extends AbstractAddon {
    
    public AddonMoreSwordsMod() {
        ModLogger.log("Loading More Swords Mod Compatibility Addon");
    }

    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
        overrideItemRenderer("dawnStar", RenderType.SWORD);
        overrideItemRenderer("vampiric", RenderType.SWORD);
        overrideItemRenderer("gladiolus", RenderType.SWORD);
        overrideItemRenderer("draconic", RenderType.SWORD);
        overrideItemRenderer("ender", RenderType.SWORD);
        overrideItemRenderer("crystal", RenderType.SWORD);
        overrideItemRenderer("glacial", RenderType.SWORD);
        overrideItemRenderer("aether", RenderType.SWORD);
        overrideItemRenderer("wither", RenderType.SWORD);
        overrideItemRenderer("admin", RenderType.SWORD);
    }

    @Override
    public String getModName() {
        return "MSM3";
    }

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
    }
}
