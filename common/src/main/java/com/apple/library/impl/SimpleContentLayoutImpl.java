package com.apple.library.impl;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIEdgeInsets;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

public class SimpleContentLayoutImpl {

    private float contentWidth = 0;
    private float contentHeight = 0;

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

    public void applyHorizontalLayout(CGRect bounds, UIEdgeInsets contentInsets, NSTextAlignment.Horizontal horizontalAlignment, NSTextAlignment.Vertical verticalAlignment) {
        results.clear();
        contentWidth = 0;
        contentHeight = 0;
        for (Pair<CGRect, UIEdgeInsets> pair : rects) {
            if (pair != null) {
                CGRect rect = pair.getKey();
                UIEdgeInsets edg = pair.getValue();
                contentWidth = contentWidth + edg.left + rect.x + rect.width + edg.right;
                contentHeight = Math.max(contentHeight, edg.top + rect.y + rect.height + edg.bottom);
            }
        }
        float x = bounds.x + contentInsets.left;
        float y = bounds.y + contentInsets.top;
        float width = bounds.width - contentInsets.left - contentInsets.right;
        float height = bounds.height - contentInsets.top - contentInsets.bottom;
        if (bounds == CGRect.ZERO) {
            width = contentWidth;
            height = contentHeight;
        }
        float dx = x + sel(width, contentWidth, horizontalAlignment);
        for (Pair<CGRect, UIEdgeInsets> pair : rects) {
            if (pair != null) {
                CGRect rect = pair.getKey();
                UIEdgeInsets edg = pair.getValue();
                float dy = y + sel(height - edg.top - edg.bottom, rect.y + rect.height, verticalAlignment);
                results.add(new CGRect(dx + edg.left + rect.x, dy + edg.top + rect.y, rect.width, rect.height));
                dx += rect.width + edg.left + edg.right;
            } else {
                results.add(null);
            }
        }
        contentWidth = contentInsets.left + contentWidth + contentInsets.right;
        contentHeight = contentInsets.top + contentHeight + contentInsets.bottom;
    }

    public void applyVerticalLayout(CGRect bounds, UIEdgeInsets contentInsets, NSTextAlignment.Horizontal horizontalAlignment, NSTextAlignment.Vertical verticalAlignment) {
        results.clear();
        contentWidth = 0;
        contentHeight = 0;
        for (Pair<CGRect, UIEdgeInsets> pair : rects) {
            if (pair != null) {
                CGRect rect = pair.getKey();
                UIEdgeInsets edg = pair.getValue();
                contentWidth = Math.max(contentWidth, edg.left + rect.x + rect.width + edg.right);
                contentHeight = contentHeight + edg.top + rect.y + rect.height + edg.bottom;
            }
        }
        float x = bounds.x + contentInsets.left;
        float y = bounds.y + contentInsets.top;
        float width = bounds.width - contentInsets.left - contentInsets.right;
        float height = bounds.height - contentInsets.top - contentInsets.bottom;
        if (bounds == CGRect.ZERO) {
            width = contentWidth;
            height = contentHeight;
        }
        float dy = y + sel(height, contentHeight, verticalAlignment);
        for (Pair<CGRect, UIEdgeInsets> pair : rects) {
            if (pair != null) {
                CGRect rect = pair.getKey();
                UIEdgeInsets edg = pair.getValue();
                float dx = x + sel(width - edg.left - edg.right, rect.x + rect.width, horizontalAlignment);
                results.add(new CGRect(dx + edg.left + rect.x, dy + edg.top + rect.y, rect.width, rect.height));
                dy += rect.height + edg.top + edg.bottom;
            } else {
                results.add(null);
            }
        }
        contentWidth = contentInsets.left + contentWidth + contentInsets.right;
        contentHeight = contentInsets.top + contentHeight + contentInsets.bottom;
    }

    public CGSize contentSize() {
        return new CGSize(contentWidth, contentHeight);
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

    private float sel(float height, float value, NSTextAlignment.Vertical alignment) {
        return switch (alignment) {
            case BOTTOM -> (height - value);
            case CENTER -> (height - value) / 2;
            default -> 0;
        };
    }

    private float sel(float width, float value, NSTextAlignment.Horizontal alignment) {
        return switch (alignment) {
            case RIGHT -> (width - value);
            case CENTER -> (width - value) / 2;
            default -> 0;
        };
    }
}
