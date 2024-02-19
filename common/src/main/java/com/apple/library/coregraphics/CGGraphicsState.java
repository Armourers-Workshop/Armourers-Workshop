package com.apple.library.coregraphics;

import com.apple.library.impl.ObjectUtilsImpl;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

@SuppressWarnings("unused")
public interface CGGraphicsState {

    IPoseStack ctm();

    IBufferSource bufferSource();

    CGPoint mousePos();

    float partialTicks();

    void flush();

    default void save() {
        ctm().pushPose();
    }

    default void translate(float x, float y, float z) {
        if (x != 0 || y != 0 || z != 0) {
            ctm().translate(x, y, z);
        }
    }

    default void rotate(float x, float y, float z) {
        if (x != 0) {
            ctm().rotate(Vector3f.XP.rotationDegrees(x));
        }
        if (y != 0) {
            ctm().rotate(Vector3f.YP.rotationDegrees(y));
        }
        if (z != 0) {
            ctm().rotate(Vector3f.ZP.rotationDegrees(z));
        }
    }

    default void concatenate(CGAffineTransform transform) {
        IPoseStack ctm = ctm();
        ctm.multiply(ObjectUtilsImpl.convertToMatrix3x3(transform));
        ctm.multiply(ObjectUtilsImpl.convertToMatrix4x4(transform));
    }

    default void restore() {
        ctm().popPose();
    }
}
