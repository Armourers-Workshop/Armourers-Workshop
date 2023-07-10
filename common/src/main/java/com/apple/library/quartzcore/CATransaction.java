package com.apple.library.quartzcore;

import com.apple.library.impl.InterpolableImpl;
import com.apple.library.impl.ObjectUtilsImpl;
import com.apple.library.uikit.UIView;

import java.util.ArrayList;
import java.util.Stack;
import java.util.function.BiConsumer;

public class CATransaction {

    private static PassData ANIMATION = new PassData();
    private static final Stack<PassData> ANIMATION_STACK = new Stack<>();

    public static void begin() {
        ANIMATION_STACK.push(ANIMATION);
        ANIMATION = ANIMATION.copy();
        ANIMATION.begin();
    }

    public static void commit() {
        ANIMATION.commit();
        ANIMATION = ANIMATION_STACK.pop();
    }

    public static boolean isEnabled() {
        return !ANIMATION_STACK.isEmpty();
    }

    public static double animationDuration() {
        return ANIMATION.duration;
    }

    public static void setAnimationDuration(double duration) {
        ANIMATION.duration = duration;
    }

    public static CAMediaTimingFunction animationTimingFunction() {
        return ANIMATION.timingFunction;
    }

    public static void setAnimationTimingFunction(CAMediaTimingFunction function) {
        ANIMATION.timingFunction = function;
    }

    public static Runnable completionBlock() {
        return ANIMATION.completionBlock;
    }

    public static void setCompletionBlock(Runnable completionBlock) {
        ANIMATION.completionBlock = completionBlock;
    }

    public static <T extends InterpolableImpl<T>> void _addAnimation(String keyPath, T oldValue, T newValue, UIView.Presentation presentation) {
//        ANIMATION.animations.add(animation);
        if (!isEnabled()) {
            return;
        }
        CAAnimation animation = CAAnimation.animationWithKeyPath(keyPath);
        animation.setFromValue(oldValue);
        animation.setToValue(newValue);
        animation._setAnimationData(ANIMATION);
        presentation.addAnimationForKey(animation, keyPath);
        ANIMATION.animations.add(animation);
    }

    public static void _completeAnimation(String keyPath, CAAnimation animation) {
        PassData pass = ObjectUtilsImpl.safeCast(animation._animationData(), PassData.class);
        if (pass != null) {
            pass.animations.remove(animation);
            if (pass.completionBlock != null && pass.animations.isEmpty()) {
                pass.completionBlock.run();
                pass.completionBlock = null;
            }
        }
    }

    public static class PassData {

        public double duration = 0.3f;
        public CAMediaTimingFunction timingFunction = CAMediaTimingFunction.EASE_IN_OUT;
        public Runnable completionBlock;
        public ArrayList<CAAnimation> animations = new ArrayList<>();

        public void begin() {

        }

        public void commit() {
            double tp = ObjectUtilsImpl.currentMediaTime();
            animations.forEach(ani -> {
                if (ani.beginTime() == 0) {
                    ani.setBeginTime(tp);
                }
                if (ani.duration() == 0) {
                    ani.setDuration(duration);
                }
            });
        }

        public PassData copy() {
            PassData data = new PassData();
            data.duration = duration;
            data.timingFunction = timingFunction;
            return data;
        }
    }
}
