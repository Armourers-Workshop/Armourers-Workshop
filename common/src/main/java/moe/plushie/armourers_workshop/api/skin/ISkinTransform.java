package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.math.IPoseStack;

public interface ISkinTransform {

    void pre(IPoseStack poseStack);

    void post(IPoseStack poseStack);

    default void apply(IPoseStack poseStack) {
        pre(poseStack);
        post(poseStack);
    }
}
