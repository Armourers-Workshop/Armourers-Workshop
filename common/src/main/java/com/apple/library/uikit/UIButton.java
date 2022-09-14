package com.apple.library.uikit;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.impl.KeyboardManagerImpl;
import com.apple.library.impl.LayoutManagerImpl;
import com.apple.library.impl.SoundManagerImpl;
import com.apple.library.impl.StateValueImpl;

import java.util.Objects;

public class UIButton extends UIControl {

    private final StateValueImpl<NSString> titleContainer = new StateValueImpl<>();
    private final StateValueImpl<UIColor> titleColorContainer = new StateValueImpl<>();

    private final StateValueImpl<UIImage> imageContainer = new StateValueImpl<>();
    private final StateValueImpl<UIImage> backgroundImageContainer = new StateValueImpl<>();

    private final StateValueImpl<NSString> tooltipContainer = new StateValueImpl<>();

    private final UILabel titleView = new UILabel(CGRect.ZERO);
    private final UIImageView imageView = new UIImageView(CGRect.ZERO);

    private UIEdgeInsets contentEdgeInsets = UIEdgeInsets.ZERO;
    private UIEdgeInsets imageEdgeInsets = UIEdgeInsets.ZERO;
    private UIEdgeInsets titleEdgeInsets = UIEdgeInsets.ZERO;

    private NSTextAlignment.Horizontal horizontalAlignment = NSTextAlignment.Horizontal.CENTER;
    private NSTextAlignment.Vertical verticalAlignment = NSTextAlignment.Vertical.CENTER;

    private CGRect cachedIconRect;
    private CGRect cachedTitleRect;
    private NSString cachedCurrentTitle;
    private UIImage cachedCurrentImage;

    private boolean shouldPassHighlighted = true;
    private boolean canBecomeFocused = true;

    public UIButton(CGRect frame) {
        super(frame);
        this.titleView.setShadowColor(UIColor.GRAY);
        this.titleView.setHidden(true);
        this.imageView.setHidden(true);
        this.addSubview(titleView);
        this.addSubview(imageView);
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        SoundManagerImpl.click();
    }

    @Override
    public void keyDown(UIEvent event) {
        super.keyDown(event);
        // simulate a mouse press.
        if (isFocused() && isDownKey(event.key)) {
            sendEvent(Event.MOUSE_LEFT_DOWN);
            SoundManagerImpl.click();
        }
    }

    @Override
    public void focusesDidChange() {
        super.focusesDidChange();
        updateStateIfNeeded();
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        remakeContentLayoutIfNeeded(context.font);
        context.drawImage(backgroundImageContainer.currentValue(), bounds());
    }

    public UIImage image(int state) {
        return imageContainer.valueForState(state);
    }

    public void setImage(UIImage image, int state) {
        imageContainer.setValueForState(image, state);
        applyImageToAnother(imageContainer, image, state);
        updateStateIfNeeded();
    }

    public UIImage backgroundImage(int state) {
        return backgroundImageContainer.valueForState(state);
    }

    public void setBackgroundImage(UIImage image, int state) {
        backgroundImageContainer.setValueForState(image, state);
        applyImageToAnother(backgroundImageContainer, image, state);
        updateStateIfNeeded();
    }

    public NSString title(int state) {
        return titleContainer.valueForState(state);
    }

    public void setTitle(NSString title, int state) {
        titleContainer.setValueForState(title, state);
        updateStateIfNeeded();
    }

    public UIColor titleColor(int state) {
        return titleColorContainer.valueForState(state);
    }

    public void setTitleColor(UIColor textColor, int state) {
        titleColorContainer.setValueForState(textColor, state);
        updateStateIfNeeded();
    }

    public NSString tooltip(int state) {
        return tooltipContainer.valueForState(state);
    }

    public void setTooltip(NSString tooltip, int state) {
        tooltipContainer.setValueForState(tooltip, state);
    }

    public UIEdgeInsets imageEdgeInsets() {
        return imageEdgeInsets;
    }

    public void setImageEdgeInsets(UIEdgeInsets imageEdgeInsets) {
        this.imageEdgeInsets = imageEdgeInsets;
        this.cachedIconRect = null;
    }

    public UIEdgeInsets titleEdgeInsets() {
        return titleEdgeInsets;
    }

    public void setTitleEdgeInsets(UIEdgeInsets titleEdgeInsets) {
        this.titleEdgeInsets = titleEdgeInsets;
        this.cachedIconRect = null;
    }

    public UIEdgeInsets contentEdgeInsets() {
        return contentEdgeInsets;
    }

