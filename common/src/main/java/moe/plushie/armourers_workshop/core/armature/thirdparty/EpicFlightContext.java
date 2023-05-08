package moe.plushie.armourers_workshop.core.armature.thirdparty;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.armature.JointTransformModifier;

import java.util.Collection;

public class EpicFlightContext {

    public boolean isLimitLimbs = true;
    public PoseStack overridePostStack;
    public Collection<ISkinPartType> overrideParts;
    public JointTransformModifier overrideTransformModifier;
}
