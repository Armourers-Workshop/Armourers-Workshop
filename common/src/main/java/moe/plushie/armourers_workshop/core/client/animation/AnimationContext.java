package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnimationContext {

    protected final ArrayList<SimpleSnapshot> snapshots = new ArrayList<>();
    protected final HashMap<AnimationController, AnimationController.PlayState> playStates = new HashMap<>();

    public AnimationContext() {
    }

    public AnimationContext(AnimationContext context) {
        context.snapshots.forEach(snapshot -> {
            snapshots.add(new ComplexSnapshot(snapshot.transform));
        });
    }


    public static AnimationContext from(List<BakedSkinPart> skinParts) {
        // find all animated transform and convert to snapshot.
        var context = new AnimationContext();
        ObjectUtils.search(skinParts, BakedSkinPart::getChildren, part -> {
            for (var transform : part.getTransform().getChildren()) {
                if (transform instanceof AnimatedTransform transform1) {
                    context.snapshots.add(new SimpleSnapshot(transform1));
                }
            }
        });
        return context;
    }

    public void begin(float animationTicks) {
        for (var snapshot : snapshots) {
            snapshot.begin();
        }
    }

    public void commit() {
        for (var snapshot : snapshots) {
            snapshot.commit();
        }
    }

    @Nullable
    public AnimationController.PlayState getPlayState(AnimationController animationController) {
        return playStates.get(animationController);
    }

    public boolean isEmpty() {
        return snapshots.isEmpty();
    }


    public static class SimpleSnapshot {

        protected final AnimatedTransform transform;

        public SimpleSnapshot(AnimatedTransform transform) {
            this.transform = transform;
        }

        public void begin() {
            // set snapshot to null, the transform will skip calculations.
            transform.snapshot = null;
        }

        public void commit() {
            // nop.
        }
    }

    public static class ComplexSnapshot extends SimpleSnapshot {

        protected final AnimatedPoint currentValue = new AnimatedPoint();

        protected AnimatedPoint startValue;

        public ComplexSnapshot(AnimatedTransform transform) {
            super(transform);
        }

        @Override
        public void begin() {
            // set snapshot to null, the transform will skip calculations.
            transform.snapshot = null;
            transform.clear();
        }

        @Override
        public void commit() {
            // when no transiting or no change, we will need skip calculate.
            if (startValue == null && transform.dirty == 0) {
                return; // keep snapshot is null.
            }
            transform.export(currentValue);

            // var tmp = lerp(startValue, currentValue, transiting_progress);
            // transform.snapshot = tmp;


            transform.snapshot = currentValue;
        }
    }

}
