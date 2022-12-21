package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.core.armature.ArmatureBuilder;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;

public class DefaultArmatureBuilder extends ArmatureBuilder {

    public DefaultArmatureBuilder(ResourceLocation name) {
        super(name);
    }

    @Override
    public Collection<ArmatureModifier> getTargets(IDataPackObject object) {
        switch (object.type()) {
            case DICTIONARY: {
                return getTargets(object.get("target"));
            }
            case STRING: {
                String value = object.stringValue();
                if (!value.isEmpty()) {
                    return Collections.singleton(new DefaultJointBinder(value));
                }
                return Collections.emptyList();
            }
            default: {
                return Collections.emptyList();
            }
        }
    }
}
