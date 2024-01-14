package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import net.minecraft.resources.ResourceLocation;

public class EpicFlightArmatureTransformerManager extends ArmatureTransformerManager {

    @Override
    protected ArmatureTransformerBuilder createBuilder(ResourceLocation name) {
        return new EpicFightArmatureTransformerBuilder(name);
    }
}
