package moe.plushie.armourers_workshop.core.skin.animation.core;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SkinAnimationTransform implements ITransform {

    private final OpenVector3f pivot;
    private final OpenVector3f afterTranslate;

    private final OpenTransform3f parent;

    protected final List<SkinAnimation.Output<OpenVector3f>> allValues = new ArrayList<>();

    protected final List<SkinAnimation.Output<OpenVector3f>> translationValues = new ArrayList<>();
    protected final List<SkinAnimation.Output<OpenVector3f>> rotationValues = new ArrayList<>();
    protected final List<SkinAnimation.Output<OpenVector3f>> scaleValues = new ArrayList<>();

    protected final List<SkinAnimation.Output<OpenVector3f>> mixedRotationValues = new ArrayList<>();

    protected SkinAnimationPose controller;
    protected SkinAnimationPose snapshot;

    protected int dirty = 0;

    public SkinAnimationTransform(OpenTransform3f parent) {
        this.parent = parent;
        this.pivot = parent.pivot();
        this.afterTranslate = parent.afterTranslate();
    }

    @Nullable
    public static SkinAnimationTransform of(SkinPartTransform partTransform) {
        // when animation transform already been created, we just use it directly.
        for (var childTransform : partTransform.children()) {
            if (childTransform instanceof SkinAnimationTransform animatedTransform) {
                return animatedTransform;
            }
        }
        // if part have a non-standard transform (preview mode),
        // we wil think this part can't be support animation.
        if (!(partTransform.parent() instanceof OpenTransform3f parent)) {
            return null;
        }
        // we will replace the standard transform to animated transform.
        var animatedTransform = new SkinAnimationTransform(parent);
        partTransform.replaceChild(parent, animatedTransform);
        return animatedTransform;
    }

    public void add(SkinAnimation.Output<OpenVector3f> output) {
        var values = identity(output.channel(), output.mode());
        if (values == null) {
            return; // not supported!!!
        }
        values.add(output);
        values.sort(Comparator.comparingInt(SkinAnimation.Output::priority));
        allValues.add(output);
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

    public void export(SkinAnimationPose pose) {
        exportTranslate(pose);
        exportRotation(pose);
        exportScale(pose);
    }

    public void clear() {
        allValues.forEach(SkinAnimation.Output::reset);
        dirty = 0;
    }

    public void reset() {
        snapshot = null;
    }

    public void set(SkinAnimationPose snapshot) {
        this.snapshot = snapshot;
    }

    public void setDirty() {
        dirty = 1;
    }

    public boolean isDirty() {
        return dirty != 0;
    }

    public void setController(SkinAnimationPose controller) {
        this.controller = controller;
    }

    public SkinAnimationPose controller() {
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

    private void exportTranslate(SkinAnimationPose pose) {
        var base = parent.translate();
        var delta = OpenVector3f.ZERO;
        for (var output : translationValues) {
            var value = output.get();
            if (value != null) { // has any animation change this point?
                delta = value;
            }
        }
        pose.setTranslation(base.x() + delta.x(), base.y() + delta.y(), base.z() + delta.z());
    }

    private void exportRotation(SkinAnimationPose pose) {
        var base = parent.rotation();
        var delta = OpenVector3f.ZERO;
        for (var output : rotationValues) {
            var value = output.get();
            if (value != null) { // has any animation change this point?
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
        for (var output : mixedRotationValues) {
            var value = output.get();
            if (value != null) { // has any animation change this point?
                x += value.x();
                y += value.y();
                z += value.z();
            }
        }
        pose.setRotation(OpenMath.wrapDegrees(x), OpenMath.wrapDegrees(y), OpenMath.wrapDegrees(z));
    }

    private void exportScale(SkinAnimationPose pose) {
        var base = parent.scale();
        var delta = OpenVector3f.ONE;
        for (var output : scaleValues) {
            var value = output.get();
            if (value != null) { // has any animation change this point?
                delta = value;
            }
        }
        pose.setScale(base.x() * delta.x(), base.y() * delta.y(), base.z() * delta.z());
    }

    private List<SkinAnimation.Output<OpenVector3f>> identity(SkinAnimation.Channel channel, SkinAnimation.Mode mode) {
        return switch (channel) {
            case TRANSLATION -> translationValues;
            case ROTATION -> {
                if (mode.isMixMode()) {
                    yield mixedRotationValues;
                } else {
                    yield rotationValues;
                }
            }
            case SCALE -> scaleValues;
            default -> null;
        };
    }
}

