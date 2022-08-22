package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.KeyboardManagerImpl;

import java.util.function.Function;

public class UISliderBox extends UIControl {

    private final UIButton leftView = getShareStateButton();
    private final UIButton rightView = getShareStateButton();
    private final UIButton middleView = new UIButton(new CGRect(0, 0, 8, 8));

    private final UIButton contentView = getContentButton();

    private double value = 0;
    private double maxValue = 1;
    private double minValue = -1;
    private double stepValue = 1;

    private boolean isSmall = false;
    private boolean isEditing = false;

    private CGRect cachedBounds = CGRect.ZERO;
    private Function<Double, NSString> formatter;

    public UISliderBox(CGRect frame) {
        super(frame);
        this.setup();
    }

    private void setup() {
        contentView.setBackgroundImage(AppearanceImpl.BUTTON_IMAGE.imageAtIndex(State.DISABLED), State.ALL);
        contentView.setTitleColor(AppearanceImpl.SLIDER_TEXT_COLOR, State.NORMAL);
        contentView.setTitleColor(AppearanceImpl.SLIDER_HIGHLIGHTED_TEXT_COLOR, State.HIGHLIGHTED);
        contentView.setTitleColor(AppearanceImpl.SLIDER_HIGHLIGHTED_TEXT_COLOR, State.SELECTED);
        addSubview(contentView);

        leftView.setBackgroundImage(AppearanceImpl.BUTTON_IMAGE, State.ALL);
        leftView.addTarget(this, Event.MOUSE_LEFT_DOWN, UISliderBox::updateValueAction);
        addSubview(leftView);

        middleView.setBackgroundImage(AppearanceImpl.BUTTON_IMAGE, State.ALL);
        middleView.setUserInteractionEnabled(false);
        contentView.insertViewAtIndex(middleView, 0);

        rightView.setBackgroundImage(AppearanceImpl.BUTTON_IMAGE, State.ALL);
        rightView.addTarget(this, Event.MOUSE_LEFT_DOWN, UISliderBox::updateValueAction);
        addSubview(rightView);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect rect = bounds();
        if (rect.equals(cachedBounds)) {
            return;
        }
        int width = 9;
        int spacing = 1;
        if (!isSmall) {
            leftView.setFrame(new CGRect(0, 0, width, rect.height));
            rightView.setFrame(new CGRect(rect.width - width, 0, width, rect.height));
            contentView.setFrame(new CGRect(width + spacing, 0, rect.width - (width + spacing) * 2, rect.height));
        } else {
            contentView.setFrame(rect);
        }
        middleView.setFrame(getCursorRect());
        cachedBounds = rect;
    }

    public Function<Double, NSString> formatter() {
        return formatter;
    }

    public void setFormatter(Function<Double, NSString> formatter) {
        this.formatter = formatter;
        this.valueDidChange();
    }

    public double value() {
        return value;
    }

    public void setValue(double value) {
        this.value = clampValue(value);
        this.valueDidChange();
    }

    public double stepValue() {
        return stepValue;
    }

    public void setStepValue(double value) {
        this.stepValue = value;
    }

    public double minValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double maxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isSmall() {
        return isSmall;
    }

    public void setSmall(boolean small) {
        isSmall = small;
        leftView.setHidden(isSmall);
        rightView.setHidden(isSmall);
        setNeedsLayout();
    }

    @Override
    protected boolean shouldPassHighlighted() {
        return false;
    }

    private void beginEditing() {
        isEditing = true;
        sendEvent(Event.EDITING_DID_BEGIN);
        updateHighlightedIfNeeded();
    }

    private void endEditing() {
        isEditing = false;
        sendEvent(Event.EDITING_DID_END);
        updateHighlightedIfNeeded();
    }

    private void updateValueAction(UIControl control) {
        double value = stepValue;
        if (control == leftView) {
            value = -stepValue;
        }
        beginEditing();
        updateValue(getResolvedValue(value));
        endEditing();
    }

    private void updateValueWithEvent(UIEvent event) {
        CGPoint point = contentView.convertPointFromView(event.locationInWindow(), null);
        double value = point.x / (double)contentView.bounds().width;
        if (Math.abs(value - 0.5) < 0.01) {
            value = 0.5; // attract to mid value.
        }
        double resolvedValue = value * (maxValue - minValue);
        resolvedValue = (int) (resolvedValue / stepValue) * stepValue;
        updateValue(minValue + resolvedValue);
    }

    private void updateValue(double value) {
        setValue(value);
        valueDidChange();
        sendEvent(Event.VALUE_CHANGED);
    }

    private void updateHighlightedIfNeeded() {
        middleView.setHighlighted(isEditing || contentView.isHighlighted());
        contentView.setSelected(isEditing || leftView.isHighlighted() || rightView.isHighlighted());
    }

    private void valueDidChange() {
        if (formatter != null) {
            contentView.setTitle(formatter.apply(value), State.NORMAL);
        }
        middleView.setFrame(getCursorRect());
    }

    private double clampValue(double value) {
        return Math.max(Math.min(value, maxValue), minValue);
    }

    private double getResolvedValue(double inc) {
        double modifier;
        if (KeyboardManagerImpl.hasShiftDown()) {
            modifier = KeyboardManagerImpl.hasControlDown() ? 0.01 : 0.1;
        } else {
            modifier = KeyboardManagerImpl.hasControlDown() ? 10.0 : 1.0;
        }
        double newValue = value + inc * modifier;
        if (KeyboardManagerImpl.hasAltDown()) {
            newValue = (int) (newValue / modifier) * modifier; // align to modifier
        }
        return newValue;
    }

    private CGRect getCursorRect() {
        CGRect rect = contentView.bounds();
        int width = rect.width;
        int height = rect.height;
        int valueWidth = middleView.frame().width;
        double progress = (value - minValue) / (maxValue - minValue);
        int x = (int)((width - valueWidth) * progress);
        return new CGRect(x, 0, valueWidth, height);
    }

    private UIButton getShareStateButton() {
        return new UIButton(CGRect.ZERO) {
            @Override
            public void setHighlighted(boolean highlighted) {
                super.setHighlighted(highlighted);
                updateHighlightedIfNeeded();
            }
        };
    }

    private UIButton getContentButton() {
        return new UIButton(CGRect.ZERO) {
            @Override
            public void setHighlighted(boolean highlighted) {
                super.setHighlighted(highlighted);
                updateHighlightedIfNeeded();
            }

            @Override
            public void mouseDown(UIEvent event) {
                beginEditing();
                updateValueWithEvent(event);
                nextResponder().mouseDown(event);
            }

            @Override
            public void mouseDragged(UIEvent event) {
                updateValueWithEvent(event);
                nextResponder().mouseDragged(event);
            }

            @Override
            public void mouseUp(UIEvent event) {
                updateValueWithEvent(event);
                endEditing();
                nextResponder().mouseUp(event);
            }
        };
    }
}
