package com.apple.library.quartzcore;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Stack;

@SuppressWarnings("unused")
public class CATransaction {

    private static double time = 0;
    private static PassData current = new PassData();
    private static final Stack<PassData> animationStack = new Stack<>();
    private static final ArrayList<PassData> runningAnimations = new ArrayList<>();

    public static void begin() {
        animationStack.push(current);
        current = current.copy();
        current.begin();
    }

    public static void commit() {
        current.commit();
        runningAnimations.add(current);
        current = animationStack.pop();
    }

    public static double currentMediaTime() {
        return time;
    }

    public static double animationDuration() {
        return current.duration;
    }

    public static void setAnimationDuration(double duration) {
        current.duration = duration;
    }

    public static CAMediaTimingFunction animationTimingFunction() {
        return current.timingFunction;
    }

    public static void setAnimationTimingFunction(CAMediaTimingFunction function) {
        current.timingFunction = function;
    }

    public static Runnable completionBlock() {
        return current.completionBlock;
    }

    public static void setCompletionBlock(Runnable completionBlock) {
        current.completionBlock = completionBlock;
    }

    public static void _addAnimation(CAAnimation animation, String keyPath, Runnable completion) {
        animation._setAnimationData(current);
        current.animations.add(Pair.of(animation, completion));
    }

    public static void _removeAnimation(CAAnimation animation, String keyPath) {
        if (animation._animationData() instanceof PassData passData) {
            passData.remove(animation);
        }
    }

    public static void _updateAnimations(double tp) {
        time = tp;
        if (runningAnimations.isEmpty()) {
            return;
        }
        var iterator = runningAnimations.iterator();
        while (iterator.hasNext()) {
            var passData = iterator.next();
            passData.update(tp);
            if (passData.isEmpty()) {
                iterator.remove();
            }
        }
    }

    public static boolean _isEnabled() {
        return !animationStack.isEmpty();
    }

    public static class PassData {

        public double duration = 0.3f;
        public CAMediaTimingFunction timingFunction = CAMediaTimingFunction.EASE_IN_OUT;
        public Runnable completionBlock = null;
        public final ArrayList<Pair<CAAnimation, Runnable>> animations = new ArrayList<>();

        public void begin() {
        }

        public void commit() {
            var tp = CATransaction.currentMediaTime();
            animations.forEach(it -> {
                var ani = it.getKey();
                if (ani.beginTime() == 0) {
                    ani.setBeginTime(tp);
                }
                if (ani.duration() == 0) {
                    ani.setDuration(duration);
                }
            });
        }

        public void update(double tp) {
            var iterator = animations.iterator();
            while (iterator.hasNext()) {
                var it = iterator.next();
                var animation = it.getKey();
                var t = animation._currentTime(tp);
                var dur = animation.duration();
                if (t >= dur) {
                    it.getValue().run();
                    iterator.remove();
                }
            }
            completeIfNeeded();
        }

        public void remove(CAAnimation animation) {
            // when remove call we will do not notify the completion.
            animations.removeIf(it -> it.getKey() == animation);
            completeIfNeeded();
        }

        private void completeIfNeeded() {
            if (completionBlock != null && animations.isEmpty()) {
                completionBlock.run();
                completionBlock = null;
            }
        }

        public boolean isEmpty() {
            return animations.isEmpty();
        }

        public PassData copy() {
            var data = new PassData();
            data.duration = duration;
            data.timingFunction = timingFunction;
            return data;
        }
    }
}
