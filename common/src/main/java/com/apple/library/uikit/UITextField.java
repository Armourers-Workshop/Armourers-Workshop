package com.apple.library.uikit;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSRange;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextPosition;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.DelegateImpl;
import com.apple.library.impl.TextInputImpl;
import com.apple.library.impl.TextInputTraits;
import com.apple.library.impl.TextStorageImpl;

@SuppressWarnings("unused")
public class UITextField extends UIControl implements TextInputTraits {

    private final TextStorageImpl storage = new TextStorageImpl();
    private final TextInputImpl input = new TextInputImpl(storage);
    private final DelegateImpl<UITextFieldDelegate> delegate = DelegateImpl.of(new UITextFieldDelegate() {
        // auto stub
    });

    private UIEdgeInsets contentInsets = new UIEdgeInsets(2, 4, 2, 4);

    private boolean isBordered = true;

    public UITextField(CGRect frame) {
        super(frame);
        this.storage.setBoundingSize(CGSize.ZERO);
        this.storage.setLineSpacing(1);
        this.storage.maxLength = 32;
        this.storage.sizeDidChange = this::sizeDidChange;
        this.storage.selectionDidChange = this::selectionDidChange;
        this.storage.valueDidChange = this::valueDidChange;
        this.storage.valueShouldChange = this::valueShouldChange;
        this.input.returnHandler = this::shouldReturn;
    }

    @Override
    public void mouseDown(UIEvent event) {
        if (!storage.isFocused()) {
            becomeFirstResponder();
        }
        if (shouldInputText()) {
            input.mouseDown(event.locationInView(this));
        }
    }

    @Override
    public void keyDown(UIEvent event) {
        if (shouldInputText() && input.keyDown(event.key)) {
            return;
        }
        super.keyDown(event);
    }

    @Override
    public void charTyped(UIEvent event) {
        if (shouldInputText() && input.charTyped((char) event.key)) {
            return;
        }
        super.charTyped(event);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        CGRect bounds = bounds();
        CGRect fixedBounds = bounds.insetBy(1, 1, 1, 1);
        if (isBordered) {
            context.fillRect(getBorderColor(), bounds);
            context.fillRect(getFillColor(), fixedBounds);
        }
        context.addClipRect(UIScreen.convertRectFromView(fixedBounds, this));
        storage.render(point, context);
        context.removeClipRect();
    }

    public boolean isEditable() {
        return input.isEditable();
    }

    public void setEditable(boolean isEditable) {
        input.setEditable(isEditable);
    }

    public UIFont font() {
        return storage.font();
    }

    public void setFont(UIFont font) {
        storage.setFont(font);
    }

    public NSString placeholder() {
        return storage.placeholder();
    }

    public void setPlaceholder(NSString placeholder) {
        storage.setPlaceholder(placeholder);
    }

    public UIColor placeholderColor() {
        return storage.placeholderColor();
    }

    public void setPlaceholderColor(UIColor placeholderColor) {
        storage.setPlaceholderColor(placeholderColor);
    }

    public String value() {
        return storage.value();
    }

    public void setValue(String text) {
        storage.setValue(text);
        storage.checkCursorAndHighlightPos();
    }

    public UIColor textColor() {
        return storage.textColor();
    }

    public void setTextColor(UIColor textColor) {
        storage.setTextColor(textColor);
    }

    public void setCursorPos(NSTextPosition pos) {
        storage.setCursorAndHighlightPos(pos);
    }

    public UITextFieldDelegate delegate() {
        return delegate.get();
    }

    public void setDelegate(UITextFieldDelegate delegate) {
        this.delegate.set(delegate);
    }

    public boolean isEditing() {
        return storage.isFocused();
    }

    public void becomeFirstResponder() {
        if (storage.isFocused()) {
            return;
        }
        UIWindow window = window();
        if (window != null) {
            if (!delegate.invoker().textFieldShouldBeginEditing(this)) {
                return;
            }
            window.setFirstInputResponder(this);
            storage.setFocused(true);
            sendEvent(Event.EDITING_DID_BEGIN);
            delegate.invoker().textFieldDidBeginEditing(this);
        }
    }

    public void resignFirstResponder() {
        if (!storage.isFocused()) {
            return;
        }
        if (!delegate.invoker().textFieldShouldEndEditing(this)) {
            return;
        }
        UIWindow window = window();
        if (window != null) {
            window.setFirstInputResponder(null);
        }
        storage.setFocused(false);
        sendEvent(Event.EDITING_DID_END);
        delegate.invoker().textFieldDidEndEditing(this);
    }

    public NSTextPosition beginOfDocument() {
        return storage.beginOfDocument();
    }

    public NSTextPosition endOfDocument() {
        return storage.endOfDocument();
    }

    public int maxLength() {
        return storage.maxLength;
    }

    public void setMaxLength(int maxLength) {
        storage.maxLength = maxLength;
    }

    public UIEdgeInsets contentInsets() {
        return contentInsets;
    }

    public void setContentInsets(UIEdgeInsets contentInsets) {
        this.contentInsets = contentInsets;
    }

    @Override
    public boolean canBecomeFocused() {
        return !_ignoresTouchEvents(this) && input.isEditable();
    }

    private boolean shouldReturn(String value) {
        return delegate.invoker().textFieldShouldReturn(this);
    }

    private boolean valueShouldChange(NSRange range, String newValue) {
        return delegate.invoker().textFieldShouldChangeCharacters(this, range, newValue);
    }

    private void valueDidChange(String oldValue, String newValue) {
        sendEvent(Event.VALUE_CHANGED);
    }

    private void selectionDidChange() {
        delegate.invoker().textFieldDidChangeSelection(this);
    }

    private void sizeDidChange(CGRect rect, CGSize size) {
        CGRect bounds = bounds().insetBy(contentInsets);
        CGRect cursorRect = rect.insetBy(0, 0, 0, -5);
        float offsetX = storage.offset.x;
        float offsetY = (bounds.height - size.height) / 2;
        float contentWidth = size.width + cursorRect.width;
        CGRect visibleRect = bounds.offset(-offsetX, 0);
        // 1. when the cursor pos not in the visible rect.
        // 2. when the display width exceeds the actual width.
        if (visibleRect.getMaxX() > contentWidth || !isSameRange(visibleRect, cursorRect)) {
            float x1 = Math.min(cursorRect.x + visibleRect.width / 2, contentWidth);
            float x0 = Math.max(x1 - visibleRect.width, 0);
            offsetX = contentInsets.left - x0;
        }
        storage.offset = new CGPoint(offsetX, contentInsets.top + offsetY + 1);
    }

    private boolean isSameRange(CGRect rect, CGRect other) {
        return rect.getMinX() <= other.getMinX() && other.getMaxX() <= rect.getMaxX();
    }

    private UIColor getFillColor() {
        return AppearanceImpl.TEXT_BACKGROUND_COLOR;
    }

    private UIColor getBorderColor() {
        if (storage.isFocused()) {
            return AppearanceImpl.TEXT_FOCUSED_BORDER_COLOR;
        }
        return AppearanceImpl.TEXT_BORDER_COLOR;
    }

    private boolean shouldInputText() {
        return !isHidden() && storage.isFocused() && isUserInteractionEnabled() && isEditable();
    }
}
