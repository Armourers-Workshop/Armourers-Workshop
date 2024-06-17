package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class AnimationTransform implements ISkinTransform {

    private int flags = 0x00;

    private final Vector3f translate = new Vector3f();
    private final Vector3f rotation = new Vector3f();
    private final Vector3f scale = new Vector3f(1, 1, 1);

    private IVector3f pivot = Vector3f.ZERO;

    public AnimationTransform(BakedSkinPart part) {
        var transform = part.getPart().getTransform();
        if (transform instanceof ITransformf transform1) {
            // the rotation animation requires pivot, so we need to inherit pivot from the part transform.
            pivot = transform1.getPivot();
        }
    }

    @Override
    public void apply(IPoseStack poseStack) {
        // fast check.
        if (flags == 0x00) {
            return;
        }
        if ((flags & 0x10) != 0) {
            poseStack.translate(translate.getX(), translate.getY(), translate.getZ());
        }
        if ((flags & 0x20) != 0) {
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(pivot.getX(), pivot.getY(), pivot.getZ());
            }
            poseStack.rotate(Vector3f.ZP.rotationDegrees(rotation.getZ()));
            poseStack.rotate(Vector3f.YP.rotationDegrees(rotation.getY()));
            poseStack.rotate(Vector3f.XP.rotationDegrees(rotation.getX()));
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
        if ((flags & 0x40) != 0) {
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
        }
    }

    public void translate(float x, float y, float z) {
        translate.set(x, y, z);
        flags |= 0x10;
    }

    public void rotate(float x, float y, float z) {
        rotation.set(x, y, z);
        flags |= 0x20;
    }

    public void scale(float x, float y, float z) {
        scale.set(x, y, z);
        flags |= 0x40;
    }

    public void reset() {
        flags = 0x00;
    }
}

