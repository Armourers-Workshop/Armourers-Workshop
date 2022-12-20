package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.core.armature.ArmatureBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureManager;
import net.minecraft.resources.ResourceLocation;

public class EpicFlightArmatureManager extends ArmatureManager {

    @Override
    protected ArmatureBuilder createBuilder(ResourceLocation name) {
        return new EpicFightArmatureBuilder(name);
    }
}
