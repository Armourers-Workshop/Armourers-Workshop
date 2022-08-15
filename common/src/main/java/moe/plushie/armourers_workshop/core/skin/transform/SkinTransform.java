package moe.plushie.armourers_workshop.core.skin.transform;

import moe.plushie.armourers_workshop.utils.ext.OpenPoseStack;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class SkinTransform {

    public static final SkinTransform IDENTIFIER = new SkinTransform();


    public void setup(float partialTicks, @Nullable Entity entity) {
    }

    public void apply(OpenPoseStack poseStack) {
        pre(poseStack);
        post(poseStack);
    }

    public void pre(OpenPoseStack poseStack) {
    }

    public void post(OpenPoseStack poseStack) {
    }
}
