package moe.plushie.armourers_workshop.core.skin.transform;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class SkinTransform {

    public static final SkinTransform IDENTIFIER = new SkinTransform();


    public void setup(float partialTicks, @Nullable Entity entity) {
    }

    public void apply(IPoseStack poseStack) {
        pre(poseStack);
        post(poseStack);
    }

    public void pre(IPoseStack poseStack) {
    }

    public void post(IPoseStack poseStack) {
    }
}
