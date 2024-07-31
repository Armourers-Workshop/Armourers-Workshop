package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AnimationContext {

    protected final ArrayList<Snapshot> snapshots = new ArrayList<>();
    protected final HashMap<AnimationController, AnimationController.PlayState> playStates = new HashMap<>();

    public AnimationContext() {
    }

    public AnimationContext(AnimationContext context) {
        context.snapshots.forEach(snapshot -> {
            snapshots.add(new Snapshot.Variant(snapshot));
        });
    }


    public static AnimationContext from(List<BakedSkinPart> skinParts) {
        // find all animated transform and convert to snapshot.
        var context = new AnimationContext();
        ObjectUtils.search(skinParts, BakedSkinPart::getChildren, part -> {
            for (var transform : part.getTransform().getChildren()) {
                if (transform instanceof AnimatedTransform transform1) {
                    context.snapshots.add(new Snapshot(part, transform1));
                }
            }
        });
        return context;
    }

    public void begin(float animationTicks) {
        for (var snapshot : snapshots) {
            snapshot.begin(animationTicks);
        }
    }

    public void commit() {
        for (var snapshot : snapshots) {
            snapshot.commit();
        }
    }

    public void addAnimation(@Nullable AnimationController fromAnimationController, @Nullable AnimationController toAnimationController, float beginTime, float duration) {
        // Find affected parts by from/to animation.
        var affectedParts = new ArrayList<BakedSkinPart>();
        affectedParts.addAll(ObjectUtils.flatMap(fromAnimationController, AnimationController::getParts, Collections.emptyList()));
        affectedParts.addAll(ObjectUtils.flatMap(toAnimationController, AnimationController::getParts, Collections.emptyList()));
        for (var snapshot : snapshots) {
            if (snapshot instanceof Snapshot.Variant variant && affectedParts.contains(snapshot.part)) {
                variant.beginTransiting(beginTime, duration);
            }
        }
    }

    @Nullable
    public AnimationController.PlayState getPlayState(AnimationController animationController) {
        return playStates.get(animationController);
    }

    public boolean isEmpty() {
        return snapshots.isEmpty();
    }


    public static class Snapshot {

        protected final BakedSkinPart part;
        protected final AnimatedTransform transform;

        public Snapshot(BakedSkinPart part, AnimatedTransform transform) {
            this.part = part;
            this.transform = transform;
        }

        public void begin(float animationTicks) {
            // set snapshot to null, the transform will skip calculations.
            transform.snapshot = null;
        }

        public void commit() {
            // nop.
        }


        public static class Variant extends Snapshot {

            protected final AnimatedPoint currentValue = new AnimatedPoint();

            protected Transiting transitingAnimation;

            protected boolean isExported = false;

            public Variant(Snapshot snapshot) {
                super(snapshot.part, snapshot.transform);
            }

            @Override
            public void begin(float animationTicks) {
                // set snapshot to null, the transform will skip calculations.
                transform.snapshot = null;
                transform.clear();
                //
                if (transitingAnimation != null) {
                    transitingAnimation.update(animationTicks);
                    if (transitingAnimation.isCompleted()) {
                        endTransiting();
                    }
                }
            }

            @Override
            public void commit() {
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
                    var fromValue = transitingAnimation.getFromValue();
                    var toValue = transitingAnimation.getToValue();
                    var progress = transitingAnimation.getProgress();
                    applyTransiting(fromValue, currentValue, toValue, progress);
                    transform.snapshot = toValue;
                }
            }

            protected void beginTransiting(float time, float duration) {
                transitingAnimation = new Transiting(time, duration);
                var fromValue = transitingAnimation.getFromValue();
                if (isExported) {
                    fromValue.setTranslate(currentValue.getTranslate());
                    fromValue.setRotate(currentValue.getRotation());
                    fromValue.setScale(currentValue.getScale());
                } else {
                    var parent = transform.getParent();
                    fromValue.setTranslate(parent.getTranslate());
                    fromValue.setRotate(parent.getRotation());
                    fromValue.setScale(parent.getScale());
                }
            }

            protected void applyTransiting(AnimatedPoint fromPoint, AnimatedPoint toPoint, AnimatedPoint output, float p) {
                var lt = fromPoint.getTranslate();
                var rt = toPoint.getTranslate();
                var lr = fromPoint.getRotation();
                var rr = toPoint.getRotation();
                var ls = fromPoint.getScale();
                var rs = toPoint.getScale();
                float tx = MathUtils.lerp(p, lt.getX(), rt.getX());
                float ty = MathUtils.lerp(p, lt.getY(), rt.getY());
                float tz = MathUtils.lerp(p, lt.getZ(), rt.getZ());
                float sx = MathUtils.lerp(p, ls.getX(), rs.getX());
                float sy = MathUtils.lerp(p, ls.getY(), rs.getY());
                float sz = MathUtils.lerp(p, ls.getZ(), rs.getZ());
                float rx = MathUtils.lerp(p, lr.getX(), rr.getX());
                float ry = MathUtils.lerp(p, lr.getY(), rr.getY());
                float rz = MathUtils.lerp(p, lr.getZ(), rr.getZ());
                output.clear();
                output.setTranslate(tx, ty, tz);
                output.setScale(sx, sy, sz);
                output.setRotate(rx, ry, rz);
            }

            protected void endTransiting() {
                transitingAnimation = null;
            }
        }

        public static class Transiting {

            private final AnimatedPoint fromValue = new AnimatedPoint();
            private final AnimatedPoint toValue = new AnimatedPoint();

            private final float beginTime;
            private final float endTime;
            private final float duration;

            private float progress;
            private boolean isCompleted;

            public Transiting(float time, float duration) {
                this.beginTime = time;
                this.endTime = time + duration;
                this.duration = duration;
            }

            public void update(float time) {
                this.progress = MathUtils.clamp((time - beginTime) / duration, 0.0f, 1.0f);
                this.isCompleted = time > endTime;
            }

            public AnimatedPoint getFromValue() {
                return fromValue;
            }

            public AnimatedPoint getToValue() {
                return toValue;
            }

            public float getProgress() {
                return progress;
            }

            public boolean isCompleted() {
                return isCompleted;
            }
        }
    }
}
