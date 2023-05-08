package moe.plushie.armourers_workshop.api.math;

import com.mojang.blaze3d.vertex.PoseStack;

public interface ITransformf {

    ITransformf NONE = poseStack -> {};

    void apply(PoseStack poseStack);
}
