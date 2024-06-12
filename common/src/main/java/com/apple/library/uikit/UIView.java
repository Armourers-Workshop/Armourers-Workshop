package com.apple.library.uikit;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.impl.ObjectUtilsImpl;
import com.apple.library.impl.ReversedIteratorImpl;
import com.apple.library.impl.ViewImpl;
import com.apple.library.quartzcore.CALayer;
import com.apple.library.quartzcore.CATransaction;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class UIView extends UIResponder implements ViewImpl {

    protected final Flags _flags = new Flags();
    protected final ArrayList<UIView> _subviews = new ArrayList<>();
    protected final CALayer _layer = new CALayer();
    protected final UIPresentationDelegate _presentation = new UIPresentationDelegate(this);

    private WeakReference<UIView> _superview;
    private WeakReference<UIWindow> _window;

    private Object _tooltip;

    private UIColor _backgroundColor;

    private CGPoint _center = CGPoint.ZERO;
    private CGRect _bounds = CGRect.ZERO;
    private CGRect _frame = CGRect.ZERO;
    private CGAffineTransform _transform = CGAffineTransform.IDENTITY;
    private CGAffineTransform _invertedTransform = CGAffineTransform.IDENTITY;

    private int _tag = 0;
    private int _zIndex = 0;
    private int _autoresizingMask = 0;

    public UIView(CGRect frame) {
        this.setFrame(frame);
    }

    public static void animationWithDuration(double duration, Runnable animations) {
        animationWithDuration(duration, 0, animations, null);
    }

    public static void animationWithDuration(double duration, Runnable animations, @Nullable Consumer<Boolean> completion) {
        animationWithDuration(duration, 0, animations, completion);
    }

    public static void animationWithDuration(double duration, int options, Runnable animations, @Nullable Consumer<Boolean> completion) {
        CATransaction.begin();
        CATransaction.setAnimationDuration(duration);
        if (completion != null) {
            CATransaction.setCompletionBlock(() -> completion.accept(true));
        }
        animations.run();
        CATransaction.commit();
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
        context.drawContents(_layer.contents(), bounds(), this);
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
        for (UIView subview : _invertedSubviews()) {
            UIView hitView = subview.hitTest(convertPointToView(point, subview), event);
            if (hitView != null) {
                return hitView;
            }
        }
        return this;
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
        CGPoint center = center();
        CGRect rect = bounds();
        CGSize size = sizeThatFits(rect.size());
        float dx = (size.getWidth() - rect.getWidth()) / 2;
        float dy = (size.getHeight() - rect.getHeight()) / 2;
        setBounds(new CGRect(rect.getX(), rect.getY(), size.getWidth(), size.getHeight()));
        setCenter(new CGPoint(center.getX() + dx, center.getY() + dy));
    }

    public CGSize sizeThatFits(CGSize size) {
        return size;
    }

    public boolean isDescendantOfView(UIView superview) {
        UIView searchingView = this;
        while (searchingView != null) {
            if (searchingView == superview) {
                return true;
            }
            searchingView = searchingView.superview();
        }
        return false;
    }

    @Override
    public UIResponder nextResponder() {
        return superview();
    }

    public Object tooltip() {
        return _tooltip;
    }

    @Nullable
    public final UIView superview() {
        if (_superview != null) {
            return _superview.get();
        }
        return null;
    }

    @Nullable
    public final UIWindow window() {
        if (_window != null) {
            return _window.get();
        }
        return null;
    }

    public final List<UIView> subviews() {
        return _subviews;
    }

    public UIColor backgroundColor() {
        return _backgroundColor;
    }

    @Override
    public CGPoint center() {
        return _center;
    }

    @Override
    public CGRect bounds() {
        return _bounds;
    }

    public CGRect frame() {
        if (_frame == null) {
            _frame = _remakeFrame();
        }
        return _frame;
    }

    @Override
    public CGAffineTransform transform() {
        return _transform;
    }

    public CALayer layer() {
        return _layer;
    }

    public boolean canBecomeFocused() {
        return false;
    }

    public boolean isFocused() {
        return _flags.isFocused;
    }

    public boolean shouldBecomeFocused(UIView subview) {
        return true;
    }

    public void setCenter(CGPoint center) {
        CGPoint oldValue = _center;
        if (_center.equals(center)) {
            return;
        }
        _frame = null;
        _center = center;
        _presentation.addAnimationForKeyPath(oldValue, center, "center");
    }

    public void setBounds(CGRect bounds) {
        CGRect oldValue = bounds();
        if (oldValue.equals(bounds)) {
            return;
        }
        _frame = null;
        _bounds = bounds;
        if (_flags.needsAutoresizing) {
            _resizeSubviewsWithOldSize(oldValue, bounds());
        }
        _sizeDidChange();
        _presentation.addAnimationForKeyPath(oldValue, bounds, "bounds");
    }

    public void setFrame(CGRect frame) {
        if (frame().equals(frame)) {
            return;
        }
        CGRect bounds = bounds().copy();
        CGPoint center = new CGPoint(frame.x + frame.width * 0.5f, frame.y + frame.height * 0.5f);
        bounds.width = frame.width;
        bounds.height = frame.height;
        // we don't need fully checks of the identity transform,
        // just needs to reduce transform apply calls.
        if (_transform != CGAffineTransform.IDENTITY) {
            CGSize size = frame.size();
            size.apply(_invertedTransform());
            bounds.width = size.width;
            bounds.height = size.height;
        }
        setCenter(center);
        setBounds(bounds);
    }

    public void setTransform(CGAffineTransform transform) {
        CGAffineTransform oldValue = _transform;
        if (_transform.equals(transform)) {
            return;
        }
        _transform = transform;
        _invertedTransform = null;
        _frame = null;
        _presentation.addAnimationForKeyPath(oldValue, transform, "transform");
    }

    public void setTooltip(Object tooltip) {
        _tooltip = tooltip;
    }

    public void setBackgroundColor(UIColor backgroundColor) {
        _backgroundColor = backgroundColor;
    }

    public boolean isHidden() {
        return _layer.isHidden();
    }

    public void setHidden(boolean isHidden) {
        _layer.setHidden(isHidden);
    }

    public boolean isOpaque() {
        return _layer.isOpaque();
    }

    public void setOpaque(boolean opaque) {
        _layer.setOpaque(opaque);
    }

    public float alpha() {
        return _layer.opacity();
    }

    public void setAlpha(float alpha) {
        _layer.setOpacity(alpha);
    }

    public int zIndex() {
        return _zIndex;
    }

    public void setZIndex(int zIndex) {
        _zIndex = zIndex;
    }

    public boolean isClipBounds() {
        return _layer.masksToBounds();
    }

    public void setClipBounds(boolean clipBounds) {
        _layer.setMasksToBounds(clipBounds);
    }

    public boolean isUserInteractionEnabled() {
        return _flags.isUserInteractionEnabled;
    }

    public void setUserInteractionEnabled(boolean isUserInteractionEnabled) {
        _flags.isUserInteractionEnabled = isUserInteractionEnabled;
    }

    public int autoresizingMask() {
        return _autoresizingMask;
    }

    public void setAutoresizingMask(int autoresizingMask) {
        _autoresizingMask = autoresizingMask;
        UIView superview = superview();
        if (superview != null) {
            superview._setAutoresizingFlagsDirty();
        }
    }

    public int tag() {
        return _tag;
    }

    public void setTag(int tag) {
        _tag = tag;
    }

    public Object contents() {
        return _layer.contents();
    }

    public void setContents(Object contents) {
        _layer.setContents(contents);
    }

    public void setNeedsLayout() {
        _flags.needsLayout = true;
        _setDirty();
    }

    @Override
    public UIView self() {
        return this;
    }

    @Override
    public String toString() {
        return ObjectUtilsImpl.makeDescription(this, "frame", frame(), "transform", _transform);
    }

    private void _setDirty() {
        _flags.isDirty = true;
        UIView superview = superview();
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
        if (view._superview != null) {
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
        float[] h = _applyAutoresizingMask(frame.x, frame.width, newParentValue.width, oldParentValue.width, mask);
        float[] v = _applyAutoresizingMask(frame.y, frame.height, newParentValue.height, oldParentValue.height, mask >> 3);
        CGRect newFrame = new CGRect(h[0], v[0], h[1], v[1]);
        if (!newFrame.equals(frame)) {
            setFrame(newFrame);
        }
    }

    private void _sizeDidChange() {
        // when the size changes, we need call the this.layoutSubview() and superview.layoutSubviews()
        setNeedsLayout();
        UIView superview = superview();
        if (superview != null) {
            superview.setNeedsLayout();
        }
    }

    private void _setSuperview(UIView newSuperview) {
        if (this.superview() == newSuperview) {
            return;
        }
        willMoveToSuperview(newSuperview);
        _superview = new WeakReference<>(newSuperview);
        setNeedsLayout();
        didMoveToSuperview();
        if (newSuperview == null) {
            _setWindow(null);
            return;
        }
        newSuperview._setAutoresizingFlagsDirty();
        if (newSuperview.window() != null) {
            _setWindow(newSuperview._window);
        }
        if (newSuperview instanceof UIWindow window) {
            _setWindow(new WeakReference<>(window));
        }
    }

    private void _setWindow(WeakReference<UIWindow> newValue) {
        if (_window == newValue) {
            return;
        }
        UIWindow oldWindow = window();
        UIWindow newWindow = null;
        if (newValue != null) {
            newWindow = newValue.get();
        }
        willMoveToWindow(newWindow);
        _window = newValue;
        _subviews.forEach(subview -> subview._setWindow(newValue));
        didMoveToWindow();
        // when a view is remove from window, the window needs additional processing.
        if (oldWindow != null && newWindow == null) {
            oldWindow._didRemoveFromWindow(this);
        }
    }

    private CGRect _remakeFrame() {
        CGRect rect = _bounds.copy();
        // when transform is anything other than the identity transform,
        // the frame is undefined and should be ignored.
        if (_transform != CGAffineTransform.IDENTITY) {
            rect.apply(_transform);
        }
        rect.x = _center.x - rect.width * 0.5f;
        rect.y = _center.y - rect.height * 0.5f;
        return rect;
    }

    private CGAffineTransform _remakeInvertedTransform() {
        if (_transform.isIdentity()) {
            return CGAffineTransform.IDENTITY;
        }
        return _transform.inverted();
    }

    @Override
    public CGAffineTransform _invertedTransform() {
        if (_invertedTransform == null) {
            _invertedTransform = _remakeInvertedTransform();
        }
        return _invertedTransform;
    }

    public Iterable<UIView> _invertedSubviews() {
        List<UIView> subviews = subviews();
        ReversedIteratorImpl<UIView> iterator = new ReversedIteratorImpl<>(subviews.listIterator(subviews.size()));
        return () -> iterator;
    }

    protected static class Flags {

        public boolean isHovered = false;
        public boolean isFocused = false;
        public boolean isUserInteractionEnabled = true;
        public boolean isDirty = false;

        public boolean needsLayout = false;
        public boolean needsAutoresizing = false;
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
