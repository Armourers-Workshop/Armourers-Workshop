package moe.plushie.armourers_workshop.core.armature.thirdparty;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.armature.JointTransformModifier;

import java.util.Collection;

public class EpicFlightContext {

    public IPoseStack overridePostStack;
    public Collection<ISkinPartType> overrideParts;
    public JointTransformModifier overrideTransformModifier;
}
