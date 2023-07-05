package com.apple.library.uikit;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.ReversedIteratorImpl;
import com.apple.library.impl.ViewImpl;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class UIView extends UIResponder implements ViewImpl {

    protected final Flags _flags = new Flags();
    protected final ArrayList<UIView> _subviews = new ArrayList<>();

    private WeakReference<UIView> superview;
    private WeakReference<UIWindow> window;

    private Object tooltip;
    private Object contents;

    private UIColor backgroundColor;

    private CGPoint origin = CGPoint.ZERO;
    private CGRect bounds = CGRect.ZERO;
    private CGRect frame = CGRect.ZERO;

    private int zIndex = 0;
    private int autoresizingMask = 0;
    private boolean isOpaque = true;

    public UIView(CGRect frame) {
        this.setFrame(frame);
    }

    public void addSubview(UIView view) {
        _insertViewAtIndex(view, _subviews.size());
    }

    public void insertViewAtIndex(UIView view, int index) {
        _insertViewAtIndex(view, index);
    }

    public void removeFromSuperview() {
        UIView superview = this.superview();
        if (superview == null) {
            return;
        }
        superview._subviews.remove(this);
        _setSuperview(null);
    }

    public void bringSubviewToFront(UIView view) {
        if (_subviews.remove(view)) {
            _subviews.add(view);
        }
    }

    public void sendSubviewToBack(UIView view) {
        if (_subviews.remove(view)) {
            _subviews.add(0, view);
        }
    }

    public void layoutSubviews() {
    }

    public void layoutIfNeeded() {
        if (!_flags.isDirty) {
            return;
        }
        if (_flags.needsLayout) {
            layoutSubviews();
        }
        for (UIView subview : subviews()) {
            subview.layoutIfNeeded();
        }
        _flags.isDirty = false;
        _flags.needsLayout = false;
    }

    public void render(CGPoint point, CGGraphicsContext context) {
        context.drawContents(contents(), bounds(), this);
    }

    public boolean pointInside(CGPoint point, UIEvent event) {
        CGRect rect = bounds();
        // hitTestInsets
        return rect.contains(point);
    }

    @Nullable
    public UIView hitTest(CGPoint point, UIEvent event) {
        if (_ignoresTouchEvents(this)) {
            return null;
        }
        if (!pointInside(point, event)) {
            return null;
        }
        for (UIView subview : _subviewsForRev()) {
            UIView hitView = subview.hitTest(convertPointToView(point, subview), event);
            if (hitView != null) {
                return hitView;
            }
        }
        return this;
    }

    public CGPoint convertPointFromView(CGPoint point, @Nullable UIView view) {
        if (view == null) {
            view = window();
        }
        CGPoint offset = _offsetInViewHierarchy(view, true);
        if (offset != null) {
            return new CGPoint(point.x - offset.x, point.y - offset.y);
        }
        return point;
    }

    public CGPoint convertPointToView(CGPoint point, @Nullable UIView view) {
        if (this == view) {
            return point;
        }
        CGPoint offset = _offsetInViewHierarchy(view, true);
        if (offset != null) {
            return new CGPoint(point.x + offset.x, point.y + offset.y);
        }
        return point;
    }

    public CGRect convertRectFromView(CGRect rect, @Nullable UIView view) {
        if (this == view) {
            return rect;
        }
        CGPoint resolvedPoint = convertPointFromView(new CGPoint(rect.x, rect.y), view);
        return new CGRect(resolvedPoint.x, resolvedPoint.y, rect.width, rect.height);
    }

    public CGRect convertRectToView(CGRect rect, @Nullable UIView view) {
        if (this == view) {
            return rect;
        }
        CGPoint resolvedPoint = convertPointToView(new CGPoint(rect.x, rect.y), view);
        return new CGRect(resolvedPoint.x, resolvedPoint.y, rect.width, rect.height);
    }

    public void willMoveToSuperview(UIView newSuperview) {
    }

    public void didMoveToSuperview() {
    }

    public void willMoveToWindow(UIWindow newWindow) {
    }

    public void didMoveToWindow() {
    }

    public void layerWillDraw(CGGraphicsContext context) {
    }

    public void layerDidDraw(CGGraphicsContext context) {
    }

    public void focusesDidChange() {
    }

    public void sizeToFit() {
    }

    @Override
    public UIResponder nextResponder() {
        return superview();
    }

    public Object contents() {
        return this.contents;
    }

    public Object tooltip() {
        return tooltip;
    }

    @Nullable
    public final UIView superview() {
        if (this.superview != null) {
            return this.superview.get();
        }
        return null;
    }

    @Nullable
    public final UIWindow window() {
        if (this.window != null) {
            return this.window.get();
        }
        return null;
    }

    public final List<UIView> subviews() {
        return this._subviews;
    }

    public UIColor backgroundColor() {
        return backgroundColor;
    }

    public CGPoint origin() {
        return origin;
    }

    public CGRect bounds() {
        return bounds;
    }

    public CGRect frame() {
        return frame;
    }

   public boolean canBecomeFocused() {
        return false;
   }

    public boolean isFocused() {
        return _flags.isFocused;
    }

    @Override
    public UIView self() {
        return this;
    }

    public void setOrigin(CGPoint origin) {
        if (this.origin.equals(origin)) {
            return;
        }
        this.origin = origin;
        this._remakeFrame();
    }

    public void setBounds(CGRect newValue) {
        CGRect oldValue = bounds();
        if (oldValue.equals(newValue)) {
            return;
        }
        this.bounds = newValue;
        this._remakeFrame();
        if (this._flags.needsAutoresizing) {
            this._resizeSubviewsWithOldSize(oldValue, bounds());
        }
        this.sizeDidChange();
    }

    public void setFrame(CGRect frame) {
        if (this.frame.equals(frame)) {
            return;
        }
        CGRect oldBounds = bounds();
        this.setBounds(new CGRect(oldBounds.x, oldBounds.y, frame.width, frame.height));
        this.setOrigin(new CGPoint(frame.x + frame.width / 2, frame.y + frame.height / 2));
    }

    public void setContents(Object contents) {
        this.contents = contents;
    }

    public void setTooltip(Object tooltip) {
        this.tooltip = tooltip;
    }

    public void setBackgroundColor(UIColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isHidden() {
        return _flags.isHidden;
    }

    public void setHidden(boolean isHidden) {
        _flags.isHidden = isHidden;
    }

    public boolean isOpaque() {
        return isOpaque;
    }

    public void setOpaque(boolean opaque) {
        isOpaque = opaque;
    }

    public int zIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public boolean isClipBounds() {
        return _flags.isClipBounds;
    }

    public void setClipBounds(boolean clipBounds) {
        _flags.isClipBounds = clipBounds;
    }

    public boolean isUserInteractionEnabled() {
        return _flags.isUserInteractionEnabled;
    }

    public void setUserInteractionEnabled(boolean isUserInteractionEnabled) {
        _flags.isUserInteractionEnabled = isUserInteractionEnabled;
    }

    public int autoresizingMask() {
        return autoresizingMask;
    }

    public void setAutoresizingMask(int autoresizingMask) {
        this.autoresizingMask = autoresizingMask;
        UIView superview = superview();
        if (superview != null) {
            superview._setAutoresizingFlagsDirty();
        }
    }

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "frame", frame);
    }

    public void setNeedsLayout() {
        _flags.needsLayout = true;
        _setDirty();
    }

    private void sizeDidChange() {
        // when the size changes, we need call the this.layoutSubview() and superview.layoutSubviews()
        setNeedsLayout();
        UIView superview = this.superview();
        if (superview != null) {
            superview.setNeedsLayout();
        }
    }

    private void _setDirty() {
        _flags.isDirty = true;
        UIView superview = this.superview();
        if (superview != null && !superview._flags.isDirty) {
            superview._setDirty();
        }
    }

    private void _setAutoresizingFlagsDirty() {
        _flags.needsAutoresizing = subviews().stream().anyMatch(view -> view.autoresizingMask() != 0);
    }

    private void _insertViewAtIndex(UIView view, int index) {
        if (this == view) {
            throw new RuntimeException("Can't add self");
        }
        if (view.superview != null) {
            view.removeFromSuperview();
        }
        if (index < _subviews.size()) {
            _subviews.add(index, view);
        } else {
            _subviews.add(view);
        }
        view._setSuperview(this);
    }

    private void _resizeSubviewsWithOldSize(CGRect oldValue, CGRect newValue) {
        for (UIView subview : subviews()) {
            subview._resizeWithOldSuperviewSize(oldValue, newValue);
        }
    }

    private void _resizeWithOldSuperviewSize(CGRect oldParentValue, CGRect newParentValue) {
        int mask = autoresizingMask();
        if (mask == 0) {
            return;
        }
        CGRect frame = frame();
        int[] h = _applyAutoresizingMask(frame.x, frame.width, newParentValue.width, oldParentValue.width, mask);
        int[] v = _applyAutoresizingMask(frame.y, frame.height, newParentValue.height, oldParentValue.height, mask >> 3);
        CGRect newFrame = new CGRect(h[0], v[0], h[1], v[1]);
        if (!newFrame.equals(frame)) {
            setFrame(newFrame);
        }
    }

    private void _setSuperview(UIView newSuperview) {
        if (this.superview() == newSuperview) {
            return;
        }
        this.willMoveToSuperview(newSuperview);
        this.superview = new WeakReference<>(newSuperview);
        this.setNeedsLayout();
        this.didMoveToSuperview();
        if (newSuperview == null) {
            _setWindow(null);
            return;
        }
        newSuperview._setAutoresizingFlagsDirty();
        if (newSuperview.window() != null) {
            _setWindow(newSuperview.window);
        }
        if (newSuperview instanceof UIWindow) {
            UIWindow window = (UIWindow) newSuperview;
            _setWindow(new WeakReference<>(window));
        }
    }

    private void _setWindow(WeakReference<UIWindow> window) {
        if (this.window == window) {
            return;
        }
        UIWindow oldWindow = window();
        UIWindow newWindow = null;
        if (window != null) {
            newWindow = window.get();
        }
        this.willMoveToWindow(newWindow);
        this.window = window;
        this._subviews.forEach(subview -> subview._setWindow(window));
        this.didMoveToWindow();
        // when a view is remove from window, the window needs additional processing.
        if (oldWindow != null && newWindow == null) {
            oldWindow._didRemoveFromWindow(this);
        }
    }

    protected int _testFlags(int value, int mask) {
        if ((value & mask) != 0) {
            return 1;
        }
        return 0;
    }

    private void _remakeFrame() {
        int x = origin.x - bounds.width / 2;
        int y = origin.y - bounds.height / 2;
        this.frame = new CGRect(x, y, bounds.width, bounds.height);
    }

    protected Iterable<UIView> _subviewsForRev() {
        List<UIView> subviews = subviews();
        ReversedIteratorImpl<UIView> iterator = new ReversedIteratorImpl<>(subviews.listIterator(subviews.size()));
        return () -> iterator;
    }

    protected static class Flags {

        boolean isHidden = false;
        boolean isClipBounds = false;
        boolean isHovered = false;
        boolean isFocused = false;
        boolean isUserInteractionEnabled = true;
        boolean isDirty = false;

        boolean needsLayout = false;
        boolean needsAutoresizing = false;
    }

    public static class AutoresizingMask {
        public static final int flexibleLeftMargin = 1 << 0;
        public static final int flexibleWidth = 1 << 1;
        public static final int flexibleRightMargin = 1 << 2;
        public static final int flexibleTopMargin = 1 << 3;
        public static final int flexibleHeight = 1 << 4;
        public static final int flexibleBottomMargin = 1 << 5;
    }
}
