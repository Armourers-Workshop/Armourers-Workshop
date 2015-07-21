package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.utils.EventState;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class AddonBuildCraft extends AbstractAddon {
    
    private static final String MOD_ID = "BuildCraft|Core";
    
    public AddonBuildCraft() {
        ModLogger.log("Loading BuildCraft Compatibility Addon");
    }
    
    @Override
    public void init() {
    }

    @Override
    public String getModName() {
        return MOD_ID;
    }

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
    }
    
    public static boolean isSkinCompatibleVersion() {
        if (Loader.isModLoaded(MOD_ID)) {
            ModContainer mc = Loader.instance().getIndexedModList().get(MOD_ID);
            if (mc != null) {
                String version = mc.getVersion();
                String[] versionSplit = version.split("\\.");
                try {
                    int majorVersion = Integer.parseInt(versionSplit[0]);
                    if (majorVersion > 6) {
                        ModLogger.log("BuildCraft robot skin support active.");
                        return true;
                    } else {
                        ModLogger.log("BuildCraft is out of date. Unable to active robot skin support.");
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }
}
