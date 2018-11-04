package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.utils.ModLogger;

public class AddonIntegratedCircuits extends ModAddon {

    public AddonIntegratedCircuits() {
        super("integratedcircuits", "Integrated Circuits");
    }

    @Override
    public void init() {
        ModLogger.log("Integrated Circuits detected! - Applying cosplay to mannequins.");
    }
}
