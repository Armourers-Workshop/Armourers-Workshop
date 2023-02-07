package com.apple.library.impl;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIEdgeInsets;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

public class LayoutManagerImpl {

    private final ArrayList<Pair<CGRect, UIEdgeInsets>> rects = new ArrayList<>();
    private final ArrayList<CGRect> results = new ArrayList<>();

    public <T> void add(T target, UIEdgeInsets edg, Function<T, CGRect> transform) {
        if (target != null) {
            add(transform.apply(target), edg);
        } else {
            add(null, edg);
        }
    }

    public void add(@Nullable CGRect rect, UIEdgeInsets edg) {
        if (rect == null) {
            rects.add(null);
        } else {
            rects.add(Pair.of(rect, edg));
        }
    }

    public void applyHorizontalLayout(CGRect bounds, NSTextAlignment.Horizontal horizontalAlignment, NSTextAlignment.Vertical verticalAlignment) {
        results.clear();
        int contentWidth = 0;
        for (Pair<CGRect, UIEdgeInsets> pair : rects) {
            if (pair != null) {
                CGRect rect = pair.getKey();
                UIEdgeInsets edg = pair.getValue();
                contentWidth += edg.left + rect.width + edg.right;
            }
        }
        int dx = bounds.x + sel(bounds.width, contentWidth, horizontalAlignment);
        for (Pair<CGRect, UIEdgeInsets> pair : rects) {
            if (pair != null) {
                CGRect rect = pair.getKey();
                UIEdgeInsets edg = pair.getValue();
                int dy = bounds.y + sel(bounds.height - edg.top - edg.bottom, rect.height, verticalAlignment);
                results.add(new CGRect(dx + edg.left + rect.x, dy + edg.top + rect.y, rect.width, rect.height));
                dx += rect.width + edg.left + edg.right;
            } else {
                results.add(null);
            }
        }
    }

    public void applyVerticalLayout(CGRect bounds, NSTextAlignment.Horizontal horizontalAlignment, NSTextAlignment.Vertical verticalAlignment) {
        results.clear();
        int contentHeight = 0;
        for (Pair<CGRect, UIEdgeInsets> pair : rects) {
            if (pair != null) {
                CGRect rect = pair.getKey();
                UIEdgeInsets edg = pair.getValue();
                contentHeight += edg.top + rect.height + edg.bottom;
            }
        }
        int dy = bounds.y + sel(bounds.height, contentHeight, verticalAlignment);
        for (Pair<CGRect, UIEdgeInsets> pair : rects) {
            if (pair != null) {
                CGRect rect = pair.getKey();
                UIEdgeInsets edg = pair.getValue();
                int dx = bounds.x + sel(bounds.width - edg.left - edg.right, rect.width, horizontalAlignment);
                results.add(new CGRect(dx + edg.left + rect.x, dy + edg.top + rect.y, rect.width, rect.height));
                dy += rect.height + edg.top + edg.bottom;
            } else {
                results.add(null);
            }
        }
    }

    @Nullable
    public CGRect getOrDefault(int index, CGRect defaultValue) {
        if (index >= 0 && index < results.size()) {
            CGRect rect = results.get(index);
            if (rect != null) {
                return rect;
            }
        }
        return defaultValue;
    }

    private int sel(int height, int value, NSTextAlignment.Vertical alignment) {
        switch (alignment) {
            case BOTTOM:
                return height - value;

            case CENTER:
                return (height - value) / 2;

            default:
                return 0;
        }
    }

    private int sel(int width, int value, NSTextAlignment.Horizontal alignment) {
        switch (alignment) {
            case RIGHT:
                return width - value;

            case CENTER:
                return (width - value) / 2;

            default:
                return 0;
        }
    }
}
