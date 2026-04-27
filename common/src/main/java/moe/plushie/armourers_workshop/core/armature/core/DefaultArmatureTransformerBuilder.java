package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class DefaultArmatureTransformerBuilder extends ArmatureTransformerBuilder {

    public DefaultArmatureTransformerBuilder(OpenResourceKey name) {
        super(name);
    }

    @Override
    protected JointModifier buildJointTarget(String name, IODataObject parameters) {
        return new DefaultJointBinder(name, parameters);
    }
}