    public void setContentEdgeInsets(UIEdgeInsets contentEdgeInsets) {
        this.contentEdgeInsets = contentEdgeInsets;
        this.cachedIconRect = null;
    }

    public NSTextAlignment.Horizontal horizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(NSTextAlignment.Horizontal horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        this.setNeedsRemakeLayouts();
    }

    public NSTextAlignment.Vertical verticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(NSTextAlignment.Vertical verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        this.setNeedsRemakeLayouts();
    }

    public UILabel titleView() {
        return titleView;
    }

    public UIImageView imageView() {
        return imageView;
    }

    @Override
    public boolean canBecomeFocused() {
        return !_ignoresTouchEvents(this) && isEnabled() && canBecomeFocused;
    }

    public void setCanBecomeFocused(boolean canBecomeFocused) {
        this.canBecomeFocused = canBecomeFocused;
    }

    @Override
    public boolean shouldPassHighlighted() {
        return shouldPassHighlighted;
    }

    public void setShouldPassHighlighted(boolean shouldPassHighlighted) {
        this.shouldPassHighlighted = shouldPassHighlighted;
    }

    private void setNeedsRemakeLayouts() {
        cachedTitleRect = null;
        cachedIconRect = null;
    }

    private void remakeContentLayoutIfNeeded(UIFont font) {
        if (cachedIconRect != null && cachedTitleRect != null) {
            return;
        }
        LayoutManagerImpl manager = new LayoutManagerImpl();
        manager.add(imageSize(imageContainer.currentValue()), imageEdgeInsets, size -> new CGRect(CGPoint.ZERO, size));
        manager.add(titleContainer.currentValue(), titleEdgeInsets, text -> text.boundingRectWithFont(font).offset(0, 1));
        manager.applyHorizontalLayout(bounds().insetBy(contentEdgeInsets), horizontalAlignment, verticalAlignment);
        cachedIconRect = manager.getOrDefault(0, CGRect.ZERO);
        if (cachedIconRect != null) {
            imageView.setFrame(cachedIconRect);
        }
        cachedTitleRect = manager.getOrDefault(1, CGRect.ZERO);
        if (cachedTitleRect != null) {
            titleView.setFrame(cachedTitleRect);
        }
    }

    @Override
    protected void updateStateIfNeeded() {
        int state = State.NORMAL;
        if (isSelected()) {
            state |= State.SELECTED;
        }
        if (isHighlighted()) {
            state |= State.HIGHLIGHTED;
        }
        if (isFocused()) {
            // highlight and focused used same state.
            state |= State.HIGHLIGHTED;
        }
        if (!isEnabled()) {
            state |= State.DISABLED;
        }
        imageContainer.setCurrentState(state);
        backgroundImageContainer.setCurrentState(state);
        titleContainer.setCurrentState(state);
        titleColorContainer.setCurrentState(state);
        tooltipContainer.setCurrentState(state);
        // when image or title is changed, reload cache
        NSString currentTitle = titleContainer.currentValue();
        titleView.setTextColor(titleColorContainer.currentValue());
        if (!Objects.equals(currentTitle, cachedCurrentTitle)) {
            titleView.setText(currentTitle);
            titleView.setHidden(currentTitle == null);
            setNeedsRemakeLayouts();
        }
        UIImage currentImage = imageContainer.currentValue();
        imageView.setImage(currentImage);
        if (!Objects.equals(imageSize(currentImage), imageSize(cachedCurrentImage))) {
            imageView.setHidden(currentImage == null);
            setNeedsRemakeLayouts();
        }
        // apply status required setup once with setTooltip(NSString, int).
        if (!tooltipContainer.isEmpty()) {
            setTooltip(tooltipContainer.currentValue());
        }
        cachedCurrentTitle = currentTitle;
        cachedCurrentImage = currentImage;
    }

    private CGSize imageSize(UIImage image) {
        if (image != null) {
            return image.size();
        }
        return null;
    }

    private boolean isDownKey(int key) {
        return KeyboardManagerImpl.isEnter(key) || KeyboardManagerImpl.isSpace(key);
    }

    private void applyImageToAnother(StateValueImpl<UIImage> container, UIImage image, int state) {
        //
        if (state != State.ALL || image == null || !image.isPacked()) {
            return;
        }
        for (int nextState = State.NORMAL; nextState < State.ALL; ++nextState) {
            UIImage image1 = image.imageAtIndex(nextState);
            if (image1 != image) {
                container.setValueForState(image1, nextState);
            }
        }
    }
}
