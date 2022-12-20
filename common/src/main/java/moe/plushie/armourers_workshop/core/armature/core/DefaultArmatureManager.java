package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.core.armature.ArmatureBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureManager;
import net.minecraft.resources.ResourceLocation;

public class DefaultArmatureManager extends ArmatureManager {

    @Override
    protected ArmatureBuilder createBuilder(ResourceLocation name) {
        return new DefaultArmatureBuilder(name);
    }
}
