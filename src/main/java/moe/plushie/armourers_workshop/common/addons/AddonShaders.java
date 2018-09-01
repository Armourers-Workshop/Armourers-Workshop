package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.ArmourersWorkshop;

public class AddonShaders extends ModAddon {

    public AddonShaders() {
        super(null, "Shaders Mod");
    }

    @Override
    protected boolean setIsModLoaded() {
        if (!ArmourersWorkshop.isDedicated()) {
            try {
                Class.forName("shadersmodcore.client.Shaders");
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }
}
