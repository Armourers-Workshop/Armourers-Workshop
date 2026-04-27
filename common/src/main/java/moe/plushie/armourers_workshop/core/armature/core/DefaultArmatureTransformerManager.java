package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class DefaultArmatureTransformerManager extends ArmatureTransformerManager {

    @Override
    protected ArmatureTransformerBuilder createBuilder(OpenResourceKey name) {
        return new DefaultArmatureTransformerBuilder(name);
    }
}
