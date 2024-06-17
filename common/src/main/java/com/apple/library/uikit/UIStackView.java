package com.apple.library.uikit;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UIStackView extends UIView {

    protected Axis _axis = Axis.HORIZONTAL;
    protected Distribution _distribution = Distribution.FILL;
    protected Alignment _alignment = Alignment.FILL;
    protected float _spacing = 0;
    protected final ArrayList<UIView> _arrangedSubviews = new ArrayList<>();

    public UIStackView(CGRect frame) {
        super(frame);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        _apply(_arrangedSubviews);
    }

    public void addArrangedSubview(UIView view) {
        addSubview(view);
        _arrangedSubviews.add(view);
        setNeedsLayout();
    }

    public void removeArrangedSubview(UIView view) {
        view.removeFromSuperview();
        _arrangedSubviews.remove(view);
        setNeedsLayout();
    }

    public void insertArrangedSubviewAtIndex(UIView view, int stackIndex) {
        insertViewAtIndex(view, stackIndex);
        _arrangedSubviews.add(stackIndex, view);
        setNeedsLayout();
    }

    public final List<UIView> arrangedSubviews() {
        return _arrangedSubviews;
    }

    public void setDistribution(Distribution distribution) {
        _distribution = distribution;
    }

    public Distribution distribution() {
        return _distribution;
    }

    public void setAlignment(Alignment alignment) {
        _alignment = alignment;
    }

    public Alignment alignment() {
        return _alignment;
    }

    public void setAxis(Axis axis) {
        _axis = axis;
    }

    public Axis axis() {
        return _axis;
    }

    public void setSpacing(float spacing) {
        _spacing = spacing;
    }

    public float spacing() {
        return _spacing;
    }

    public void setCustomSpacing(float spacing, UIView arrangedSubview) {
    }

    public float customSpacing(UIView arrangedSubview) {
        return 0;
    }

    private void _apply(List<UIView> views) {
        var bounds = _rotation(bounds().copy());
        var rects = new ArrayList<CGRect>();
        for (var view : views) {
            rects.add(_rotation(view.frame().copy()));
        }
        _applySize(bounds, rects, _distribution);
        _applyOffset(bounds, rects, _alignment);
        for (var i = 0; i < rects.size(); ++i) {
            views.get(i).setFrame(_rotation(rects.get(i)));
        }
    }

    private void _applySize(CGRect bounds, List<CGRect> rects, Distribution distribution) {
        int count = rects.size();
        if (count == 0) {
            return;
        }
        switch (distribution) {
            case FILL: {
                // TODO: @SAGESSE FIX MULTI VIEW IN FILL MODE
                _applySize(bounds, rects, Distribution.FILL_EQUALLY);
                break;
            }
            case FILL_EQUALLY: {
                var usableSize = bounds.getWidth() - _spacing * (count - 1);
                var itemLeft = 0f;
                var itemWidth = usableSize / count;
                for (var rect : rects) {
                    rect.setX(itemLeft);
                    rect.setWidth(itemWidth);
                    itemLeft += itemWidth + _spacing;
                }
                break;
            }
            case FILL_PROPORTIONALLY: {
                var usableSize = bounds.getWidth() - _spacing * (count - 1);
                var contentWidth = Math.max(_calcSize(rects).getWidth(), 1);
                var itemLeft = 0f;
                for (var rect : rects) {
                    var pro = rect.getWidth() / contentWidth;
                    var itemWidth = pro * usableSize;
                    rect.setX(itemLeft);
                    rect.setWidth(itemWidth);
                    itemLeft += itemWidth + _spacing;
                }
                break;
            }
            case EQUAL_SPACING: {
                if (count == 1) {
                    _applySize(bounds, rects, Distribution.FILL_EQUALLY);
                    break;
                }
                var contentSize = Math.max(_calcSize(rects).getWidth(), 1);
                var spacing = (bounds.getWidth() - contentSize) / (count - 1);
                var itemLeft = 0f;
                for (var rect : rects) {
                    var itemWidth = rect.getWidth();
                    rect.setX(itemLeft);
                    itemLeft += itemWidth + spacing;
                }
                break;
            }
            case EQUAL_CENTERING: {
                var centerSize = bounds.getWidth() / (count + 1);
                var itemLeft = 0f;
                for (var rect : rects) {
                    float itemWidth = rect.getWidth();
                    rect.setX(itemLeft + centerSize - itemWidth / 2);
                    itemLeft += centerSize;
                }
                break;
            }
        }
    }

    private void _applyOffset(CGRect bounds, List<CGRect> rects, Alignment alignment) {
        var count = rects.size();
        if (count == 0) {
            return;
        }
        switch (alignment) {
            case FILL: {
                var usableHeight = bounds.getHeight();
                for (var rect : rects) {
                    rect.setY(0);
                    rect.setHeight(usableHeight);
                }
                break;
            }
            case TOP:
            case LEADING: {
                for (var rect : rects) {
                    rect.setY(0);
                }
                break;
            }
            case BOTTOM:
            case TRAILING: {
                var usableHeight = bounds.getHeight();
                for (var rect : rects) {
                    rect.setY(usableHeight - rect.getHeight());
                }
                break;
            }
            case CENTER: {
                var usableHeight = bounds.getHeight();
                for (var rect : rects) {
                    rect.setY((usableHeight - rect.getHeight()) / 2);
                }
                break;
            }
            case FIRST_BASELINE:
            case LAST_BASELINE: {
                // TODO: @SAGESSE NO IMPL
                break;
            }
        }
    }

    private CGSize _calcSize(List<CGRect> rects) {
        var width = 0f;
        var height = 0f;
        for (var rect : rects) {
            width += rect.getWidth();
            height = Math.max(height, rect.getHeight());
        }
        return new CGSize(width, height);
    }

    private CGRect _rotation(CGRect rect) {
        if (_axis == Axis.VERTICAL) {
            var x = rect.getY();
            var y = rect.getX();
            var width = rect.getHeight();
            var height = rect.getWidth();
            rect.setX(x);
            rect.setY(y);
            rect.setWidth(width);
            rect.setHeight(height);
        }
        return rect;
    }


    public enum Axis {
        HORIZONTAL, VERTICAL
    }

    public enum Distribution {
        FILL, FILL_EQUALLY, FILL_PROPORTIONALLY, EQUAL_SPACING, EQUAL_CENTERING
    }

    public enum Alignment {
        FILL, LEADING, TOP, FIRST_BASELINE, CENTER, TRAILING, BOTTOM, LAST_BASELINE
    }
}
