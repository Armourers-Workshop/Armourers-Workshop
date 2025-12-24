package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;

public interface IGraphicsContext {

    /**
     * Draw a graphics element.
     */
    void draw(IGraphicsElement element);

    /**
     * Return the current graphics state's transformation matrix.
     */
    IPoseStack ctm();

    /**
     * Push a copy of the current graphics state onto the graphics state stack.
     * Note that the path is not considered part of the graphics state, and is not saved.
     */
    default void saveGraphicsState() {
        ctm().pushPose();
    }

    /**
     * Restore the current graphics state from the one on the top of the graphics state stack,
     * popping the graphics state stack in the process.
     */
    default void restoreGraphicsState() {
        ctm().popPose();
    }

    /**
     * Translate the current graphics state's transformation matrix (the CTM) by `(tx, ty, tz)`.
     */
    default void translateCTM(float x, float y, float z) {
        ctm().translate(x, y, z);
    }

    /**
     * Translate the current graphics state's transformation matrix (the CTM) by `(tx, ty, tz)`.
     */
    default void translateCTM(IVector3f translation) {
        translateCTM(translation.x(), translation.y(), translation.z());
    }

    /**
     * Scale the current graphics state's transformation matrix (the CTM) by `(sx, sy, sz)`.
     */
    default void scaleCTM(float x, float y, float z) {
        ctm().scale(x, y, z);
    }

    /**
     * Scale the current graphics state's transformation matrix (the CTM) by `(sx, sy, sz)`.
     */
    default void scaleCTM(IVector3f scalar) {
        scaleCTM(scalar.x(), scalar.y(), scalar.z());
    }

    /**
     * Rotate the current graphics state's transformation matrix (the CTM) with the `quaternion`.
     */
    default void rotateCTM(IQuaternionf quaternion) {
        ctm().rotate(quaternion);
    }

    /**
     * Concatenate the current graphics state's transformation matrix (the CTM) with the `pose`.
     */
    default void concatenateCTM(IPoseStack.Pose pose) {
        ctm().multiply(pose.pose());
        ctm().multiply(pose.normal());
    }
}
