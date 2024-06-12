package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;

public class DefaultArmatureTransformerManager extends ArmatureTransformerManager {

    @Override
    protected ArmatureTransformerBuilder createBuilder(IResourceLocation name) {
        return new DefaultArmatureTransformerBuilder(name);
    }
}
