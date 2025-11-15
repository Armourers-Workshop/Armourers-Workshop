package com.apple.library.coregraphics;

import com.apple.library.impl.ObjectUtilsImpl;
import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

public class CGGraphicsState {

    protected final IPoseStack ctm;

    private CGBlendMode blendMode = CGBlendMode.NORMAL;
    private UIColor blendColor = UIColor.WHITE;

    private Snapshot lastSnapshot;

    public CGGraphicsState() {
        this(new OpenPoseStack());
    }

    public CGGraphicsState(IPoseStack ctm) {
        this.ctm = ctm;
    }

    public void save() {
        ctm.pushPose();
        setDirty();
    }

    public void translate(float x, float y, float z) {
        if (x != 0 || y != 0 || z != 0) {
            ctm.translate(x, y, z);
            setDirty();
        }
    }

    public void scale(float x, float y, float z) {
        if (x != 1 || y != 1 || z != 1) {
            ctm.scale(x, y, z);
            setDirty();
        }
    }

    public void rotate(float x, float y, float z) {
        if (x != 0) {
            ctm.rotate(OpenVector3f.XP.rotationDegrees(x));
            setDirty();
        }
        if (y != 0) {
            ctm.rotate(OpenVector3f.YP.rotationDegrees(y));
            setDirty();
        }
        if (z != 0) {
            ctm.rotate(OpenVector3f.ZP.rotationDegrees(z));
            setDirty();
        }
    }

    public void concatenate(CGAffineTransform transform) {
        ctm.multiply(ObjectUtilsImpl.convertToMatrix3x3(transform));
        ctm.multiply(ObjectUtilsImpl.convertToMatrix4x4(transform));
        setDirty();
    }

    public void restore() {
        ctm.popPose();
        setDirty();
    }

    public void apply(Snapshot snapshot) {
        ctm.last().set(snapshot.ctm);
        blendMode = snapshot.blendMode;
        blendColor = snapshot.blendColor;
        setDirty();
    }

    public Snapshot snapshot() {
        if (lastSnapshot == null) {
            lastSnapshot = new Snapshot(ctm.last(), blendMode, blendColor);
        }
        return lastSnapshot;
    }

    public IPoseStack ctm() {
        return ctm;
    }

    public void setBlendMode(CGBlendMode mode) {
        this.blendMode = mode;
        setDirty();
    }

    public CGBlendMode blendMode() {
        return blendMode;
    }

    public void setBlendColor(UIColor color) {
        this.blendColor = color;
        setDirty();
    }

    public UIColor blendColor() {
        return blendColor;
    }

    private void setDirty() {
        lastSnapshot = null;
    }

    public static class Snapshot {

        private final OpenPoseStack.Pose ctm = new OpenPoseStack.Pose();
        private final CGBlendMode blendMode;
        private final UIColor blendColor;

        public Snapshot(IPoseStack.Pose ctm, CGBlendMode blendMode, UIColor blendColor) {
            this.ctm.set(ctm);
            this.blendMode = blendMode;
            this.blendColor = blendColor;
        }

        public CGBlendMode blendMode() {
            return blendMode;
        }

        public UIColor blendColor() {
            return blendColor;
        }

        public OpenPoseStack.Pose ctm() {
            return ctm;
        }
    }
}
