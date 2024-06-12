package com.apple.library.uikit;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.InterpolableImpl;
import com.apple.library.impl.ObjectUtilsImpl;
import com.apple.library.impl.ViewImpl;
import com.apple.library.quartzcore.CAAnimation;
import com.apple.library.quartzcore.CAMediaTimingFunction;
import com.apple.library.quartzcore.CATransaction;

import java.util.HashMap;
import java.util.function.Function;

@SuppressWarnings("unused")
class UIPresentationDelegate implements ViewImpl {

    protected final UIView view;
    protected HashMap<String, CAAnimation> animations;

    public UIPresentationDelegate(UIView view) {
        this.view = view;
    }

    @Override
    public UIView self() {
        return view;
    }

    @Override
    public CGPoint center() {
        return _valueForKeyPath("center", UIView::center);
    }

    @Override
    public CGRect bounds() {
        return _valueForKeyPath("bounds", UIView::bounds);
    }

    @Override
    public CGAffineTransform transform() {
        return _valueForKeyPath("transform", UIView::transform);
    }

    public CAAnimation animationForKey(String key) {
        if (animations != null) {
            return animations.get(key);
        }
        return null;
    }

    public <T extends InterpolableImpl<T>> void addAnimationForKeyPath(T oldValue, T newValue, String keyPath) {
        if (!CATransaction._isEnabled()) {
            return;
        }
        CAAnimation animation = CAAnimation.animationWithKeyPath(keyPath);
        animation.setFromValue(oldValue);
        animation.setToValue(newValue);
        addAnimationForKeyPath(animation, keyPath);
    }

    public void addAnimationForKeyPath(CAAnimation animation, String keyPath) {
        if (!CATransaction._isEnabled()) {
            return;
        }
        removeAnimationForKey(keyPath, true);
        if (animations == null) {
            animations = new HashMap<>();
        }
        animations.put(keyPath, animation);
        CATransaction._addAnimation(animation, keyPath, () -> removeAnimationForKey(keyPath, false));
    }

    public void removeAnimationForKey(String key, boolean notify) {
        if (animations == null) {
            return;
        }
        CAAnimation oldValue = animations.remove(key);
        if (oldValue != null && notify) {
            CATransaction._removeAnimation(oldValue, key);
        }
        if (animations.isEmpty()) {
            animations = null;
        }
    }

    public boolean hasAnimations() {
        return animations != null && !animations.isEmpty();
    }

    @Override
    public CGAffineTransform _invertedTransform() {
        CGAffineTransform transform = transform();
        if (transform != view.transform()) {
            return transform.inverted();
        }
        return view._invertedTransform();
    }

    public <T extends InterpolableImpl<T>> T _valueForKeyPath(String keyPath, Function<UIView, T> getter) {
        CAAnimation animation = animationForKey(keyPath);
        if (animation != null) {
            double t = animation._currentTime(CATransaction.currentMediaTime());
            double dur = animation.duration();
            if (t < 0 || t >= dur) {
                return getter.apply(view);
            }

            T fromValue = ObjectUtilsImpl.unsafeCast(animation.fromValue());
            T toValue = ObjectUtilsImpl.unsafeCast(animation.toValue());

            CAMediaTimingFunction f = animation.timingFunction();
            return fromValue.interpolating(toValue, f.applying((float) (t / dur)));
        }
        return getter.apply(view);
    }

    @Override
    public ViewImpl _superviewInViewHierarchy(ViewImpl searchingView) {
        UIView superview = searchingView.self().superview();
        if (superview == null) {
            return null;
        }
        var presentation = superview._presentation;
        if (presentation.hasAnimations()) {
            return presentation;
        }
        return superview;
    }
}
