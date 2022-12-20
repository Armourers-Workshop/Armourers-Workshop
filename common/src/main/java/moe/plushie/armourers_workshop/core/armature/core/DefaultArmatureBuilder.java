package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.core.armature.ArmatureBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;
import net.minecraft.resources.ResourceLocation;

public class DefaultArmatureBuilder extends ArmatureBuilder {

    public DefaultArmatureBuilder(ResourceLocation name) {
        super(name);
    }

    @Override
    public ArmatureModifier getTarget(IDataPackObject object) {
        if (object.type() == IDataPackObject.Type.STRING) {
            return new DefaultJointBinder(object.stringValue());
        }
        return new DefaultJointBinder(object.get("target").stringValue());
    }
}
