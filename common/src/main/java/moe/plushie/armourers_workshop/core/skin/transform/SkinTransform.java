package moe.plushie.armourers_workshop.core.skin.transform;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class SkinTransform {

    public static final SkinTransform IDENTIFIER = new SkinTransform();

    public static SkinFixedTransform createTranslateTransform(Vector3f offset) {
        SkinFixedTransform transform = new SkinFixedTransform();
        if (!offset.equals(Vector3f.ZERO)) {
            transform.translate = offset;
        }
        return  transform;
    }

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

    public static class Mul extends SkinTransform {

        private final SkinTransform left;
        private final SkinTransform right;


        public Mul(SkinTransform left, SkinTransform right) {
            this.left = left;
            this.right = right;
        }

        public void setup(float partialTicks, @Nullable Entity entity) {
            left.setup(partialTicks, entity);
            right.setup(partialTicks, entity);
        }

        public void pre(IPoseStack poseStack) {
            left.pre(poseStack);
            right.pre(poseStack);
        }

        public void post(IPoseStack poseStack) {
            left.post(poseStack);
            right.post(poseStack);
        }
    }
}
