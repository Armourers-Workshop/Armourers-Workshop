package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.utils.ModLogger;

public class AddonTails extends ModAddon {

    public AddonTails() {
        super("tails", "Tails");
    }
    
    @Override
    public void init() {
        ModLogger.log("Tails detected! - Sand praising module active.");
    }
}
