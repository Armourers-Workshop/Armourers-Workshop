package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.impl.DelegateImpl;

import java.util.function.BiFunction;

public class UIScrollView extends UIView {

    protected CGPoint contentOffset = CGPoint.ZERO;
    protected CGSize contentSize = CGSize.ZERO;
    protected UIEdgeInsets contentInsets = UIEdgeInsets.ZERO;

    protected UIEdgeInsets scrollIndicatorInsets = new UIEdgeInsets(2, 2, 2, 2);

    protected final DelegateImpl<UIScrollViewDelegate> delegate = DelegateImpl.of(new UIScrollViewDelegate() {});

    private final boolean isInit;
    private final Indicator verticalIndicator = new Indicator((a, b) -> b);
    private final Indicator horizontalIndicator = new Indicator((a, b) -> a);

    public UIScrollView(CGRect frame) {
        super(frame);
        this.setClipBounds(true);
        super.addSubview(verticalIndicator);
        super.addSubview(horizontalIndicator);
        this.isInit = true;
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect bounds = bounds();
        CGRect rect = bounds.insetBy(scrollIndicatorInsets);
        verticalIndicator.setProgress(rect, contentOffset.y, contentSize.height - bounds.height);
        horizontalIndicator.setProgress(rect, contentOffset.x, contentSize.width - bounds.width);
    }

    @Override
    public void mouseWheel(UIEvent event) {
        super.mouseWheel(event);
        double delta = event.delta() * bounds().getHeight() / 5;
        int tx = contentOffset.x;
        int ty = contentOffset.y - (int) delta; // revert
        this.setContentOffset(new CGPoint(tx, ty));
    }

    public void flashScrollIndicators() {
        verticalIndicator.flash();
        horizontalIndicator.flash();
    }

    public CGPoint contentOffset() {
        return contentOffset;
    }

    public void setContentOffset(CGPoint contentOffset) {
        contentOffset = clamp(contentOffset);
        CGRect rect = bounds();
        this.contentOffset = contentOffset;
        super.setBounds(new CGRect(contentOffset.x, contentOffset.y, rect.width, rect.height));
        this.didScroll();
    }

    public CGSize contentSize() {
        return contentSize;
    }

    public void setContentSize(CGSize contentSize) {
        this.contentSize = contentSize;
        this.updateIndicatorIfNeeded();
        this.setNeedsLayout();
    }

    public UIEdgeInsets contentInsets() {
        return contentInsets;
    }

    public void setContentInsets(UIEdgeInsets contentInsets) {
        this.contentInsets = contentInsets;
        this.setNeedsLayout();
    }

    public UIEdgeInsets scrollIndicatorInsets() {
        return scrollIndicatorInsets;
    }

    public void setScrollIndicatorInsets(UIEdgeInsets scrollIndicatorInsets) {
        this.scrollIndicatorInsets = scrollIndicatorInsets;
        this.setNeedsLayout();
    }

    public boolean showsHorizontalScrollIndicator() {
        return horizontalIndicator.isEnabled();
    }

    public void setShowsHorizontalScrollIndicator(boolean showsHorizontalScrollIndicator) {
        horizontalIndicator.setEnabled(showsHorizontalScrollIndicator);
        updateIndicatorIfNeeded();
    }

    public boolean showsVerticalScrollIndicator() {
        return verticalIndicator.isEnabled();
    }

    public void setShowsVerticalScrollIndicator(boolean showsVerticalScrollIndicator) {
        verticalIndicator.setEnabled(showsVerticalScrollIndicator);
        updateIndicatorIfNeeded();
    }

    public UIScrollViewDelegate delegate() {
        return delegate.get();
    }

    public void setDelegate(UIScrollViewDelegate delegate) {
        this.delegate.set(delegate);
    }

    @Override
    public void setBounds(CGRect bounds) {
        super.setBounds(bounds);
        if (!isInit) {
            return;
        }
        this.contentOffset = new CGPoint(bounds.x, bounds.y);
        this.updateIndicatorIfNeeded();
        this.didScroll();
    }

    @Override
    public void addSubview(UIView view) {
        super.insertViewAtIndex(view, subviews().size() - 2);
    }

    @Override
    public void insertViewAtIndex(UIView view, int index) {
        super.insertViewAtIndex(view, Math.min(index, subviews().size() - 2));
    }

    private CGPoint clamp(CGPoint point) {
        CGRect rect = bounds();
        UIEdgeInsets edg = contentInsets;
        int tx = Math.max(Math.min(point.x, contentSize.width - rect.width + edg.right), -edg.left);
        int ty = Math.max(Math.min(point.y, contentSize.height - rect.height + edg.bottom), -edg.top);
        if (point.x == tx && point.y == ty) {
            return point;
        }
        return new CGPoint(tx, ty);
    }

    protected void didScroll() {
        delegate.invoker().scrollViewDidScroll(this);
    }

    private void updateIndicatorIfNeeded() {
        CGRect bounds = bounds();
        CGSize size = contentSize();
        verticalIndicator.setRadio(bounds.height, size.height);
        horizontalIndicator.setRadio(bounds.width, size.width);
    }

    protected static class Indicator extends UIView {

        protected int size = 3;
        protected float radio = 0;

        protected boolean allowsDisplay = true;
        protected boolean enabled = true;

        private final BiFunction<Integer, Integer, Integer> selector;

        public Indicator(BiFunction<Integer, Integer, Integer> selector) {
            super(CGRect.ZERO);
            this.selector = selector;
            this.setBackgroundColor(new UIColor(0x7f000000, true));
            this.setHidden(true);
        }

        private static float eval(float lhs, float rhs) {
            if (lhs == 0 || rhs <= 0) {
                return 0;
            }
            return lhs / rhs;
        }

        static float clamp(float value, float minValue, float maxValue) {
            if (value < minValue) {
                return minValue;
            }
            if (value > maxValue) {
                return maxValue;
            }
            return value;
        }

        public void setProgress(CGRect rect, float offset, float maxSize) {
            float m = selector.apply(rect.width, rect.height);
            float v = eval(offset, maxSize) * m * (1 - radio);
            float p = clamp(v, 0, m);
            float q = clamp(v + m * radio, 0, m);
            int x = selector.apply(rect.getMinX() + (int) p, rect.getMaxX() - size);
            int y = selector.apply(rect.getMaxY() - size, rect.getMinY() + (int) p);
            int width = selector.apply((int) (q - p), size);
            int height = selector.apply(size, (int) (q - p));
            setFrame(new CGRect(x, y, width, height));
            flash();
        }

        public void setRadio(float value, float maxValue) {
            if (value == 0 || maxValue == 0 || value <= maxValue) {
                setHidden(true);
                allowsDisplay = false;
                radio = 0;
                return;
            }
            radio = clamp(value / maxValue, 0.35f, 1.0f);
            allowsDisplay = isEnabled();
            flash();
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        private void flash() {
            if (!enabled) {
                return;
            }
            setHidden(false);
            // after and the hidden
        }
    }
}
