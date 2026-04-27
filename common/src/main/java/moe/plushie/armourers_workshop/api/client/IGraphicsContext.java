package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.api.core.math.IVector3d;
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
    default void translateCTM(double x, double y, double z) {
        ctm().translate(x, y, z);
    }

    /**
     * Translate the current graphics state's transformation matrix (the CTM) by `(tx, ty, tz)`.
     */
    default void translateCTM(IVector3f translation) {
        ctm().translate(translation);
    }

    /**
     * Translate the current graphics state's transformation matrix (the CTM) by `(tx, ty, tz)`.
     */
    default void translateCTM(IVector3d translation) {
        ctm().translate(translation);
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
    default void scaleCTM(double x, double y, double z) {
        ctm().scale(x, y, z);
    }

    /**
     * Scale the current graphics state's transformation matrix (the CTM) by `(sx, sy, sz)`.
     */
    default void scaleCTM(IVector3f scalar) {
        ctm().scale(scalar);
    }

    /**
     * Scale the current graphics state's transformation matrix (the CTM) by `(sx, sy, sz)`.
     */
    default void scaleCTM(IVector3d scalar) {
        ctm().scale(scalar);
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
