package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.utils.Collections;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AnimatedTransform implements ITransform {

    private final OpenVector3f pivot;
    private final OpenVector3f afterTranslate;

    private final OpenTransform3f parent;

    private final List<AnimatedOutputPoint> pendingPoints = new ArrayList<>();

    private final List<AnimatedPoint> points = new ArrayList<>();
    private final List<AnimatedPoint> defaultPoints = new ArrayList<>();
    private final List<AnimatedPoint> mixedPoints = new ArrayList<>();

    private final OpenVector3f lastPivot = new OpenVector3f(0, 0, 0);
    private final OpenVector3f lastTranslate = new OpenVector3f(0, 0, 0);
    private final OpenVector3f lastRotation = new OpenVector3f(0, 0, 0);
    private final OpenVector3f lastScale = new OpenVector3f(1, 1, 1);

    protected AnimatedOutputPoint controller;
    protected AnimatedPoint snapshot;
    protected int dirty = 0;

    public AnimatedTransform(OpenTransform3f parent) {
        this.parent = parent;
        this.pivot = parent.pivot();
        this.afterTranslate = parent.afterTranslate();
    }

    @Nullable
    public static AnimatedTransform of(SkinPartTransform partTransform) {
        // when animation transform already been created, we just use it directly.
        for (var childTransform : partTransform.children()) {
            if (childTransform instanceof AnimatedTransform animatedTransform) {
                return animatedTransform;
            }
        }
        // if part have a non-standard transform (preview mode),
        // we wil think this part can't be support animation.
        if (!(partTransform.parent() instanceof OpenTransform3f parent)) {
            return null;
        }
        // we will replace the standard transform to animated transform.
        var animatedTransform = new AnimatedTransform(parent);
        partTransform.replaceChild(parent, animatedTransform);
        return animatedTransform;
    }

    public void link(AnimatedOutputPoint point) {
        // add point and sort it.
        this.pendingPoints.add(point);
        this.pendingPoints.sort(Comparator.comparingInt(it -> it.mode().priority()));
        // rebuild linked values.
        this.points.clear();
        this.defaultPoints.clear();
        this.mixedPoints.clear();
        this.points.addAll(pendingPoints);
        this.defaultPoints.addAll(Collections.filter(pendingPoints, it -> !it.mode().isMixMode()));
        this.mixedPoints.addAll(Collections.filter(pendingPoints, it -> it.mode().isMixMode()));
    }

    @Override
    public void apply(IPoseStack poseStack) {
        // the translation have changes?
        var translate = translation();
        if (translate != OpenVector3f.ZERO) {
            poseStack.translate(translate.x(), translate.y(), translate.z());
        }
        // the rotation have changes?
        var pivot = pivot();
        var rotation = rotation();
        if (rotation != OpenVector3f.ZERO) {
            if (pivot != OpenVector3f.ZERO) {
                poseStack.translate(pivot.x(), pivot.y(), pivot.z());
            }
            poseStack.rotate(OpenQuaternionf.fromEulerAnglesZYX(rotation, true));
            if (pivot != OpenVector3f.ZERO) {
                poseStack.translate(-pivot.x(), -pivot.y(), -pivot.z());
            }
        }
        // the scale have changes?
        var scale = scale();
        if (scale != OpenVector3f.ONE) {
            if (pivot != OpenVector3f.ZERO) {
                poseStack.translate(pivot.x(), pivot.y(), pivot.z());
            }
            poseStack.scale(scale.x(), scale.y(), scale.z());
            if (pivot != OpenVector3f.ZERO) {
                poseStack.translate(-pivot.x(), -pivot.y(), -pivot.z());
            }
        }
        // the after translate have changes?
        if (afterTranslate != OpenVector3f.ZERO) {
            poseStack.translate(afterTranslate.x(), afterTranslate.y(), afterTranslate.z());
        }
    }

    public void export(AnimatedPoint value) {
        value.clear();
        exportTranslate(value);
        exportRotation(value);
        exportScale(value);
    }

    private void exportTranslate(AnimatedPoint result) {
        var base = parent.translate();
        var delta = OpenVector3f.ZERO;
        for (var point : points) {
            var value = point.translation();
            if (value != OpenVector3f.ZERO) { // has any animation change this point?
                delta = value;
            }
        }
        result.setTranslation(base.x() + delta.x(), base.y() + delta.y(), base.z() + delta.z());
    }

    private void exportRotation(AnimatedPoint result) {
        var base = parent.rotation();
        var delta = OpenVector3f.ZERO;
        for (var point : defaultPoints) {
            var value = point.rotation();
            if (value != OpenVector3f.ZERO) { // has any animation change this point?
                delta = value;
            }
        }
        // if the wants the bone to be controlled, we always use the rotation by controller.
        if (controller != null) {
            delta = controller.rotation();
            base = OpenVector3f.ZERO; // the parent rotation will overwrite when a bone controlled.
        }
        // in mixed mode we need to merge all rotation.
        var x = base.x() + delta.x();
        var y = base.y() + delta.y();
        var z = base.z() + delta.z();
        for (var point : mixedPoints) {
            var value = point.rotation();
            if (value != OpenVector3f.ZERO) { // has any animation change this point?
                x += value.x();
                y += value.y();
                z += value.z();
            }
        }
        result.setRotation(OpenMath.wrapDegrees(x), OpenMath.wrapDegrees(y), OpenMath.wrapDegrees(z));
    }

    private void exportScale(AnimatedPoint result) {
        var base = parent.scale();
        var delta = OpenVector3f.ONE;
        for (var point : points) {
            var value = point.scale();
            if (value != OpenVector3f.ONE) { // has any animation change this point?
                delta = value;
            }
        }
        result.setScale(base.x() * delta.x(), base.y() * delta.y(), base.z() * delta.z());
    }

    public void clear() {
        points.forEach(AnimatedPoint::clear);
        dirty = 0;
    }

    public void reset() {
        snapshot = null;
    }

    public void setDirty(int flags) {
        dirty |= flags;
    }

    public void setController(AnimatedOutputPoint controller) {
        this.controller = controller;
    }

    public AnimatedOutputPoint controller() {
        return controller;
    }

    public OpenTransform3f parent() {
        return parent;
    }

    public OpenVector3f translation() {
        if (snapshot != null) {
            return snapshot.translation();
        }
        return parent.translate();
    }

    public OpenVector3f rotation() {
        if (snapshot != null) {
            return snapshot.rotation();
        }
        if (controller != null) {
            return controller.rotation();
        }
        return parent.rotation();
    }

    public OpenVector3f scale() {
        if (snapshot != null) {
            return snapshot.scale();
        }
        return parent.scale();
    }

    public OpenVector3f pivot() {
        return pivot;
    }
}

