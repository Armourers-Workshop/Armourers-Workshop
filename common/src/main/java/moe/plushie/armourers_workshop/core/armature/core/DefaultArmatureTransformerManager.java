package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import net.minecraft.resources.ResourceLocation;

public class DefaultArmatureTransformerManager extends ArmatureTransformerManager {

    @Override
    protected ArmatureTransformerBuilder createBuilder(ResourceLocation name) {
        return new DefaultArmatureTransformerBuilder(name);
    }
}
