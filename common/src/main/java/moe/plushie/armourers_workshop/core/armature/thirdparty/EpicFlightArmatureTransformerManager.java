package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class EpicFlightArmatureTransformerManager extends ArmatureTransformerManager {

    @Override
    protected ArmatureTransformerBuilder createBuilder(OpenResourceKey name) {
        return new EpicFightArmatureTransformerBuilder(name);
    }
}
