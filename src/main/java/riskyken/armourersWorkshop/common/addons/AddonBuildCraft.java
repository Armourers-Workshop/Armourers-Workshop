package riskyken.armourersWorkshop.common.addons;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import riskyken.armourersWorkshop.utils.ModLogger;

public class AddonBuildCraft extends ModAddon {
    
    public AddonBuildCraft() {
        super("BuildCraft|Core", "BuildCraft");
    }
    
    public boolean isSkinCompatibleVersion() {
        if (isModLoaded()) {
            ModContainer mc = Loader.instance().getIndexedModList().get(getModId());
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
