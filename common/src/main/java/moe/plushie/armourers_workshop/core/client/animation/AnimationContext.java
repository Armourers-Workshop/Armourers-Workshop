package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.ExecutionContextImpl;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class AnimationContext {

    private final List<Snapshot> snapshots = new ArrayList<>();
    private final Map<AnimationController, AnimationPlayState> playStates = new HashMap<>();

    protected final ExecutionContextImpl executionContext;
    protected final List<AnimationController> animationControllers;

    public AnimationContext(ExecutionContextImpl executionContext, List<AnimationController> animationControllers) {
        this.executionContext = executionContext;
        this.animationControllers = animationControllers;
        // find all animated transform and add into context.
        var animatedTransforms = new LinkedHashSet<>(Collections.flatMap(animationControllers, AnimationController::affectedTransforms));
        for (var animatedTransform : animatedTransforms) {
            this.snapshots.add(new Snapshot(animatedTransform));
        }
    }

    public void beginUpdates(double animationTime) {
        for (var snapshot : snapshots) {
            snapshot.beginUpdates(animationTime);
        }
    }

    public void commitUpdates() {
        for (var snapshot : snapshots) {
            snapshot.commitUpdates();
        }
    }

    public void addAnimation(@Nullable AnimationController fromAnimationController, @Nullable AnimationController toAnimationController, double time, double speed, double duration) {
        // Find affected transform by from/to animation.
        var affectedTransforms = new ArrayList<AnimatedTransform>();
        affectedTransforms.addAll(Objects.flatMap(fromAnimationController, AnimationController::affectedTransforms, Collections.emptyList()));
        affectedTransforms.addAll(Objects.flatMap(toAnimationController, AnimationController::affectedTransforms, Collections.emptyList()));
        for (var snapshot : snapshots) {
            if (affectedTransforms.contains(snapshot.transform)) {
                snapshot.addTransitingAnimation(time, speed, duration);
            }
        }
    }

    protected void addPlayState(AnimationController animationController, AnimationPlayState playState) {
        playStates.put(animationController, playState);
    }

    @Nullable
    protected AnimationPlayState removePlayState(AnimationController animationController) {
        return playStates.remove(animationController);
    }

    @Nullable
    public AnimationPlayState getPlayState(AnimationController animationController) {
        return playStates.get(animationController);
    }

    public List<AnimationController> animationControllers() {
        return animationControllers;
    }

    public ExecutionContextImpl executionContext() {
        return executionContext;
    }


    private static class Snapshot {

        protected final AnimatedPoint currentValue = new AnimatedPoint();
        protected final AnimatedTransform transform;

        protected TransitingAnimation transitingAnimation;

        protected boolean isExported = false;

        public Snapshot(AnimatedTransform transform) {
            this.transform = transform;
        }

        public void beginUpdates(double animationTick) {
            // set snapshot to null, the transform will skip calculations.
            transform.snapshot = null;
            transform.clear();
            //
            if (transitingAnimation != null) {
                transitingAnimation.update(animationTick);
                if (transitingAnimation.isCompleted()) {
                    transitingAnimation = null;
                }
            }
        }

        public void commitUpdates() {
            // when no transiting or no change, we will need skip calculate.
            if (transitingAnimation == null && transform.dirty == 0) {
                isExported = false;
                return; // keep snapshot is null.
            }
            isExported = true;
            transform.export(currentValue);
            transform.snapshot = currentValue;
            // when the snapshot is transiting, we're mix tow snapshot calculation.
            if (transitingAnimation != null) {
                transitingAnimation.apply(currentValue);
                transform.snapshot = transitingAnimation.outputValue();
            }
        }

        protected void addTransitingAnimation(double time, double speed, double duration) {
            transitingAnimation = new TransitingAnimation(time, duration);
            var snapshotValue = transitingAnimation.snapshotValue();
            if (isExported) {
                snapshotValue.setTranslation(currentValue.translation());
                snapshotValue.setRotation(currentValue.rotation());
                snapshotValue.setScale(currentValue.scale());
            } else {
                var original = transform.parent();
                snapshotValue.setTranslation(original.translate());
                snapshotValue.setRotation(original.rotation());
                snapshotValue.setScale(original.scale());
            }
        }

        @Override
        public String toString() {
            return Objects.toString(this);
        }
    }

    private static class TransitingAnimation {

        private final AnimatedPoint snapshotValue = new AnimatedPoint();
        private final AnimatedPoint outputValue = new AnimatedPoint();

        private final double beginTime;
        private final double endTime;
        private final double duration;

        private float progress;
        private boolean isCompleted;

        public TransitingAnimation(double time, double duration) {
            this.beginTime = time;
            this.endTime = time + duration;
            this.duration = duration;
        }

        public void update(double time) {
            this.progress = (float) OpenMath.clamp((time - beginTime) / duration, 0.0, 1.0);
            this.isCompleted = time > endTime;
        }

        public void apply(AnimatedPoint currentValue) {
            var lt = snapshotValue.translation();
            var lr = snapshotValue.rotation();
            var ls = snapshotValue.scale();
            var rt = currentValue.translation();
            var rr = currentValue.rotation();
            var rs = currentValue.scale();
            float tx = OpenMath.lerp(progress, lt.x(), rt.x());
            float ty = OpenMath.lerp(progress, lt.y(), rt.y());
            float tz = OpenMath.lerp(progress, lt.z(), rt.z());
            float sx = OpenMath.lerp(progress, ls.x(), rs.x());
            float sy = OpenMath.lerp(progress, ls.y(), rs.y());
            float sz = OpenMath.lerp(progress, ls.z(), rs.z());
            float rx = OpenMath.lerp(progress, lr.x(), rr.x());
            float ry = OpenMath.lerp(progress, lr.y(), rr.y());
            float rz = OpenMath.lerp(progress, lr.z(), rr.z());
            outputValue.clear();
            outputValue.setTranslation(tx, ty, tz);
            outputValue.setScale(sx, sy, sz);
            outputValue.setRotation(rx, ry, rz);
        }

        public AnimatedPoint snapshotValue() {
            return snapshotValue;
        }

        public AnimatedPoint outputValue() {
            return outputValue;
        }

        public float progress() {
            return progress;
        }

        public boolean isCompleted() {
            return isCompleted;
        }
    }
}
