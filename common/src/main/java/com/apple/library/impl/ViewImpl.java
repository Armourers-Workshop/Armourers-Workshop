package com.apple.library.impl;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIView;
import org.jetbrains.annotations.Nullable;

public interface ViewImpl {

    UIView self();

    @Nullable
    default CGPoint _offsetInViewHierarchy(@Nullable UIView searchedView, boolean isAutoReverse) {
        int dx = 0;
        int dy = 0;
        UIView searchingView = self();
        while (searchingView != searchedView && searchingView != null) {
            CGRect frame = searchingView.frame();
            dx += frame.x;
            dy += frame.y;
            CGRect bounds = searchingView.bounds();
            dx -= bounds.x;
            dy -= bounds.y;
            searchingView = searchingView.superview();
        }
        if (searchingView == searchedView) {
            return new CGPoint(dx, dy);
        }
        if (isAutoReverse) {
            CGPoint point = searchedView._offsetInViewHierarchy(self(), false);
            if (point != null) {
                return new CGPoint(-point.x, -point.y);
            }
        }
        return null;
    }

    default boolean _ignoresTouchEvents(UIView view) {
        if (!view.isUserInteractionEnabled() || view.isHidden()) {
            return true;
        }
//        if (view.alpha() <= 0.001) {
//            return true;
//        }
        return false;
    }

    default int[] _applyAutoresizingMask(int offset, int size, int newValue, int oldValue, int mask) {
        float newOffset = offset;
        float newSize = size;
        switch (mask & 7) {
            case 1: {
                // left
                newOffset = newValue - (oldValue - offset);
                break;
            }
            case 2: {
                // width
                newSize = newValue - (oldValue - size);
                break;
            }
            case 3: {
                // left + width
                // when the old value is zero, we can't to infer the space usage!!!
                float usage = 0.5f;
                if (offset != 0 || size != 0) {
                    usage = (offset / (float) (offset + size));
                }
                int dx = oldValue - offset - size;
                newOffset = (newValue - dx) * usage;
                newSize = (newValue - dx) - newOffset;
                break;
            }
            case 5: {
                // left + right
                // when the parent and child has same size,
                // we directly use half of the remaining space.
                float usage = 0.5f;
                if (oldValue != size) {
                    usage = (offset / (float) (oldValue - size));
                }
                newOffset = (newValue - size) * usage;
                break;
            }
            case 6: {
                // width + right
                float usage = 1f;
                if (oldValue != size) {
                    usage = (size / (float) (oldValue - size));
                }
                newSize = (newValue - offset) * usage;
                break;
            }
            case 7: {
                // left + width + right
                // when the old value is zero, we can't to infer the space usage!!!
                float usage1 = 1 / 3f;
                float usage2 = usage1;
                if (oldValue != 0) {
                    usage1 = (offset / (float) oldValue);
                    usage2 = (size / (float) oldValue);
                }
                newOffset = newValue * usage1;
                newSize = newValue * usage2;
                break;
            }
            default: {
                // ignore right or other.
                break;
            }
        }
        return new int[]{Math.round(newOffset), Math.max(Math.round(newSize), 0)};
    }
}
