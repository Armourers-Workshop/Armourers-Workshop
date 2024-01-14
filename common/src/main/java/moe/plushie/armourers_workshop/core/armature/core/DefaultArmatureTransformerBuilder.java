package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import net.minecraft.resources.ResourceLocation;

public class DefaultArmatureTransformerBuilder extends ArmatureTransformerBuilder {

    public DefaultArmatureTransformerBuilder(ResourceLocation name) {
        super(name);
    }

    @Override
    protected JointModifier buildJointTarget(String name) {
        return new DefaultJointBinder(name);
    }
}
