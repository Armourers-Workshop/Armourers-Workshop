package com.apple.library.impl;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.UIView;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public interface ViewImpl {

    UIView self();

    CGPoint center();

    CGRect bounds();

    CGAffineTransform transform();

    default CGPoint convertPointFromView(CGPoint point, @Nullable UIView view) {
        CGAffineTransform transform = _computeTransformInViewHierarchy(view, false);
        if (transform != null) {
            return point.applying(transform);
        }
        return point;
    }

    default CGPoint convertPointToView(CGPoint point, @Nullable UIView view) {
        CGAffineTransform transform = _computeTransformInViewHierarchy(view, true);
        if (transform != null) {
            return point.applying(transform);
        }
        return point;
    }

    default CGRect convertRectFromView(CGRect rect, @Nullable UIView view) {
        CGAffineTransform transform = _computeTransformInViewHierarchy(view, false);
        if (transform != null) {
            return rect.applying(transform);
        }
        return rect;
    }

    default CGRect convertRectToView(CGRect rect, @Nullable UIView view) {
        CGAffineTransform transform = _computeTransformInViewHierarchy(view, true);
        if (transform != null) {
            return rect.applying(transform);
        }
        return rect;
    }

    @Nullable
    default CGAffineTransform _computeTransformInViewHierarchy(@Nullable UIView searchedView, boolean reversed) {
        // yep, pass null to find window.
        if (searchedView == null) {
            searchedView = self().window();
            if (searchedView == null) {
                return null;
            }
        }
        // is self, not any convert changes.
        if (searchedView == self()) {
            return null;
        }
        ViewImpl parentView = null;
        LinkedList<ViewImpl> toViews = new LinkedList<>();
        LinkedList<ViewImpl> fromViews = new LinkedList<>();
        ViewImpl searchingView1 = this;
        ViewImpl searchingView2 = searchedView;
        HashSet<UIView> parentViews = new HashSet<>();
        Iterator<ViewImpl> cleaner = null;
        while (searchingView1 != null || searchingView2 != null) {
            if (searchingView1 != null) {
                if (!parentViews.add(searchingView1.self())) {
                    parentView = searchingView1.self();
                    cleaner = toViews.iterator();
                    break;
                }
                fromViews.addFirst(searchingView1);
                searchingView1 = _superviewInViewHierarchy(searchingView1);
            }
            if (searchingView2 != null) {
                if (!parentViews.add(searchingView2.self())) {
                    parentView = searchingView2.self();
                    cleaner = fromViews.iterator();
                    break;
                }
                toViews.addFirst(searchingView2);
                searchingView2 = _superviewInViewHierarchy(searchingView2);
            }
        }
        // when already found the common view, we need excluded the view of the common view above.
        // and the also excluded the common view, because common view does not need to eval.
        while (cleaner != null && cleaner.hasNext()) {
            ViewImpl view = cleaner.next();
            cleaner.remove();
            if (view.self() == parentView) {
                cleaner = null;
            }
        }
        if (parentView == null) {
            // can't find same parent view, maybe target view not in the same hierarchy.
            return null;
        }
        CGAffineTransform offset = null;
        ArrayList<Pair<Collection<ViewImpl>, Boolean>> enumerator = new ArrayList<>();
        if (fromViews.size() != 0) {
            enumerator.add(Pair.of(fromViews, reversed));
        }
        if (toViews.size() != 0) {
            enumerator.add(Pair.of(toViews, !reversed));
        }
        // when forward search, the evaluate order needs reverse.
        if (!reversed && enumerator.size() > 1) {
            Collections.reverse(enumerator);
        }
        for (Pair<Collection<ViewImpl>, Boolean> it : enumerator) {
            float tx = 0, ty = 0;
            CGAffineTransform translate = CGAffineTransform.createScale(1, 1);
            for (ViewImpl view : it.getLeft()) {
                CGAffineTransform transform = view.transform();
                CGPoint center = view.center();
                CGRect bounds = view.bounds();
                tx += center.x;
                ty += center.y;
                // TODO: the bounds origin need apply transform?
                tx -= bounds.x;
                ty -= bounds.y;
                if (transform.isIdentity()) {
                    tx -= bounds.width * 0.5f;
                    ty -= bounds.height * 0.5f;
                    continue;
                }
                // calculate transformed view size
                CGSize size = bounds.size();
                size.apply(transform);
                tx -= size.width * 0.5f;
                ty -= size.height * 0.5f;
                CGAffineTransform tmp = CGAffineTransform.createTranslation(-tx, -ty);
                tmp.concat(view._invertedTransform());
                translate.concat(tmp);
                tx = 0;
                ty = 0;
            }
            if (tx != 0 || ty != 0) {
                translate.concat(CGAffineTransform.createTranslation(-tx, -ty));
            }
            if (it.getRight()) {
                translate.invert();
            }
            // the first eval, we will get the offset from current view to common view,
            // the second eval, we will get the offset from the common view to target view,
            // the final offset is the current view to target view.
            if (offset == null) {
                offset = translate;
            } else {
                offset.concat(translate);
            }
        }
        return offset;
    }

    @Nullable
    default LinkedList<ViewImpl> _searchInViewHierarchy(UIView searchedView) {
        LinkedList<ViewImpl> results = new LinkedList<>();
        ViewImpl searchingView = this;
        while (searchingView != null && searchingView.self() != searchedView) {
            results.add(searchingView);
            searchingView = _superviewInViewHierarchy(searchingView);
        }
        UIView searchingView1 = ObjectUtilsImpl.flatMap(searchingView, ViewImpl::self);
        if (searchingView1 == searchedView) {
            return results;
        }
        return null;
    }

    default ViewImpl _superviewInViewHierarchy(ViewImpl searchingView) {
        return searchingView.self().superview();
    }

    default CGAffineTransform _invertedTransform() {
        return transform().inverted();
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

    default float[] _applyAutoresizingMask(float offset, float size, float newValue, float oldValue, int mask) {
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
                float dx = oldValue - offset - size;
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
        return new float[]{newOffset, Math.max(newSize, 0)};
    }
}
