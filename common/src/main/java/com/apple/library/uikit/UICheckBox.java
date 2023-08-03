package com.apple.library.uikit;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.SoundManagerImpl;

@SuppressWarnings("unused")
public class UICheckBox extends UIControl {

    private final UILabel markerView = new UILabel(new CGRect(1, 0, 9, 9));
    private final UILabel titleView = new UILabel(CGRect.ZERO);
    private final UIImageView imageView = new UIImageView(new CGRect(0, 0, 9, 9));

    private float boxSize = 9;
    private float boxSpacing = 1;

    public UICheckBox(CGRect frame) {
        super(frame);
        this.imageView.setImage(AppearanceImpl.BUTTON_IMAGE.imageAtIndex(State.DISABLED));
        this.addSubview(imageView);
        this.markerView.setText(new NSString("x"));
        this.markerView.setTextColor(UIColor.WHITE);
        this.markerView.setTextHorizontalAlignment(NSTextAlignment.Horizontal.CENTER);
        this.addSubview(markerView);
        this.addSubview(titleView);
        this.setSelected(false);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect rect = bounds();
        CGRect frame = new CGRect(0, (rect.getHeight() - boxSize) / 2, boxSize, boxSize);
        this.titleView.setFrame(rect.insetBy(0, frame.getWidth() + boxSpacing, 0, 0));
        this.imageView.setFrame(frame);
        this.markerView.setFrame(frame.offset(0.5f, 0));
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        if (!isEnabled()) {
            return;
        }
        setSelected(!isSelected());
        sendEvent(Event.VALUE_CHANGED);
        SoundManagerImpl.click();
    }

    public NSString title() {
        return titleView.text();
    }

    public void setTitle(NSString title) {
        titleView.setText(title);
    }

    public UIColor titleColor() {
        return titleView.textColor();
    }

    public void setTitleColor(UIColor color) {
        titleView.setTextColor(color);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        if (!isEnabled) {
            titleView.setTextColor(new UIColor(0xa0a0a0));
        } else {
            titleView.setTextColor(null);
        }
    }

    @Override
    public void setSelected(boolean isSelected) {
        super.setSelected(isSelected);
        markerView.setHidden(!isSelected);
    }

    public void setBox(float boxSize, float boxSpacing) {
        this.boxSize = boxSize;
        this.boxSpacing = boxSpacing;
        this.setNeedsLayout();
    }
}
