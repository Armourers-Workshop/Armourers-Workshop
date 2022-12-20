package moe.plushie.armourers_workshop.api.math;

public interface ITransformf {

    ITransformf NONE = poseStack -> {};

    void apply(IPoseStack poseStack);
}
